package simplexity.simpleteleportrequests;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import simplexity.simpleteleportrequests.config.Message;

import java.util.HashMap;
import java.util.List;
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
    public final HashMap<UUID, List<TeleportRequest>> incomingTeleportRequests = new HashMap<>();
    // TPA requests
    public final HashMap<UUID, TeleportRequest> outgoingTeleportRequest = new HashMap<>();
    private final HashMap<TeleportRequest, BukkitTask> expireTasks = new HashMap<>();

    public void sendRequest(Player sender, Player target, boolean tpHereRequest, boolean safetyOverride) {
        UUID senderUuid = sender.getUniqueId();
        UUID targetUuid = target.getUniqueId();
        if (!tpHereRequest && outgoingTeleportRequest.containsKey(senderUuid)) {
            sender.sendRichMessage(Message.TELEPORT_REQUEST_ALREADY_PENDING.getMessage());
            return;
        }
        Location tpLocation;
        if (tpHereRequest) {
            tpLocation = sender.getLocation();
        } else {
            tpLocation = target.getLocation();
        }

    }
}
