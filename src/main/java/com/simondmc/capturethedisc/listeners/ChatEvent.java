package com.simondmc.capturethedisc.listeners;

import com.simondmc.capturethedisc.CaptureTheDisc;
import com.simondmc.capturethedisc.game.GameCore;
import com.simondmc.capturethedisc.game.Teams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Arrays;
import java.util.List;

public class ChatEvent implements Listener {

    @EventHandler
    public void chat(AsyncPlayerChatEvent e) {
        if (!GameCore.isOn()) return;

        e.setCancelled(true);
        String color = Teams.getRed().contains(e.getPlayer()) ? "§c" : "§a";
        List<Player> recievers = Teams.getRed().contains(e.getPlayer()) ? Teams.getRed() : Teams.getGreen();

        for (Player p : recievers) {
            p.sendMessage(color + e.getPlayer().getName() + "§r: " + e.getMessage());
        }
    }

    @EventHandler
    public void command(PlayerCommandPreprocessEvent e) {
        if (!GameCore.isOn()) return;

        String command = e.getMessage().split(" ")[0].substring(1);

        if (command.equalsIgnoreCase("me") || command.equalsIgnoreCase("minecraft:me")) {
            e.setCancelled(true);
            e.getPlayer().sendMessage("§c>:(");
        }

        if (command.equalsIgnoreCase("shout") || command.equalsIgnoreCase(CaptureTheDisc.plugin.getName() + ":shout")) {
            e.setCancelled(true);
            String color = Teams.getRed().contains(e.getPlayer()) ? "§c" : "§a";
            for (Player p : Bukkit.getOnlinePlayers()) {
                // https://stackoverflow.com/a/40910835
                p.sendMessage("§e[SHOUT] " + color + e.getPlayer().getName() + "§r: " + e.getMessage().substring(e.getMessage().indexOf(" ") + 1));
            }
        }
    }
}
