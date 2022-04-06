package com.simondmc.eventctw.game;

import com.simondmc.eventctw.util.Utils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class Coins {
    private final static HashMap<Player, Float> coins = new HashMap<>();

    public static void addCoins(Player p, float amount) {
        float baseAmount = (coins.containsKey(p) ? coins.get(p) : 0);
        float adjusted = Math.round(amount * 10) / 10f;
        coins.put(p, baseAmount + adjusted);

        // no sound or message in chat if subtracting or refunding from below 0
        if (amount <= 0 || baseAmount < 0) return;

        // if damage is whole, round to omit decimal point
        if (adjusted == Math.round(adjusted)) {
            p.sendMessage("§6+" + Math.round(adjusted) + " Coins!");
        } else {
            p.sendMessage("§6+" + adjusted + " Coins!");
        }
        Utils.playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
    }

    public static float getCoins(Player p) {
        return (coins.containsKey(p) ? coins.get(p) : 0);
    }

    public static boolean hasCoins(Player p, float amount) {
        if (!coins.containsKey(p)) return false;
        return coins.get(p) >= amount;
    }

    public static void setCoins(Player p, float amount) {
        coins.put(p, amount);
    }
}
