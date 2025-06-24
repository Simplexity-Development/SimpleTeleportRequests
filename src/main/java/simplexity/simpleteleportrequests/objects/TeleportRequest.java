package simplexity.simpleteleportrequests.objects;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TeleportRequest{

    private final Player teleportingPlayer;
    private final Player targetPlayer;
    private final boolean tpahere;
    private final long requestTime;
    private final int expireTimeSeconds;
    private final Location teleportLocation;

    public TeleportRequest(Player teleportingPlayer, Player targetPlayer, boolean tpahere,
                           int expireTimeSeconds, Location teleportLocation) {
        this.teleportingPlayer = teleportingPlayer;
        this.targetPlayer = targetPlayer;
        this.tpahere = tpahere;
        this.requestTime = System.currentTimeMillis();
        this.expireTimeSeconds = expireTimeSeconds;
        this.teleportLocation = teleportLocation;
    }

    public boolean hasRequestExpired(){
        return (System.currentTimeMillis() - requestTime) >= (expireTimeSeconds * 1000L);
    }


    public Player getTeleportingPlayer() {
        return teleportingPlayer;
    }

    public Player getTargetPlayer() {
        return targetPlayer;
    }

    public boolean isTpaHere() {
        return tpahere;
    }

    public long getRequestTimeSysMil(){
        return requestTime;
    }

    public int getExpireTimeSeconds(){
        return expireTimeSeconds;
    }

    public Location getTeleportLocation() {
        return teleportLocation;
    }

    public String toString() {
        return "TeleportRequest=["
               + "requestingPlayer=" + teleportingPlayer
               + ", targetPlayer=" + targetPlayer
               + ", requestTimeSystemMil=" + requestTime
               + ", didTargetPlayerInitiate=" + tpahere
               + ", expireTimeSeconds=" + expireTimeSeconds
               + ", teleportLocation=" + teleportLocation
               + ", requestHasExpired=" + hasRequestExpired()
               + "]";

    }
}
