package simplexity.simpleteleportrequests.logic;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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
                teleportingPlayer,
                targetPlayer,
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
            Player targetPlayer = tpRequest.getTargetPlayer();
            Player teleportingPlayer = tpRequest.getTeleportingPlayer();
            if (targetPlayer.isOnline()) {
                targetPlayer.sendRichMessage(
                        Message.REQUEST_TIMED_OUT_DESTINATION_PLAYER.getMessage(),
                        Placeholder.component("player", teleportingPlayer.displayName())
                );
            }
            if (teleportingPlayer.isOnline()) {
                teleportingPlayer.sendRichMessage(
                        Message.REQUEST_TIMED_OUT_TELEPORTING_PLAYER.getMessage(),
                        Placeholder.component("player", targetPlayer.displayName())
                );
            }
            removePlayersFromMaps(teleportingPlayer, targetPlayer);
        }, timeoutLengthTicks);
        expireTasks.put(tpRequest, timeOutTask);
    }


    public void removePlayersFromMaps(Player teleportingPlayer, Player targetPlayer) {
        UUID teleportingPlayerUuid = teleportingPlayer.getUniqueId();
        UUID targetPlayerUuid = targetPlayer.getUniqueId();
        HashMap<UUID, TeleportRequest> teleportingMap = activeTeleportRequests.get(teleportingPlayerUuid);
        if (teleportingMap != null) {
            for (TeleportRequest request : teleportingMap.values()) {
                if (request.getTargetPlayer().equals(targetPlayer)) {
                    teleportingMap.remove(teleportingPlayerUuid, request);
                }
            }
            if (teleportingMap.isEmpty()) {
                activeTeleportRequests.remove(targetPlayerUuid);
            } else {
                activeTeleportRequests.put(teleportingPlayerUuid, teleportingMap);
            }
        }
        HashMap<UUID, TeleportRequest> targetMap = activeTeleportRequests.get(targetPlayerUuid);
        if (targetMap != null) {
            for (TeleportRequest request : targetMap.values()) {
                if (request.getTeleportingPlayer().equals(teleportingPlayer)) {
                    targetMap.remove(targetPlayerUuid, request);
                }
            }
            if (targetMap.isEmpty()) {
                activeTeleportRequests.remove(targetPlayerUuid);
            } else {
                activeTeleportRequests.put(targetPlayerUuid, targetMap);
            }
        }

    }

    public TeleportRequest getTeleportRequest(@NotNull Player player) {
        UUID uuid = player.getUniqueId();
        if (!activeTeleportRequests.containsKey(uuid)) return null;
        HashMap<UUID, TeleportRequest> tpRequests = activeTeleportRequests.get(uuid);
        if (tpRequests == null || tpRequests.isEmpty()) return null;
        TeleportRequest requestToReturn = null;
        for (TeleportRequest request : tpRequests.values()) {
            if (request.hasRequestExpired()) {
                tpRequests.remove(uuid, request);
                continue;
            }
            if (request.didTargetPlayerInitiate() && request.getTargetPlayer().equals(player)) {
                continue;
            }
            if (!request.didTargetPlayerInitiate() && request.getTeleportingPlayer().equals(player)) {
                continue;
            }
            if (requestToReturn == null) {
                requestToReturn = request;
                continue;
            }
            if (requestToReturn.getRequestTimeSysMil() < request.getRequestTimeSysMil()) continue;
            requestToReturn = request;
        }
        activeTeleportRequests.put(uuid, tpRequests);
        return requestToReturn;
    }

    public void removeUpcomingTask(TeleportRequest tpRequest) {
        if (!expireTasks.containsKey(tpRequest)) return;
        expireTasks.get(tpRequest).cancel();
        expireTasks.remove(tpRequest);
    }

}
