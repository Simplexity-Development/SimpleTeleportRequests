package simplexity.simpleteleportrequests.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import simplexity.simpleteleportrequests.SimpleTeleportRequests;
import simplexity.simpleteleportrequests.config.ConfigHandler;

import java.util.Collection;

public class CommandUtils {
    private static final MiniMessage miniMessage = SimpleTeleportRequests.getMiniMessage();

    public static Player playerFromString(String string){
        Player player = Bukkit.getPlayer(string);
        if (player != null) return player;
        if (ConfigHandler.getInstance().requestRequireUsername) return null;
        return playerFromNickname(string);
    }

    private static Player playerFromNickname(String nickname){
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        for(Player player : players){
            String displayName = player.getDisplayName();
            if(displayName.equals(nickname)){
                return player;
            }
        }
        return null;
    }

    public static Component parseTeleportRequestMessage(String message, Player player){
        return miniMessage.deserialize(message,
                Placeholder.component("player", player.displayName()),
                Placeholder.parsed("value", ConfigHandler.getInstance().getRequestTimeoutInSeconds().toString()));
    }


}
