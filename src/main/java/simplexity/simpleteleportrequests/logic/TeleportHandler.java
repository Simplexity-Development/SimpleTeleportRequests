package simplexity.simpleteleportrequests.logic;

public class TeleportHandler {

    /*
    public static final HashMap<TeleportRequest, BukkitTask> activeTeleportTasks = new HashMap<>();
    public static final HashMap<UUID, ArrayList<TeleportRequest>> activeRequests = new HashMap<>();

    public static void startTeleportTask(TeleportRequest teleportRequest) {
        handleStartDestinationPlayer(teleportRequest);
        long requestTimeOut = ConfigHandler.getInstance().getRequestTimeoutInSeconds() * 20L;
        BukkitTask task = Bukkit.getScheduler().runTaskLater(SimpleTeleportRequests.getInstance(), () -> {
            Player teleportingPlayer = Bukkit.getPlayer(teleportRequest.teleportingPlayerUUID());
            Player destinationPlayer = Bukkit.getPlayer(teleportRequest.destinationPlayerUUID());
            if (teleportingPlayer != null && destinationPlayer != null) {
                teleportingPlayer.sendRichMessage(
                        Message.REQUEST_TIMED_OUT_TELEPORTING_PLAYER.getMessage(),
                        Placeholder.component("player", destinationPlayer.displayName()));
                destinationPlayer.sendRichMessage(
                        Message.REQUEST_TIMED_OUT_DESTINATION_PLAYER.getMessage(),
                        Placeholder.component("player", teleportingPlayer.displayName()));
            }
            removeTeleportRequest(teleportRequest);
        }, requestTimeOut);
        activeTeleportTasks.put(teleportRequest, task);
    }

    private static void handleStartDestinationPlayer(TeleportRequest teleportRequest) {
        UUID destinationPlayer = teleportRequest.destinationPlayerUUID();
        ArrayList<TeleportRequest> requests = activeRequests.get(destinationPlayer);
        if (requests == null) {
            requests = new ArrayList<>();
        }
        requests.add(teleportRequest);
        activeRequests.put(destinationPlayer, requests);
    }

    private static void removeTeleportRequest(TeleportRequest teleportRequest) {
        ArrayList<TeleportRequest> requests = activeRequests.get(teleportRequest.destinationPlayerUUID());
        if (requests == null) return;
        requests.remove(teleportRequest);
        activeRequests.put(teleportRequest.destinationPlayerUUID(), requests);
    }


    public static boolean hasActiveRequest(UUID playerUUID) {
        return activeRequests.containsKey(playerUUID);
    }

    public static boolean hasActiveRequest(UUID destinationPlayerUUID, UUID teleportingPlayerUUID) {
        ArrayList<TeleportRequest> requests = activeRequests.get(destinationPlayerUUID);
        if (requests == null) return false;
        for (TeleportRequest teleportRequest : requests) {
            if (teleportRequest.teleportingPlayerUUID().equals(teleportingPlayerUUID)) return true;
        }
        return false;
    }

    public static void cancelTeleportTask(UUID playerUUID) {
        for (TeleportRequest teleportRequest : activeTeleportTasks.keySet()) {
            if (teleportRequest.teleportingPlayerUUID().equals(playerUUID) || teleportRequest.destinationPlayerUUID().equals(playerUUID)) {
                Player teleportingPlayer = Bukkit.getPlayer(teleportRequest.teleportingPlayerUUID());
                if (teleportingPlayer != null)
                    teleportingPlayer.sendRichMessage(Message.TELEPORT_REQUEST_CANCELLED.getMessage());
                Player destinationPlayer = Bukkit.getPlayer(teleportRequest.destinationPlayerUUID());
                if (destinationPlayer != null)
                    destinationPlayer.sendRichMessage(Message.TELEPORT_REQUEST_CANCELLED.getMessage());
                activeTeleportTasks.get(teleportRequest).cancel();
                activeTeleportTasks.remove(teleportRequest);
                return;
            }
        }
    }*/


}
