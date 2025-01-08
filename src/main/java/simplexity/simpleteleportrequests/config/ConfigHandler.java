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

    public ConfigHandler() {
    }

    public Long requestTimeoutInSeconds;
    public Boolean requestRequireUsername, requestSendToVanished;

    public void reloadConfigValues(){
        SimpleTeleportRequests.getInstance().reloadConfig();
        LocaleHandler.getInstance().reloadLocale();
        FileConfiguration config = SimpleTeleportRequests.getInstance().getConfig();
        requestTimeoutInSeconds = config.getLong("request.timeout-seconds", 30);
        requestRequireUsername = config.getBoolean("request.require-username", false);
        requestSendToVanished = config.getBoolean("request.send-to-vanished", false);
    }

    public Long getRequestTimeoutInSeconds() {
        return requestTimeoutInSeconds;
    }

    public boolean requiresUsername() {
        return requestRequireUsername;
    }

    public boolean sendToVanished() {
        return requestSendToVanished;
    }
}
