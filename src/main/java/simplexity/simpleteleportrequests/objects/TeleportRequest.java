package simplexity.simpleteleportrequests.objects;

import org.bukkit.entity.Player;

public class TeleportRequest{

    private final Player sendingPlayer;
    private final Player targetPlayer;
    private final boolean tpHere;
    private final long requestTime;
    private final int expireTimeSeconds;

    public TeleportRequest(Player sendingPlayer, Player targetPlayer, boolean tpHere,
                           int expireTimeSeconds) {
        this.sendingPlayer = sendingPlayer;
        this.targetPlayer = targetPlayer;
        this.tpHere = tpHere;
        this.requestTime = System.currentTimeMillis();
        this.expireTimeSeconds = expireTimeSeconds;


    }

    public boolean hasRequestExpired(){
        return (System.currentTimeMillis() - requestTime) >= (expireTimeSeconds * 1000L);
    }


    public Player getSendingPlayer() {
        return sendingPlayer;
    }

    public Player getTargetPlayer() {
        return targetPlayer;
    }

    public boolean isTpaHere() {
        return tpHere;
    }

    public long getRequestTimeSysMil(){
        return requestTime;
    }

    public int getExpireTimeSeconds(){
        return expireTimeSeconds;
    }



    public String toString() {
        return "TeleportRequest=["
               + "requestingPlayer=" + sendingPlayer
               + ", targetPlayer=" + targetPlayer
               + ", requestTimeSystemMil=" + requestTime
               + ", didTargetPlayerInitiate=" + tpHere
               + ", expireTimeSeconds=" + expireTimeSeconds
               + ", requestHasExpired=" + hasRequestExpired()
               + "]";

    }
}
