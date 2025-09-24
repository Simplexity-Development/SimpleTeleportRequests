package simplexity.simpleteleportrequests.logic;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import simplexity.simpleteleportrequests.SimpleTeleportRequests;
import simplexity.simpleteleportrequests.commands.Exceptions;
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


    public boolean createRequest(Player sender, Player target, boolean tpHere, boolean safetyOverride) throws CommandSyntaxException {

        Set<UUID> senderOutgoing = outgoingRequests.computeIfAbsent(sender.getUniqueId(), k -> new HashSet<>());
        if (senderOutgoing.size() >= ConfigHandler.getInstance().getMaxOutgoingRequests()) {
            throw Exceptions.TOO_MANY_REQUESTS.create();
        }
        if (senderOutgoing.contains(target.getUniqueId())) {
            sender.sendRichMessage("You already have a pending request to this player!");
            throw Exceptions.ALREADY_REQUESTED.create();
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
            clearRequest(request);
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

    @Nullable
    public TeleportRequest resolveRequest(@NotNull Player player, @NotNull UUID userToAccept) {
        Deque<TeleportRequest> queue = incomingRequests.get(player.getUniqueId());
        if (queue == null || queue.isEmpty()) return null;

        for (TeleportRequest request : queue) {
            if (request.getTargetPlayer().getUniqueId().equals(userToAccept)) {
                clearRequest(request);
                if (queue.isEmpty()) incomingRequests.remove(player.getUniqueId());
                return request;
            }
        }

        return null;
    }

    public void resolveRequest(TeleportRequest request) {
        clearRequest(request);
    }


    public List<TeleportRequest> getPendingRequests(Player player) {
        Deque<TeleportRequest> queue = incomingRequests.get(player.getUniqueId());
        if (queue == null) return Collections.emptyList();
        return new ArrayList<>(queue);
    }

    public List<TeleportRequest> getOutgoingRequests(Player player) {
        Set<UUID> targets = outgoingRequests.get(player.getUniqueId());
        if (targets == null || targets.isEmpty()) return Collections.emptyList();

        List<TeleportRequest> result = new ArrayList<>();
        for (UUID targetUuid : targets) {
            Deque<TeleportRequest> queue = incomingRequests.get(targetUuid);
            if (queue == null) continue;
            for (TeleportRequest request : queue) {
                if (request.getSendingPlayer().getUniqueId().equals(player.getUniqueId())) {
                    result.add(request);
                }
            }
        }
        return result;
    }

    public TeleportRequest getLatestOutgoingRequest(Player player){
        List<TeleportRequest> outgoingRequests = getOutgoingRequests(player);
        if (outgoingRequests.isEmpty()) return null;
        TeleportRequest latest = null;
        for (TeleportRequest request : outgoingRequests) {
            if (latest == null || request.getRequestTimeSysMil() > latest.getRequestTimeSysMil()) {
                latest = request;
            }
        }
        return latest;
    }

    public TeleportRequest getSpecificOutgoingRequest(Player sender, Player target){
        List<TeleportRequest> outgoingRequests = getOutgoingRequests(sender);
        if (outgoingRequests.isEmpty()) return null;
        for (TeleportRequest request : outgoingRequests) {
            if (request.getTargetPlayer().equals(target)) return request;
        }
        return null;
    }


    private void clearRequest(TeleportRequest request) {
        cancelTimeout(request);
        removeOutgoing(request);
        removeFromQueue(request);
    }

    private void cancelTimeout(TeleportRequest tpRequest) {
        BukkitTask task = expireTasks.get(tpRequest);
        if (task != null) task.cancel();
        expireTasks.remove(tpRequest);
    }

    private void removeOutgoing(TeleportRequest request) {
        UUID senderUuid = request.getSendingPlayer().getUniqueId();
        UUID targetUuid = request.getTargetPlayer().getUniqueId();

        Set<UUID> senderOutgoing = outgoingRequests.get(senderUuid);
        if (senderOutgoing == null) return;
        senderOutgoing.remove(targetUuid);
        if (senderOutgoing.isEmpty()) outgoingRequests.remove(senderUuid);
    }

    private void removeFromQueue(TeleportRequest request) {
        UUID targetUuid = request.getTargetPlayer().getUniqueId();
        Deque<TeleportRequest> queue = incomingRequests.get(targetUuid);
        if (queue == null) return;
        queue.remove(request);
        if (queue.isEmpty()) incomingRequests.remove(targetUuid);
    }


}
