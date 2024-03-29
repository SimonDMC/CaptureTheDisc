package com.simondmc.capturethedisc;

import com.simondmc.capturethedisc.command.*;
import com.simondmc.capturethedisc.command.template.AdminCommand;
import com.simondmc.capturethedisc.command.template.CommandType;
import com.simondmc.capturethedisc.command.template.PublicCommand;
import com.simondmc.capturethedisc.command.template.SuperCommand;
import com.simondmc.capturethedisc.game.GameLoop;
import com.simondmc.capturethedisc.kits.Kits;
import com.simondmc.capturethedisc.listeners.BlockEvent;
import com.simondmc.capturethedisc.listeners.ChatEvent;
import com.simondmc.capturethedisc.listeners.ClickEvent;
import com.simondmc.capturethedisc.listeners.PlayerEvent;
import com.simondmc.capturethedisc.util.Config;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class CaptureTheDisc extends JavaPlugin {
    public static CaptureTheDisc plugin;
    public static List<SuperCommand> commands = new ArrayList<>();
    public static boolean coreEnabled = false;

    @Override
    public void onEnable() {
        // registers plugin for static use
        plugin = this;
        // check for core
        if (Bukkit.getPluginManager().getPlugin("CharityEventCore") != null) {
            coreEnabled = true;
        }
        // registers all commands
        populateCommands();
        for (SuperCommand cmd : commands)
            registerCommand(cmd);
        // registers listeners
        registerListeners();
        // config file
        plugin.saveDefaultConfig();
        // copies the map over
        plugin.saveResource("world.zip", false);
        // starts game loop
        GameLoop.gameLoop();
        // build kit inventory
        Kits.initKitGui();
        // register disc goal
        Config.registerDiscGoal();
        if (coreEnabled) CoreHolder.registerCore();
    }

    @Override
    public void onDisable() {
        // patch persistent glowing and sidebar
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.setGlowing(false);
            p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }
        if (coreEnabled) CoreHolder.unregisterCore();
    }

    void populateCommands() {
        commands.add(new TestCommand());
        commands.add(new PasteMapCommand());
        commands.add(new StartCommand());
        commands.add(new ForceStopCommand());
        commands.add(new ShopCommand());
        commands.add(new CoinsCommand());
        commands.add(new TestUpgradesCommand());
        commands.add(new ToggleDevCommand());
        commands.add(new KillCommand());
        commands.add(new PickupDiscCommand());
        commands.add(new TogglePerformanceCommand());
        if (!CaptureTheDisc.coreEnabled) commands.add(new ToggleShoutCommand());
        commands.add(new JoinCommand());
        commands.add(new StuckCommand());
    }

    void registerListeners() {
        getServer().getPluginManager().registerEvents(new BlockEvent(), plugin);
        getServer().getPluginManager().registerEvents(new PlayerEvent(), plugin);
        getServer().getPluginManager().registerEvents(new ClickEvent(), plugin);
        if (!CaptureTheDisc.coreEnabled) getServer().getPluginManager().registerEvents(new ChatEvent(), plugin);
    }

    void registerCommand(SuperCommand cmd) {
        if (cmd.getType().equals(CommandType.ADMIN_COMMAND)) getCommand(cmd.getLabel()).setExecutor(new AdminCommand());
        if (cmd.getType().equals(CommandType.PUBLIC_COMMAND)) getCommand(cmd.getLabel()).setExecutor(new PublicCommand());
    }
}
