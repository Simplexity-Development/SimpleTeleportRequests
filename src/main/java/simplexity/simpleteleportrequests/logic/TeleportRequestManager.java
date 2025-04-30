package simplexity.simpleteleportrequests.logic;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
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

    // TPAHERE requests, regardless of if it's /tpa <user> or /tpahere <multiple>
    public final HashMap<UUID, HashMap<UUID, TeleportRequest>> incomingTeleportRequests = new HashMap<>();
    // TPA requests
    public final HashMap<UUID, TeleportRequest> outgoingTeleportRequest = new HashMap<>();
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
        handleTpMapping(teleportingPlayer, targetPlayer, location, tpHereRequest);
        return true;

    }

    private Boolean handleTpMapping(Player teleportingPlayer, Player targetPlayer, Location teleportLocation, boolean tpHereReq) {
        UUID teleportingPlayerUuid = teleportingPlayer.getUniqueId();
        UUID targetPlayerUuid = targetPlayer.getUniqueId();
        TeleportRequest tpRequest = new TeleportRequest(
                teleportingPlayerUuid,
                targetPlayerUuid,
                false,
                ConfigHandler.getInstance().getRequestTimeoutInSeconds(),
                teleportLocation);
        Boolean addedSuccessfully = addPlayersAndRequestsToMaps(tpRequest);
        if (addedSuccessfully == null) {
            return null;
        }
        if (!addedSuccessfully) {
            return false;
        }
        teleportTimeOutTask(tpRequest);
        return true;
    }


    private void teleportTimeOutTask(TeleportRequest tpRequest) {
        long timeoutLengthTicks = ConfigHandler.getInstance().getRequestTimeoutInSeconds() * 20L;
        BukkitTask timeOutTask = Bukkit.getScheduler().runTaskLater(SimpleTeleportRequests.getInstance(), () -> {
            if (tpRequest == null) return;
            OfflinePlayer destinationOfflinePlayer = Bukkit.getOfflinePlayer(tpRequest.getDestinationPlayerUuid());
            OfflinePlayer teleportingOfflinePlayer = Bukkit.getOfflinePlayer(tpRequest.getTeleportingPlayerUuid());
            if (destinationOfflinePlayer instanceof Player destinationPlayer) {
                destinationPlayer.sendRichMessage(
                        Message.REQUEST_TIMED_OUT_DESTINATION_PLAYER.getMessage(),
                        Placeholder.parsed("player", teleportingOfflinePlayer.getName())
                );
            }
            if (teleportingOfflinePlayer instanceof Player teleportingPlayer) {
                teleportingPlayer.sendRichMessage(
                        Message.REQUEST_TIMED_OUT_TELEPORTING_PLAYER.getMessage(),
                        Placeholder.parsed("player", destinationOfflinePlayer.getName())
                );
            }
            removePlayersFromMaps(tpRequest.getTeleportingPlayerUuid(), tpRequest.getDestinationPlayerUuid());
        }, timeoutLengthTicks);
        expireTasks.put(tpRequest, timeOutTask);
    }

    public Boolean addPlayersAndRequestsToMaps(TeleportRequest tpRequest){
        UUID teleportingPlayerUuid = tpRequest.getTeleportingPlayerUuid();
        UUID targetPlayerUuid = tpRequest.getDestinationPlayerUuid();
        Player teleportingPlayer = Bukkit.getPlayer(teleportingPlayerUuid);
        Player targetPlayer = Bukkit.getPlayer(targetPlayerUuid);
        boolean tpHere = tpRequest.isTpHereRequest();
        if (teleportingPlayer == null) {
            return null;
        }
        if (targetPlayer == null) {
            return null;
        }
        HashMap<UUID, TeleportRequest> currentIncomingRequests = incomingTeleportRequests.get(targetPlayerUuid);
        if (!tpHere) {
            if (outgoingTeleportRequest.containsKey(teleportingPlayerUuid)) {
                teleportingPlayer.sendRichMessage(Message.TELEPORT_REQUEST_ALREADY_PENDING.getMessage());
                return false;
            }
            if (currentIncomingRequests.containsKey(teleportingPlayerUuid)) {
                teleportingPlayer.sendRichMessage(Message.TELEPORT_REQUEST_SENT_BY_TARGET.getMessage(),
                        Placeholder.component("player", targetPlayer.displayName()));
                return false;
            }
            outgoingTeleportRequest.put(teleportingPlayerUuid, tpRequest);
            //todo check if blocked
        }
        currentIncomingRequests.put(targetPlayerUuid, tpRequest);
        incomingTeleportRequests.put(targetPlayerUuid, currentIncomingRequests);
        return true;
    }

    public void removePlayersFromMaps(UUID teleportingPlayerUuid, UUID targetPlayerUuid) {
        outgoingTeleportRequest.remove(teleportingPlayerUuid);
        HashMap<UUID, TeleportRequest> requests = incomingTeleportRequests.get(targetPlayerUuid);
        requests.remove(teleportingPlayerUuid);
        incomingTeleportRequests.put(teleportingPlayerUuid, requests);
    }

    public TeleportRequest getTeleportRequest(UUID player1uuid, UUID player2uuid) {
        if (playerHasIncomingRequest(player1uuid, player2uuid)) {
            return incomingTeleportRequests.get(player1uuid).get(player2uuid);
        }
        if (playerHasIncomingRequest(player2uuid, player1uuid)) {
            return incomingTeleportRequests.get(player2uuid).get(player1uuid);
        }
        return null;
    }

    private boolean playerHasIncomingRequest(UUID player1uuid, UUID player2uuid){
        if (!incomingTeleportRequests.containsKey(player1uuid)) return false;
        return incomingTeleportRequests.get(player1uuid).containsKey(player2uuid);
    }


}
