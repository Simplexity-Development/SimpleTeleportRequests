package simplexity.simpleteleportrequests.config;

import org.bukkit.configuration.file.FileConfiguration;
import simplexity.simpleteleportrequests.SimpleTeleportRequests;

public class ConfigHandler {
    private static ConfigHandler instance;

    public static ConfigHandler getInstance() {
        if (instance == null) {
            instance = new ConfigHandler();
        }
        return instance;
    }

    private ConfigHandler() {
    }

    private int requestTimeoutInSeconds, maxOutgoingRequests;
    private boolean requestRequireUsername, requestSendToVanished;

    public void reloadConfigValues(){
        SimpleTeleportRequests.getInstance().reloadConfig();
        LocaleHandler.getInstance().reloadLocale();
        FileConfiguration config = SimpleTeleportRequests.getInstance().getConfig();
        requestTimeoutInSeconds = config.getInt("request.timeout-seconds", 30);
        requestRequireUsername = config.getBoolean("request.require-username", false);
        requestSendToVanished = config.getBoolean("request.send-to-vanished", false);
        maxOutgoingRequests = config.getInt("request.max-outgoing", 3);
    }

    public int getRequestTimeoutInSeconds() {
        return requestTimeoutInSeconds;
    }

    public boolean requiresUsername() {
        return requestRequireUsername;
    }

    public boolean sendToVanished() {
        return requestSendToVanished;
    }

    public int getMaxOutgoingRequests(){
        return maxOutgoingRequests;
    }

}
