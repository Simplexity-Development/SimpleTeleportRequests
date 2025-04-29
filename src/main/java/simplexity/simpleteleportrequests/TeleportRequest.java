package simplexity.simpleteleportrequests;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.UUID;

public class TeleportRequest{

    private final UUID requestingPlayerUuid;
    private final UUID targetPlayerUuid;
    private final boolean isTpHereRequest;
    private final long requestTime;
    private final int expireTimeSeconds;
    private final Location teleportLocation;

    public TeleportRequest(UUID requestingPlayerUuid, UUID targetPlayerUuid, boolean isTpHereRequest,
                           int expireTimeSeconds, Location teleportLocation) {
        this.requestingPlayerUuid = requestingPlayerUuid;
        this.targetPlayerUuid = targetPlayerUuid;
        this.isTpHereRequest = isTpHereRequest;
        this.requestTime = System.currentTimeMillis();
        this.expireTimeSeconds = expireTimeSeconds;
        this.teleportLocation = teleportLocation;
    }

    public boolean hasRequestExpired(){
        return (System.currentTimeMillis() - requestTime) >= (expireTimeSeconds * 1000L);
    }


    public UUID getRequestingPlayerUuid() {
        return requestingPlayerUuid;
    }

    public UUID getTargetPlayerUuid() {
        return targetPlayerUuid;
    }

    public boolean isTpHereRequest() {
        return isTpHereRequest;
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
               + "requestingPlayer=" + Bukkit.getPlayer(requestingPlayerUuid)
               + ", targetPlayer=" + Bukkit.getPlayer(targetPlayerUuid)
               + ", requestTimeSystemMil=" + requestTime
               + ", isTpHereRequest=" + isTpHereRequest
               + ", expireTimeSeconds=" + expireTimeSeconds
               + ", teleportLocation=" + teleportLocation
               + ", requestHasExpired=" + hasRequestExpired()
               + "]";

    }
}
