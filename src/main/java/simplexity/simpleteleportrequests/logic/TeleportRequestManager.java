package simplexity.simpleteleportrequests.logic;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import simplexity.simpleteleportrequests.SimpleTeleportRequests;
import simplexity.simpleteleportrequests.config.ConfigHandler;
import simplexity.simpleteleportrequests.config.Message;
import simplexity.simpleteleportrequests.objects.TeleportRequest;
import simplexity.simpleteleportrequests.safety.SafetyCheck;

import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

public class TeleportRequestManager {
    private static TeleportRequestManager instance;

    public TeleportRequestManager() {
    }

    public static TeleportRequestManager getInstance() {
        if (instance == null) instance = new TeleportRequestManager();
        return instance;
    }

    public final HashMap<UUID, HashMap<UUID, TeleportRequest>> activeTeleportRequests = new HashMap<>();
    private final HashMap<TeleportRequest, BukkitTask> expireTasks = new HashMap<>();

    public Boolean sendRequest(Player teleportingPlayer, Player targetPlayer, boolean tpHereRequest, boolean safetyOverride) {
        Location location = targetPlayer.getLocation();
        int safetyFlags = SafetyCheck.checkSafetyFlags(location, teleportingPlayer);
        if (safetyFlags != 0 && !safetyOverride) {
            //todo aaaaa
            teleportingPlayer.sendRichMessage("unsafe");
            targetPlayer.sendRichMessage("unsafe");
            return false;
        }
        handleTeleportRequestCreation(teleportingPlayer, targetPlayer, location, tpHereRequest);
        return true;

    }


    /**
     * 0 = successful,
     * 1 = teleporting player's requests already have the target player
     * 2 = target player's requests already have the teleporting player
     * 3 = teleporting player is blocked
     * 4 = target player is blocked
     */
    public int handleTeleportRequestCreation(Player teleportingPlayer, Player targetPlayer, Location teleportLocation, boolean targetInitiated) {
        UUID teleportingPlayerUuid = teleportingPlayer.getUniqueId();
        UUID targetPlayerUuid = targetPlayer.getUniqueId();
        TeleportRequest tpRequest = new TeleportRequest(
                teleportingPlayerUuid,
                targetPlayerUuid,
                targetInitiated,
                ConfigHandler.getInstance().getRequestTimeoutInSeconds(),
                teleportLocation);
        HashMap<UUID, TeleportRequest> targetPlayersRequests = activeTeleportRequests.get(targetPlayerUuid);
        if (targetPlayersRequests == null) {
            targetPlayersRequests = new HashMap<>();
        }
        HashMap<UUID, TeleportRequest> teleportingPlayersRequests = activeTeleportRequests.get(teleportingPlayerUuid);
        if (teleportingPlayersRequests == null) {
            teleportingPlayersRequests = new HashMap<>();
        }
        if (teleportingPlayersRequests.containsKey(targetPlayerUuid)) {
            if (targetInitiated && teleportingPlayersRequests.get(targetPlayerUuid).didTargetPlayerInitiate()) {
                return 3;
            }
        }
        if (targetPlayersRequests.containsKey(teleportingPlayerUuid)) {
            if (targetInitiated && targetPlayersRequests.get(teleportingPlayerUuid).didTargetPlayerInitiate()) {
                return 4;
            }
        }
        //todo check if blocked
        targetPlayersRequests.put(targetPlayerUuid, tpRequest);
        activeTeleportRequests.put(targetPlayerUuid, targetPlayersRequests);
        teleportTimeOutTask(tpRequest);
        return 0;
    }


    private void teleportTimeOutTask(TeleportRequest tpRequest) {
        long timeoutLengthTicks = ConfigHandler.getInstance().getRequestTimeoutInSeconds() * 20L;
        BukkitTask timeOutTask = Bukkit.getScheduler().runTaskLater(SimpleTeleportRequests.getInstance(), () -> {
            if (tpRequest == null) return;
            OfflinePlayer destinationOfflinePlayer = Bukkit.getOfflinePlayer(tpRequest.getTargetPlayerUuid());
            OfflinePlayer teleportingOfflinePlayer = Bukkit.getOfflinePlayer(tpRequest.getTeleportingPlayerUuid());
            String destinationPlayerName = destinationOfflinePlayer.getName();
            String teleportingPlayerName = teleportingOfflinePlayer.getName();
            if (destinationPlayerName == null) {
                destinationPlayerName = Message.OFFLINE_PLAYER_NAME_NOT_FOUND.getMessage();
            }
            if (teleportingPlayerName == null) {
                teleportingPlayerName = Message.OFFLINE_PLAYER_NAME_NOT_FOUND.getMessage();
            }
            if (destinationOfflinePlayer instanceof Player destinationPlayer) {
                destinationPlayer.sendRichMessage(
                        Message.REQUEST_TIMED_OUT_DESTINATION_PLAYER.getMessage(),
                        Placeholder.parsed("player", teleportingPlayerName)
                );
            }
            if (teleportingOfflinePlayer instanceof Player teleportingPlayer) {
                teleportingPlayer.sendRichMessage(
                        Message.REQUEST_TIMED_OUT_TELEPORTING_PLAYER.getMessage(),
                        Placeholder.parsed("player", destinationPlayerName)
                );
            }
            removePlayersFromMaps(tpRequest.getTeleportingPlayerUuid(), tpRequest.getTargetPlayerUuid());
        }, timeoutLengthTicks);
        expireTasks.put(tpRequest, timeOutTask);
    }


    public void removePlayersFromMaps(UUID teleportingPlayerUuid, UUID targetPlayerUuid) {
        HashMap<UUID, TeleportRequest> requests = activeTeleportRequests.get(targetPlayerUuid);
        if (requests == null) return;
        requests.remove(teleportingPlayerUuid);
        activeTeleportRequests.put(teleportingPlayerUuid, requests);
    }

    /**
     * Returns the oldest teleport request that the user has
     *
     * @param uuid tpaccept user
     * @return TeleportRequest
     */
    public TeleportRequest getTeleportRequest(@NotNull UUID uuid) {
        if (!activeTeleportRequests.containsKey(uuid)) return null;
        HashMap<UUID, TeleportRequest> tpRequests = activeTeleportRequests.get(uuid);
        if (tpRequests == null || tpRequests.isEmpty()) return null;
        TeleportRequest requestToReturn = null;
        Logger logger = SimpleTeleportRequests.getInstance().getLogger();
        for (TeleportRequest request : tpRequests.values()) {
            logger.info(request.toString());
            if (request.didTargetPlayerInitiate()
                && request.getTargetPlayerUuid().equals(uuid)) {
                logger.info("Target Player initiated, uuid provided is the same as the target player");
                continue;
            }
            if (!request.didTargetPlayerInitiate()
                && request.getTeleportingPlayerUuid().equals(uuid)) {
                logger.info("Teleporting player initiated, uuid provided is the teleporting player");
                continue;
            }
            if (requestToReturn == null) {
                requestToReturn = request;
                continue;
            }
            if (requestToReturn.getRequestTimeSysMil() < request.getRequestTimeSysMil()) continue;
            requestToReturn = request;
        }
        return requestToReturn;
    }

    public void removeUpcomingTask(TeleportRequest tpRequest) {
        if (!expireTasks.containsKey(tpRequest)) return;
        expireTasks.get(tpRequest).cancel();
        expireTasks.remove(tpRequest);
    }

}
