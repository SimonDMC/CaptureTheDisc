package com.simondmc.eventctw.kits;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Kits {
    private static final HashMap<Player, Kit> kitList = new HashMap<>();

    public static Kit getKit(Player p) {
        return kitList.get(p);
    }

    public static void setKit(Player p, Kit k) {
        kitList.put(p, k);
    }

    public static void resetKits() {
        kitList.clear();
    }

    public static List<Player> getKitMembers(Kit k) {
        List<Player> result = new ArrayList<>();
        kitList.forEach((player, kit) -> {
            if (kit.equals(k)) result.add(player);
        });
        return result;
    }
}
