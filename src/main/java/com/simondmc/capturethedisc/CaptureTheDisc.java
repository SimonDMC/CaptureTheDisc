package com.simondmc.capturethedisc;

import com.simondmc.capturethedisc.command.*;
import com.simondmc.capturethedisc.command.template.AdminCommand;
import com.simondmc.capturethedisc.command.template.CommandType;
import com.simondmc.capturethedisc.command.template.SuperCommand;
import com.simondmc.capturethedisc.game.GameLoop;
import com.simondmc.capturethedisc.kits.Kits;
import com.simondmc.capturethedisc.listeners.BlockEvent;
import com.simondmc.capturethedisc.listeners.PlayerEvent;
import com.simondmc.capturethedisc.listeners.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class CaptureTheDisc extends JavaPlugin {
    public static CaptureTheDisc plugin;
    public static List<SuperCommand> commands = new ArrayList<>();

    @Override
    public void onEnable() {
        // registers plugin for static use
        plugin = this;
        // registers all commands
        populateCommands();
        for (SuperCommand cmd : commands)
            registerCommand(cmd);
        // registers listeners
        registerListeners();
        // config file
        plugin.saveDefaultConfig();
        // copies the schematic over
        /*this just doesnt want to work for some reason so ill leave it for now and maybe resolve later
        saveResource("map.schem", false);*/
        // starts game loop
        GameLoop.gameLoop();
        // build kit inventory
        Kits.initKitGui();
    }

    @Override
    public void onDisable() {
        // patch persistant glowing..?
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.setGlowing(false);
        }
    }

    void populateCommands() {
        commands.add(new TestCommand());
        commands.add(new DeconstructMapCommand());
        commands.add(new PasteMapCommand());
        commands.add(new StartCommand());
        commands.add(new ForceStopCommand());
        //commands.add(new TestDiscCommand());
        commands.add(new ShopCommand());
        commands.add(new CoinsCommand());
        commands.add(new TestUpgradesCommand());
        commands.add(new ToggleDevCommand());
        commands.add(new KillCommand());
        commands.add(new PickupDiscCommand());
    }

    void registerListeners() {
        getServer().getPluginManager().registerEvents(new BlockEvent(), plugin);
        getServer().getPluginManager().registerEvents(new PlayerEvent(), plugin);
        getServer().getPluginManager().registerEvents(new ClickEvent(), plugin);
    }

    void registerCommand(SuperCommand cmd) {
        if (cmd.getType().equals(CommandType.ADMIN_COMMAND)) getCommand(cmd.getLabel()).setExecutor(new AdminCommand());
    }
}
