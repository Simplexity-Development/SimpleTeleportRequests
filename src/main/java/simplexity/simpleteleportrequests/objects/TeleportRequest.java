package simplexity.simpleteleportrequests.objects;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.UUID;

public class TeleportRequest{

    private final UUID teleportingPlayerUuid;
    private final UUID targetPlayerUuid;
    private final boolean targetPlayerInitiated;
    private final long requestTime;
    private final int expireTimeSeconds;
    private final Location teleportLocation;

    public TeleportRequest(UUID teleportingPlayerUuid, UUID targetPlayerUuid, boolean targetPlayerInitiated,
                           int expireTimeSeconds, Location teleportLocation) {
        this.teleportingPlayerUuid = teleportingPlayerUuid;
        this.targetPlayerUuid = targetPlayerUuid;
        this.targetPlayerInitiated = targetPlayerInitiated;
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

    public UUID getTargetPlayerUuid() {
        return targetPlayerUuid;
    }

    public boolean didTargetPlayerInitiate() {
        return targetPlayerInitiated;
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
               + ", targetPlayer=" + Bukkit.getPlayer(targetPlayerUuid)
               + ", requestTimeSystemMil=" + requestTime
               + ", didTargetPlayerInitiate=" + targetPlayerInitiated
               + ", expireTimeSeconds=" + expireTimeSeconds
               + ", teleportLocation=" + teleportLocation
               + ", requestHasExpired=" + hasRequestExpired()
               + "]";

    }
}
