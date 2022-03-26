package com.simondmc.eventctw.game;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class ActionBarHandler {
    public static void displayStats(Player p) {
        // https://www.spigotmc.org/threads/257845/
        float coins = Coins.getCoins(p);
        int kills = GameUtils.getKills(p);
        // remove decimal point if whole number (or like 0.0000000004 or whatever floating point precision is)
        if (Math.round(coins * 10) % 10 == 0)
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§6" + Math.round(coins) + "¢" + " §e| §a" + kills + "⚔"));
            // hopefully get rid of floating point precision error istg
        else
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§6" + Math.round(coins * 10) / 10f + "¢" + " §e| §a" + kills + "⚔"));
    }
}
