package simplexity.simpleteleportrequests.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import simplexity.simpleteleportrequests.config.LocaleMessage;
import simplexity.simpleteleportrequests.constants.TeleportPermission;
import simplexity.simpleteleportrequests.logic.TeleportHandler;
import simplexity.simpleteleportrequests.logic.TeleportRequestManager;
import simplexity.simpleteleportrequests.objects.TeleportRequest;

import java.util.UUID;

@SuppressWarnings("UnstableApiUsage")
public class TeleportAccept {

    public static LiteralCommandNode<CommandSourceStack> createCommand() {
        return Commands.literal("tpaccept")
                .requires(TeleportAccept::canExecute)
                .executes(TeleportAccept::execute)
                .then(Commands.argument("player", StringArgumentType.word())
                        .suggests(SuggestionUtils::suggestPendingRequests)
                        .executes(TeleportAccept::executeWithArg)).build();

    }

    private static boolean canExecute(CommandSourceStack css) {
        if (!(css.getSender() instanceof Player player)) return false;
        return player.hasPermission(TeleportPermission.BASIC_TELEPORT.getPermission());
    }

    private static int execute(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        if (!(ctx.getSource().getSender() instanceof Player player)) throw Exceptions.MUST_BE_PLAYER.create();
        TeleportRequest request = TeleportRequestManager.getInstance().acceptRequest(player);
        if (request == null) throw Exceptions.NO_PENDING_REQUESTS.create();
        return acceptTeleport(request);
    }

    private static int executeWithArg(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        if (!(ctx.getSource().getSender() instanceof Player player)) throw Exceptions.MUST_BE_PLAYER.create();
        String playerName = StringArgumentType.getString(ctx, "player");
        Player requestedPlayer = Bukkit.getPlayerExact(playerName);
        if (requestedPlayer == null || !requestedPlayer.isOnline()) throw Exceptions.PLAYER_NOT_FOUND.create();
        UUID requestedPlayerUuid = requestedPlayer.getUniqueId();
        TeleportRequest request = TeleportRequestManager.getInstance().acceptRequest(player, requestedPlayerUuid);
        if (request == null) throw Exceptions.NO_PENDING_REQUESTS_BY_THAT_NAME.create();
        return acceptTeleport(request);
    }

    private static int acceptTeleport(TeleportRequest request) throws CommandSyntaxException {
        boolean tpHere = request.isTpaHere();
        Player targetPlayer = request.getTargetPlayer();
        Player sendingPlayer = request.getSendingPlayer();
        if (!targetPlayer.isOnline() || !sendingPlayer.isOnline()) throw Exceptions.PLAYER_LOGGED_OFF.create();

        if (tpHere) {
            TeleportHandler.teleportPlayer(sendingPlayer, targetPlayer);
        } else {
            TeleportHandler.teleportPlayer(targetPlayer, sendingPlayer);
        }
        sendingPlayer.sendRichMessage(LocaleMessage.TELEPORT_REQUEST_ACCEPTED.getMessage());
        targetPlayer.sendRichMessage(LocaleMessage.TELEPORT_REQUEST_ACCEPTED.getMessage());
        return Command.SINGLE_SUCCESS;
    }

}
