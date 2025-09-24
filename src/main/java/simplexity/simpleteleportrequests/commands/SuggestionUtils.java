package simplexity.simpleteleportrequests.commands;


import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import simplexity.simpleteleportrequests.logic.TeleportRequestManager;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("UnstableApiUsage")
public class SuggestionUtils {

    public static CompletableFuture<Suggestions> suggestPendingRequests(@NotNull CommandContext<?> context, @NotNull SuggestionsBuilder builder) {
        CommandSourceStack css = (CommandSourceStack) context.getSource();
        if (!(css.getSender() instanceof Player player)) return builder.buildFuture();

        TeleportRequestManager.getInstance().getPendingRequests(player).forEach(request -> {
            Player sendingPlayer = request.getSendingPlayer();
            if (sendingPlayer != null) {
                builder.suggest(sendingPlayer.getName());
            }
        });

        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> suggestOnlinePlayers(@NotNull CommandContext<?> context, @NotNull SuggestionsBuilder builder) {
        CommandSourceStack css = (CommandSourceStack) context.getSource();
        if (!(css.getSender() instanceof Player player)) return builder.buildFuture();
        Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
            if (player.canSee(onlinePlayer)
                    && onlinePlayer.getName().toLowerCase().startsWith(builder.getRemainingLowerCase())
                    && !onlinePlayer.equals(player)) {
                builder.suggest(onlinePlayer.getName(),
                        MessageComponentSerializer.message().serialize(onlinePlayer.displayName()));
            }
        });
        return builder.buildFuture();
    }
}
