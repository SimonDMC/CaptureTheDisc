package com.simondmc.eventctw;

import com.simondmc.eventctw.command.*;
import com.simondmc.eventctw.command.template.AdminCommand;
import com.simondmc.eventctw.command.template.CommandType;
import com.simondmc.eventctw.command.template.SuperCommand;
import com.simondmc.eventctw.game.Config;
import com.simondmc.eventctw.game.GameLoop;
import com.simondmc.eventctw.listeners.BlockEvent;
import com.simondmc.eventctw.listeners.PlayerEvent;
import com.simondmc.eventctw.shop.ShopClick;
import com.simondmc.eventctw.shop.ShopGUI;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class EventCTW extends JavaPlugin {
    public static EventCTW plugin;
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
        Config.createConfig("blocks.yml");
        // copies the schematic over
        /*this just doesnt want to work for some reason so ill leave it for now and maybe resolve later
        saveResource("map.schem", false);*/
        // loads shop
        ShopGUI.initShop();
        // starts game loop
        GameLoop.gameLoop();
    }

    @Override
    public void onDisable() {
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
    }

    void registerListeners() {
        getServer().getPluginManager().registerEvents(new BlockEvent(), plugin);
        getServer().getPluginManager().registerEvents(new PlayerEvent(), plugin);
        getServer().getPluginManager().registerEvents(new ShopClick(), plugin);
    }

    void registerCommand(SuperCommand cmd) {
        if (cmd.getType().equals(CommandType.ADMIN_COMMAND)) getCommand(cmd.getLabel()).setExecutor(new AdminCommand());
    }
}
