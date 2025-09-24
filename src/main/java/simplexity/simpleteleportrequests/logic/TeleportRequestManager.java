package simplexity.simpleteleportrequests.logic;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import simplexity.simpleteleportrequests.SimpleTeleportRequests;
import simplexity.simpleteleportrequests.config.ConfigHandler;
import simplexity.simpleteleportrequests.config.LocaleMessage;
import simplexity.simpleteleportrequests.objects.TeleportRequest;

import java.util.*;

public class TeleportRequestManager {
    private static TeleportRequestManager instance;

    public TeleportRequestManager() {
    }

    public static TeleportRequestManager getInstance() {
        if (instance == null) instance = new TeleportRequestManager();
        return instance;
    }

    private final MiniMessage miniMessage = SimpleTeleportRequests.getMiniMessage();
    public final HashMap<UUID, Deque<TeleportRequest>> incomingRequests = new HashMap<>();
    private final HashMap<UUID, Set<UUID>> outgoingRequests = new HashMap<>();
    private final HashMap<TeleportRequest, BukkitTask> expireTasks = new HashMap<>();

    private final int maxOutgoingRequests = 3; //todo MAKE CONFIGURABLE

    public boolean createRequest(Player sender, Player target, boolean tpHere, boolean safetyOverride) {

        Set<UUID> senderOutgoing = outgoingRequests.computeIfAbsent(sender.getUniqueId(), k -> new HashSet<>());
        if (senderOutgoing.size() >= maxOutgoingRequests) {
            sender.sendRichMessage("You have too many pending requests!");
            return false;
        }
        if (senderOutgoing.contains(target.getUniqueId())) {
            sender.sendRichMessage("You already have a pending request to this player!");
            return false;
        }
        TeleportRequest tpRequest = new TeleportRequest(
                sender,
                target,
                tpHere,
                ConfigHandler.getInstance().getRequestTimeoutInSeconds());
        Deque<TeleportRequest> targetQueue = incomingRequests.computeIfAbsent(target.getUniqueId(),
                k -> new ArrayDeque<>());
        targetQueue.addLast(tpRequest);
        senderOutgoing.add(target.getUniqueId());

        scheduleTimeout(tpRequest);
        return true;
    }

    private void removeOutgoing(TeleportRequest request) {
        if (!request.isTpaHere()) {
            Set<UUID> senderOutgoing = outgoingRequests.get(request.getSendingPlayer().getUniqueId());
            if (senderOutgoing == null) return;
            senderOutgoing.remove(request.getTargetPlayer().getUniqueId());
            if (senderOutgoing.isEmpty()) outgoingRequests.remove(request.getSendingPlayer().getUniqueId());
        } else {
            Set<UUID> targetOutgoing = outgoingRequests.get(request.getTargetPlayer().getUniqueId());
            if (targetOutgoing == null) return;
            targetOutgoing.remove(request.getSendingPlayer().getUniqueId());
            if (targetOutgoing.isEmpty()) outgoingRequests.remove(request.getTargetPlayer().getUniqueId());
        }
    }

    private void removeFromQueue(TeleportRequest request) {
        Deque<TeleportRequest> queue;
        if (request.isTpaHere()) {
            queue = incomingRequests.get(request.getSendingPlayer().getUniqueId());
        } else {
            queue = incomingRequests.get(request.getTargetPlayer().getUniqueId());
        }
        if (queue == null) return;
        queue.remove(request);
    }


    private void scheduleTimeout(TeleportRequest request) {
        long timeout = ConfigHandler.getInstance().getRequestTimeoutInSeconds() * 20L;
        BukkitTask task = Bukkit.getScheduler().runTaskLater(SimpleTeleportRequests.getInstance(), () -> {
            Player target = request.getTargetPlayer();
            Player sender = request.getSendingPlayer();
            if (target != null && target.isOnline()) {
                target.sendRichMessage(
                        LocaleMessage.REQUEST_TIMED_OUT_DESTINATION_PLAYER.getMessage(),
                        Placeholder.component("player", sender.displayName())
                );
            }
            if (sender != null && sender.isOnline()) {
                sender.sendRichMessage(
                        LocaleMessage.REQUEST_TIMED_OUT_TELEPORTING_PLAYER.getMessage(),
                        Placeholder.component("player", target.displayName())
                );
            }
            removeOutgoing(request);
            removeFromQueue(request);
            expireTasks.remove(request);
        }, timeout);
        expireTasks.put(request, task);
    }

    @Nullable
    public TeleportRequest resolveRequest(@NotNull Player player) {
        Deque<TeleportRequest> queue = incomingRequests.get(player.getUniqueId());
        if (queue == null || queue.isEmpty()) return null;
        TeleportRequest request = queue.pollFirst();
        removeOutgoing(request);

        if (queue.isEmpty()) incomingRequests.remove(player.getUniqueId());
        cancelTimeout(request);
        return request;
    }

    public TeleportRequest resolveRequest(@NotNull Player player, @NotNull UUID userToAccept) {
        Deque<TeleportRequest> queue = incomingRequests.get(player.getUniqueId());
        if (queue == null || queue.isEmpty()) return null;

        Iterator<TeleportRequest> requestIterator = queue.iterator();
        while (requestIterator.hasNext()) {
            TeleportRequest request = requestIterator.next();
            if (request.getTargetPlayer().getUniqueId().equals(userToAccept)) {
                requestIterator.remove();
                removeOutgoing(request);
                cancelTimeout(request);
                if (queue.isEmpty()) incomingRequests.remove(player.getUniqueId());
                return request;
            }
        }

        return null;
    }


    public void cancelTimeout(TeleportRequest tpRequest) {
        BukkitTask task = expireTasks.get(tpRequest);
        if (task != null) task.cancel();
        expireTasks.remove(tpRequest);
    }

    public List<TeleportRequest> getPendingRequests(Player player) {
        Deque<TeleportRequest> queue = incomingRequests.get(player.getUniqueId());
        if (queue == null) return Collections.emptyList();
        return new ArrayList<>(queue);
    }


}
