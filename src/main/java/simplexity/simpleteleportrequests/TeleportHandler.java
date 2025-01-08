package simplexity.simpleteleportrequests;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import simplexity.simpleteleportrequests.config.ConfigHandler;
import simplexity.simpleteleportrequests.config.Message;

import java.util.ArrayList;
import java.util.HashMap;

public class TeleportHandler {
    public static final HashMap<TeleportRequest, BukkitTask> activeTeleportTasks = new HashMap<>();
    public static final HashMap<Player, TeleportRequest> outgoingRequests = new HashMap<>();
    public static final HashMap<Player, ArrayList<TeleportRequest>> incomingRequests = new HashMap<>();

    public static void startTeleportTask(TeleportRequest teleportRequest) {
        handleStartTeleportingPlayer(teleportRequest);
        handleStartDestinationPlayer(teleportRequest);
        long requestTimeOut = ConfigHandler.getInstance().getRequestTimeoutInSeconds() * 20L;
        BukkitTask task = Bukkit.getScheduler().runTaskLater(SimpleTeleportRequests.getInstance(), () -> {
            Player teleportingPlayer = teleportRequest.teleportingPlayer();
            Player destinationPlayer = teleportRequest.destinationPlayer();
            teleportingPlayer.sendRichMessage(
                    Message.REQUEST_TIMED_OUT_TELEPORTING_PLAYER.getMessage(),
                    Placeholder.component("player", destinationPlayer.displayName()));
            destinationPlayer.sendRichMessage(
                    Message.REQUEST_TIMED_OUT_DESTINATION_PLAYER.getMessage(),
                    Placeholder.component("player", teleportingPlayer.displayName()));
            removeTeleportRequests(teleportRequest);
        }, requestTimeOut);
        activeTeleportTasks.put(teleportRequest, task);
    }

    private static void handleStartTeleportingPlayer(TeleportRequest teleportRequest) {
        Player teleportingPlayer = teleportRequest.teleportingPlayer();
        outgoingRequests.put(teleportingPlayer, teleportRequest);
    }

    private static void handleStartDestinationPlayer(TeleportRequest teleportRequest) {
        Player destinationPlayer = teleportRequest.destinationPlayer();
        ArrayList<TeleportRequest> requests = incomingRequests.get(destinationPlayer);
        if (requests == null) {
            requests = new ArrayList<>();
        }
        requests.add(teleportRequest);
        incomingRequests.put(destinationPlayer, requests);
    }

    private static void removeTeleportRequests(TeleportRequest teleportRequest) {
        outgoingRequests.remove(teleportRequest.teleportingPlayer());
        ArrayList<TeleportRequest> requests = incomingRequests.get(teleportRequest.destinationPlayer());
        if (requests == null) return;
        requests.remove(teleportRequest);
        incomingRequests.put(teleportRequest.destinationPlayer(), requests);
    }

    public static boolean hasOutgoingRequest(Player player) {
        return outgoingRequests.containsKey(player);
    }

    public static boolean hasOutgoingRequest(Player player, Player target){
        TeleportRequest teleportRequest = outgoingRequests.get(player);
        if (teleportRequest == null) return false;
        return teleportRequest.destinationPlayer().equals(target);
    }

    public static boolean hasIncomingRequest(Player destinationPlayer, Player teleportingPlayer){
        ArrayList<TeleportRequest> requests = incomingRequests.get(destinationPlayer);
        if (requests == null) return false;
        for (TeleportRequest teleportRequest : requests) {
            if (teleportRequest.teleportingPlayer().equals(teleportingPlayer)) return true;
        }
        return false;
    }

    public static void cancelTeleportTask(Player player) {
        for (TeleportRequest teleportRequest : activeTeleportTasks.keySet()) {
            if (teleportRequest.teleportingPlayer().equals(player) || teleportRequest.destinationPlayer().equals(player)) {
                teleportRequest.teleportingPlayer().sendRichMessage(Message.TELEPORT_REQUEST_CANCELLED.getMessage());
                teleportRequest.destinationPlayer().sendRichMessage(Message.TELEPORT_REQUEST_CANCELLED.getMessage());
                activeTeleportTasks.get(teleportRequest).cancel();
                activeTeleportTasks.remove(teleportRequest);
                return;
            }
        }
    }





}
