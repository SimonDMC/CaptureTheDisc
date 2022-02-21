package com.simondmc.eventctw.game;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class ActionBarHandler {
    public static void displayCoins(Player p) {
        // https://www.spigotmc.org/threads/257845/
        float coins = Coins.getCoins(p);
        // remove decimal point if whole number
        if (coins == Math.round(coins)) p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§6" + Math.round(coins) + "¢"));
            // hopefully get rid of floating point precision error istg
        else p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§6" + Math.round(coins*10)/10f + "¢"));
    }
}
