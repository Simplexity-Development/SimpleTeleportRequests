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

    public boolean sendRequest(Player teleportingPlayer, Player targetPlayer, boolean tpHereRequest, boolean safetyOverride) {
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
    public int handleTeleportRequestCreation(Player teleportingPlayer, Player targetPlayer, Location teleportLocation, boolean tpahere) {
        UUID teleportingUuid = teleportingPlayer.getUniqueId();
        UUID targetUuid = targetPlayer.getUniqueId();
        TeleportRequest tpRequest = new TeleportRequest(
                teleportingPlayer,
                targetPlayer,
                tpahere,
                ConfigHandler.getInstance().getRequestTimeoutInSeconds(),
                teleportLocation);
        HashMap<UUID, TeleportRequest> targetRequests = activeTeleportRequests.computeIfAbsent(teleportingUuid, k -> new HashMap<>());
        HashMap<UUID, TeleportRequest> teleportingRequests = activeTeleportRequests.computeIfAbsent(teleportingUuid, k -> new HashMap<>());

        if (tpahere) {
            if (teleportingRequests.containsKey(targetUuid) && teleportingRequests.get(targetUuid).isTpaHere()) {
                return 1;
            }
            if (targetRequests.containsKey(teleportingUuid) && targetRequests.get(teleportingUuid).isTpaHere()) {
                return 2;
            }
        }
        //todo check if blocked
        if (tpahere) {
            teleportingRequests.put(targetUuid, tpRequest);
            activeTeleportRequests.put(teleportingUuid, teleportingRequests);
        } else {
            targetRequests.put(teleportingUuid, tpRequest);
            activeTeleportRequests.put(targetUuid, targetRequests);
        }
        teleportTimeOutTask(tpRequest);
        return 0;
    }


    private void teleportTimeOutTask(TeleportRequest tpRequest) {
        long timeout = ConfigHandler.getInstance().getRequestTimeoutInSeconds() * 20L;
        BukkitTask task = Bukkit.getScheduler().runTaskLater(SimpleTeleportRequests.getInstance(), () -> {
            Player target = tpRequest.getTargetPlayer();
            Player teleporting = tpRequest.getTeleportingPlayer();
            if (target.isOnline()) {
                target.sendRichMessage(
                        Message.REQUEST_TIMED_OUT_DESTINATION_PLAYER.getMessage(),
                        Placeholder.component("player", teleporting.displayName())
                );
            }
            if (teleporting.isOnline()) {
                teleporting.sendRichMessage(
                        Message.REQUEST_TIMED_OUT_TELEPORTING_PLAYER.getMessage(),
                        Placeholder.component("player", target.displayName())
                );
            }
            removePlayersFromMaps(teleporting, target);
        }, timeout);
        expireTasks.put(tpRequest, task);
    }


    public void removePlayersFromMaps(Player teleportingPlayer, Player targetPlayer) {
        UUID teleportingUuid = teleportingPlayer.getUniqueId();
        UUID targetUuid = targetPlayer.getUniqueId();
        HashMap<UUID, TeleportRequest> teleportingMap = activeTeleportRequests.get(teleportingUuid);
        if (teleportingMap != null) {
            teleportingMap.remove(targetUuid);
            if (teleportingMap.isEmpty()) {
                activeTeleportRequests.remove(teleportingUuid);
            } else {
                activeTeleportRequests.put(teleportingUuid, teleportingMap);
            }
        }
        HashMap<UUID, TeleportRequest> targetMap = activeTeleportRequests.get(targetUuid);
        if (targetMap != null) {
            targetMap.remove(teleportingUuid);
            if (targetMap.isEmpty()) {
                activeTeleportRequests.remove(targetUuid);
            } else {
                activeTeleportRequests.put(targetUuid, targetMap);
            }
        }
    }

    public TeleportRequest getTeleportRequest(@NotNull Player player) {
        UUID uuid = player.getUniqueId();
        HashMap<UUID, TeleportRequest> tpRequests = activeTeleportRequests.get(uuid);
        if (tpRequests == null || tpRequests.isEmpty()) return null;
        TeleportRequest latest = null;
        for (TeleportRequest request : tpRequests.values()) {
            if (request.hasRequestExpired()) {
                tpRequests.remove(uuid, request);
                continue;
            }
            if (request.isTpaHere() && request.getTargetPlayer().equals(player)) continue;
            if (!request.isTpaHere() && request.getTeleportingPlayer().equals(player)) continue;

            if (latest == null || request.getRequestTimeSysMil() > latest.getRequestTimeSysMil()) {
                latest = request;
            }
        }

        if (tpRequests.isEmpty()) {
            activeTeleportRequests.remove(uuid);
        }

        return latest;
    }

    public void removeUpcomingTask(TeleportRequest tpRequest) {
        BukkitTask task = expireTasks.get(tpRequest);
        if (task != null) task.cancel();
        expireTasks.remove(tpRequest);
    }

}
