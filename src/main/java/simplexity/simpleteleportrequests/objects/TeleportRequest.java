package simplexity.simpleteleportrequests.objects;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.UUID;

public class TeleportRequest{

    private final UUID teleportingPlayerUuid;
    private final UUID destinationPlayerUuid;
    private final boolean isTpHereRequest;
    private final long requestTime;
    private final int expireTimeSeconds;
    private final Location teleportLocation;

    public TeleportRequest(UUID teleportingPlayerUuid, UUID destinationPlayerUuid, boolean isTpHereRequest,
                           int expireTimeSeconds, Location teleportLocation) {
        this.teleportingPlayerUuid = teleportingPlayerUuid;
        this.destinationPlayerUuid = destinationPlayerUuid;
        this.isTpHereRequest = isTpHereRequest;
        this.requestTime = System.currentTimeMillis();
        this.expireTimeSeconds = expireTimeSeconds;
        this.teleportLocation = teleportLocation;
    }

    public boolean hasRequestExpired(){
        return (System.currentTimeMillis() - requestTime) >= (expireTimeSeconds * 1000L);
    }


    public UUID getTeleportingPlayerUuid() {
        return teleportingPlayerUuid;
    }

    public UUID getDestinationPlayerUuid() {
        return destinationPlayerUuid;
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
               + "requestingPlayer=" + Bukkit.getPlayer(teleportingPlayerUuid)
               + ", targetPlayer=" + Bukkit.getPlayer(destinationPlayerUuid)
               + ", requestTimeSystemMil=" + requestTime
               + ", isTpHereRequest=" + isTpHereRequest
               + ", expireTimeSeconds=" + expireTimeSeconds
               + ", teleportLocation=" + teleportLocation
               + ", requestHasExpired=" + hasRequestExpired()
               + "]";

    }
}
