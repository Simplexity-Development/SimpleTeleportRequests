package simplexity.simpleteleportrequests;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.java.JavaPlugin;
import simplexity.simpleteleportrequests.commands.*;
import simplexity.simpleteleportrequests.config.ConfigHandler;

@SuppressWarnings("UnstableApiUsage")
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
        this.getLifecycleManager().registerEventHandler(
                LifecycleEvents.COMMANDS, commands -> {
                    commands.registrar().register(TeleportAsk.createCommand());
                    commands.registrar().register(TeleportAccept.createCommand());
                    commands.registrar().register(TeleportAskHere.createCommand());
                    commands.registrar().register(TeleportDeny.createCommand());
                    commands.registrar().register(TeleportCancel.createCommand());
                }
        );
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
