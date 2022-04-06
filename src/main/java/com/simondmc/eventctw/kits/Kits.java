package com.simondmc.eventctw.kits;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Kits {
    private static final HashMap<Player, Kit> kitList = new HashMap<>();
    public static final List<Player> selected = new ArrayList<>();
    private static final Inventory kitGui = Bukkit.createInventory(null, 27, "Select Kit");

    public static Kit getKit(Player p) {
        return kitList.getOrDefault(p, null);
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

    public static void initKitGui() {
        ItemStack i = new ItemStack(Material.CROSSBOW);
        ItemMeta m = i.getItemMeta();
        m.setDisplayName("§eArcher Kit");
        List<String> l = new ArrayList<>(Arrays.asList(
                " ",
                "§7Contents:",
                "§7- 1x Crossbow",
                "§7- 1x Regenerating Arrow (10s)"
        ));
        m.setLore(l);
        m.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        i.setItemMeta(m);
        kitGui.setItem(11, i);

        i = new ItemStack(Material.FISHING_ROD);
        m = i.getItemMeta();
        m.setDisplayName("§eTactician Kit");
        l = new ArrayList<>(Arrays.asList(
                " ",
                "§7Contents:",
                "§7- 1x Fishing Rod",
                "§7- 1x Speed I Potion (0:30)",
                "§7- 10x Scaffolding"
        ));
        m.setLore(l);
        m.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        i.setItemMeta(m);
        kitGui.setItem(13, i);

        i = new ItemStack(Material.IRON_BOOTS);
        m = i.getItemMeta();
        m.setDisplayName("§eTank Kit");
        l = new ArrayList<>(Arrays.asList(
                " ",
                "§7Contents:",
                "§7- 1x Chainmail Helmet",
                "§7- 1x Iron Boots"
        ));
        m.setLore(l);
        m.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        i.setItemMeta(m);
        kitGui.setItem(15, i);
    }

    public static Inventory getKitGui() {
        return kitGui;
    }

    public static void openKitGui(Player p) {
        p.openInventory(kitGui);
    }
}
