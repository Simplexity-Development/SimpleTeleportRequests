package simplexity.simpleteleportrequests;

import org.bukkit.entity.Player;

public record TeleportRequest(Player teleportingPlayer, Player destinationPlayer, long timeSent) {

    public String toString() {
        return "Teleporting Player: " + teleportingPlayer.getName()
               + ", Destination Player: " + destinationPlayer.getName()
               + ", Time Sent: " + timeSent;

    }
}
