package simplexity.simpleteleportrequests.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import simplexity.simpleteleportrequests.config.Message;
import simplexity.simpleteleportrequests.logic.TeleportRequestManager;
import simplexity.simpleteleportrequests.objects.TeleportRequest;

public class TeleportAccept implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendRichMessage(Message.MUST_BE_PLAYER.getMessage());
            return true;
        }
        TeleportRequest request = TeleportRequestManager.getInstance().getTeleportRequest(player);
        if (request == null) {
            player.sendRichMessage(Message.NO_PENDING_REQUESTS.getMessage());
            return true;
        }

        Player teleportingPlayer = request.getTeleportingPlayer();
        Player destinationPlayer = request.getTargetPlayer();
        if (teleportingPlayer == null || destinationPlayer == null) return true;
        teleportingPlayer.teleportAsync(request.getTeleportLocation());
        teleportingPlayer.sendRichMessage(Message.TELEPORT_REQUEST_ACCEPTED.getMessage());
        destinationPlayer.sendRichMessage(Message.TELEPORT_REQUEST_ACCEPTED.getMessage());
        if (!request.isTpaHere()) {
            TeleportRequestManager.getInstance().removePlayersFromMaps(teleportingPlayer, request.getTargetPlayer());
        } else {
            TeleportRequestManager.getInstance().removePlayersFromMaps(request.getTargetPlayer(), teleportingPlayer);
        }
        TeleportRequestManager.getInstance().removeUpcomingTask(request);
        return true;
    }
}
