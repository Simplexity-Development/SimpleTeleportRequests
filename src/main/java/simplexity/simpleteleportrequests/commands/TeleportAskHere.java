package simplexity.simpleteleportrequests.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import simplexity.simpleteleportrequests.config.Message;
import simplexity.simpleteleportrequests.logic.TeleportRequestManager;

public class TeleportAskHere  implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendRichMessage(Message.MUST_BE_PLAYER.getMessage());
            return false;
        }
        if (args.length < 1) {
            sender.sendRichMessage(Message.MUST_SUPPLY_PLAYER.getMessage());
            return false;
        }
        String playerName = args[0];
        Player targetPlayer = CommandUtils.playerFromString(playerName);
        if (targetPlayer == null) {
            sender.sendRichMessage(Message.PLAYER_DOES_NOT_EXIST.getMessage());
            return false;
        }
        if (targetPlayer.equals(player)) {
            sender.sendRichMessage(Message.TELEPORT_REQUEST_SELF.getMessage());
            return false;
        }
        TeleportRequestManager.getInstance().sendRequest(targetPlayer, player, true, true);
        player.sendMessage(CommandUtils.parseTeleportRequestMessage(Message.TELEPORT_ASK_HERE_SENT.getMessage(), targetPlayer));
        targetPlayer.sendMessage(CommandUtils.parseTeleportRequestMessage(Message.TELEPORT_ASK_HERE_RECEIVED.getMessage(), player));
        return false;
    }
}
