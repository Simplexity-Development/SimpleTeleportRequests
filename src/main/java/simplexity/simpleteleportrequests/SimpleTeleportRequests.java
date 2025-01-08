package simplexity.simpleteleportrequests;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.java.JavaPlugin;
import simplexity.simpleteleportrequests.commands.TeleportAsk;
import simplexity.simpleteleportrequests.config.ConfigHandler;

public final class SimpleTeleportRequests extends JavaPlugin {

    private static SimpleTeleportRequests instance;
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();
        ConfigHandler.getInstance().reloadConfigValues();
        this.getCommand("tpa").setExecutor(new TeleportAsk());
        // Plugin startup logic

    }

    public static SimpleTeleportRequests getInstance() {
        return instance;
    }

    public static MiniMessage getMiniMessage() {
        return miniMessage;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
