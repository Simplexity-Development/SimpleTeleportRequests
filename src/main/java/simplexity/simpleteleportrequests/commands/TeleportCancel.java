package simplexity.simpleteleportrequests.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import simplexity.simpleteleportrequests.config.LocaleMessage;
import simplexity.simpleteleportrequests.constants.TeleportPermission;
import simplexity.simpleteleportrequests.logic.TeleportRequestManager;
import simplexity.simpleteleportrequests.objects.TeleportRequest;

@SuppressWarnings("UnstableApiUsage")
public class TeleportCancel {

    public static LiteralCommandNode<CommandSourceStack> createCommand() {
        return Commands.literal("tpcancel")
                .requires(TeleportCancel::canExecute)
                .executes(TeleportCancel::execute)
                .then(Commands.argument("player", StringArgumentType.word())
                        .suggests(SuggestionUtils::suggestPendingRequests)
                        .executes(TeleportCancel::executeWithArg)).build();

    }

    private static boolean canExecute(CommandSourceStack css) {
        if (!(css.getSender() instanceof Player player)) return false;
        return player.hasPermission(TeleportPermission.BASIC_TELEPORT.getPermission());
    }

    private static int execute(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        if (!(ctx.getSource().getSender() instanceof Player player)) throw Exceptions.MUST_BE_PLAYER.create();
        TeleportRequest request = TeleportRequestManager.getInstance().getLatestOutgoingRequest(player);
        if (request == null) throw Exceptions.NO_PENDING_REQUESTS.create();
        TeleportRequestManager.getInstance().resolveRequest(request);
        return cancelTeleport(request);
    }

    private static int executeWithArg(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        if (!(ctx.getSource().getSender() instanceof Player player)) throw Exceptions.MUST_BE_PLAYER.create();
        String playerName = StringArgumentType.getString(ctx, "player");
        Player requestedPlayer = Bukkit.getPlayerExact(playerName);
        if (requestedPlayer == null || !requestedPlayer.isOnline()) throw Exceptions.PLAYER_NOT_FOUND.create();
        TeleportRequest request = TeleportRequestManager.getInstance().getSpecificOutgoingRequest(player, requestedPlayer);
        if (request == null) throw Exceptions.NO_PENDING_REQUESTS_BY_THAT_NAME.create();
        TeleportRequestManager.getInstance().resolveRequest(request);
        return cancelTeleport(request);
    }

    private static int cancelTeleport(TeleportRequest request) throws CommandSyntaxException {
        Player targetPlayer = request.getTargetPlayer();
        Player sendingPlayer = request.getSendingPlayer();
        if (!targetPlayer.isOnline() || !sendingPlayer.isOnline()) throw Exceptions.PLAYER_LOGGED_OFF.create();
        sendingPlayer.sendRichMessage(LocaleMessage.TELEPORT_REQUEST_CANCELLED.getMessage(),
                Placeholder.component("player", targetPlayer.displayName()));
        targetPlayer.sendRichMessage(LocaleMessage.TELEPORT_REQUEST_CANCELLED.getMessage(),
                Placeholder.component("player", sendingPlayer.displayName()));
        return Command.SINGLE_SUCCESS;
    }
}
