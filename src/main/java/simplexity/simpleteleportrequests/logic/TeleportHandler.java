package simplexity.simpleteleportrequests.logic;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import simplexity.simpleteleportrequests.objects.TeleportRequest;

/**
 * Handles the teleport functionality and how it functions with players
 */
public class TeleportHandler {

    public static void teleportPlayer(@NotNull Player stationaryPlayer, @NotNull Player teleportingPlayer){
        Location location = stationaryPlayer.getLocation();
        //todo add cooldown
        teleportingPlayer.teleport(location);
    }

}
