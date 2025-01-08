package simplexity.simpleteleportrequests;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import simplexity.simpleteleportrequests.config.ConfigHandler;

import java.util.HashMap;

public class TeleportHandler {
    public static final HashMap<TeleportRequest, BukkitTask> pendingTeleports = new HashMap<>();

    public static void startTeleportTask(TeleportRequest teleportRequest) {
        long requestTimeOut = ConfigHandler.getInstance().getRequestTimeoutInSeconds() * 20L;
        BukkitTask task = Bukkit.getScheduler().runTaskLater(SimpleTeleportRequests.getInstance(), () -> {
            //todo change message
            teleportRequest.teleportingPlayer().sendRichMessage("<red>REQUEST TIMED OUT");
            teleportRequest.destinationPlayer().sendRichMessage("<green>REQUEST TIMED OUT");
        }, requestTimeOut);
        pendingTeleports.put(teleportRequest, task);
    }

    public static void cancelTeleportTask(Player player) {
        for (TeleportRequest teleportRequest : pendingTeleports.keySet()) {
            if (teleportRequest.teleportingPlayer().equals(player)) {
                pendingTeleports.get(teleportRequest).cancel();
                pendingTeleports.remove(teleportRequest);
                player.sendMessage("<red>CANCELLED");
                return;
            }
            if (teleportRequest.destinationPlayer().equals(player)) {
                pendingTeleports.get(teleportRequest).cancel();
                pendingTeleports.remove(teleportRequest);
                player.sendMessage("<green>CANCELLED");
                return;
            }
        }
    }





}
