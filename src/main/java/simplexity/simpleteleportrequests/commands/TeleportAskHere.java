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
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import simplexity.simpleteleportrequests.config.ConfigHandler;
import simplexity.simpleteleportrequests.config.LocaleMessage;
import simplexity.simpleteleportrequests.constants.TeleportPermission;
import simplexity.simpleteleportrequests.logic.TeleportRequestManager;

@SuppressWarnings("UnstableApiUsage")
public class TeleportAskHere {

    public static LiteralCommandNode<CommandSourceStack> createCommand() {
        return Commands.literal("tpahere")
                .requires(TeleportAskHere::canExecute)
                .then(Commands.argument("player", StringArgumentType.word())
                        .suggests(SuggestionUtils::suggestOnlinePlayers)
                        .executes(TeleportAskHere::execute)).build();
    }

    private static boolean canExecute(CommandSourceStack css) {
        if (!(css.getSender() instanceof Player player)) return false;
        return player.hasPermission(TeleportPermission.BASIC_TELEPORT.getPermission());
    }

    private static int execute(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        if (!(ctx.getSource().getSender() instanceof Player player)) throw Exceptions.MUST_BE_PLAYER.create();
        String targetName = StringArgumentType.getString(ctx, "player");
        Player target = Bukkit.getPlayerExact(targetName);
        if (target == null) throw Exceptions.PLAYER_NOT_FOUND.create();
        if (target.equals(player)) throw Exceptions.CANNOT_TELEPORT_TO_SELF.create();
        if (!player.canSee(target) && !ConfigHandler.getInstance().sendToVanished()) throw Exceptions.PLAYER_NOT_FOUND.create();
        boolean success = TeleportRequestManager.getInstance().createRequest(player, target, true, false);
        player.sendRichMessage(LocaleMessage.TELEPORT_ASK_HERE_SENT.getMessage(),
                Placeholder.component("player", target.displayName()),
                Placeholder.parsed("value", String.valueOf(ConfigHandler.getInstance().getRequestTimeoutInSeconds())));
        target.sendRichMessage(LocaleMessage.TELEPORT_ASK_HERE_RECEIVED.getMessage(),
                Placeholder.component("player", player.displayName()),
                Placeholder.parsed("value", String.valueOf(ConfigHandler.getInstance().getRequestTimeoutInSeconds())));
        if (success) return Command.SINGLE_SUCCESS;
        return 0;
    }

}

