package simplexity.simpleteleportrequests.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import simplexity.simpleteleportrequests.TeleportHandler;
import simplexity.simpleteleportrequests.config.Message;

public class TeleportAccept implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendRichMessage(Message.MUST_BE_PLAYER.getMessage());
            return false;
        }
        if (!TeleportHandler.hasOutgoingRequest(player)) {
            player.sendRichMessage(Message.NO_PENDING_REQUESTS.getMessage());
            return false;
        }

        return false;
    }
}
