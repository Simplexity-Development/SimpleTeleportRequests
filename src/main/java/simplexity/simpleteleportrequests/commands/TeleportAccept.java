package simplexity.simpleteleportrequests.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import simplexity.simpleteleportrequests.logic.TeleportHandler;
import simplexity.simpleteleportrequests.config.Message;
import simplexity.simpleteleportrequests.logic.TeleportRequestManager;
import simplexity.simpleteleportrequests.objects.TeleportRequest;

public class TeleportAccept implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendRichMessage(Message.MUST_BE_PLAYER.getMessage());
            return false;
        }
        TeleportRequest tpRequest = TeleportRequestManager.
        if (!TeleportHandler.hasActiveRequest(player.getUniqueId() )) {
            player.sendRichMessage(Message.NO_PENDING_REQUESTS.getMessage());
            return false;
        }

        return false;
    }
}
