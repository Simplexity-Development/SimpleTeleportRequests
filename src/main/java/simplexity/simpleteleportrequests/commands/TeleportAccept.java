package simplexity.simpleteleportrequests.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import simplexity.simpleteleportrequests.config.Message;
import simplexity.simpleteleportrequests.logic.TeleportRequestManager;
import simplexity.simpleteleportrequests.objects.TeleportRequest;

import java.util.UUID;

public class TeleportAccept implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendRichMessage(Message.MUST_BE_PLAYER.getMessage());
            return false;
        }
        TeleportRequest request = TeleportRequestManager.getInstance().getTeleportRequest(player.getUniqueId());
        if (request == null) {
            player.sendRichMessage(Message.NO_PENDING_REQUESTS.getMessage());
            return false;
        }
        UUID teleportingUUID = request.getTeleportingPlayerUuid();
        UUID targetUUID = request.getTargetPlayerUuid();
        Player teleportingPlayer = Bukkit.getPlayer(request.getTeleportingPlayerUuid());
        Player destinationPlayer = Bukkit.getPlayer(request.getTargetPlayerUuid());
        if (teleportingPlayer == null || destinationPlayer == null) return false;
        teleportingPlayer.teleportAsync(request.getTeleportLocation());
        teleportingPlayer.sendRichMessage(Message.TELEPORT_REQUEST_ACCEPTED.getMessage());
        destinationPlayer.sendRichMessage(Message.TELEPORT_REQUEST_ACCEPTED.getMessage());
        TeleportRequestManager.getInstance().removePlayersFromMaps(targetUUID, teleportingUUID);
        TeleportRequestManager.getInstance().removePlayersFromMaps(teleportingUUID, targetUUID);
        TeleportRequestManager.getInstance().removeUpcomingTask(request);
        return false;
    }
}
