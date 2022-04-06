package com.simondmc.eventctw.game;

import com.simondmc.eventctw.EventCTW;
import com.simondmc.eventctw.kits.Inventory;
import com.simondmc.eventctw.kits.Kits;
import com.simondmc.eventctw.region.Region;
import com.simondmc.eventctw.shop.ShopGUI;
import com.simondmc.eventctw.shop.Upgrade;
import com.simondmc.eventctw.util.Utils;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

public class GameUtils {
    public static void clearEntities(Player p) {
        for (Entity e : p.getWorld().getEntities()) {
            if (!(e instanceof Player) && Utils.inRegion(e.getLocation(), Region.MAP)) e.remove();
        }
    }

    public static void setupPlayers() {
        for (Player p : Teams.getPlayers()) {
            p.setGameMode(GameMode.SURVIVAL);
            p.setHealth(20);
            p.setFoodLevel(20);
            p.setSaturation(0);
            p.getInventory().clear();
            Inventory.fillInv(p);
            if (Teams.getRed().contains(p)) {
                p.teleport(Utils.genLocation(p.getWorld(), Region.RED_SPAWN, .5f, 0, .5f, -90, 0));
            } else {
                p.teleport(Utils.genLocation(p.getWorld(), Region.GREEN_SPAWN, .5f, 0, .5f, 90, 0));
            }
            // reset coins and kills
            Coins.setCoins(p, 0);
            GameCore.kills.put(p, 0);
            // reset kit selected list
            Kits.selected.clear();
            // open kit selection
            Kits.openKitGui(p);
        }
    }

    public static void spawnRedDisc() {
        Region.RED_DISC.getWorld().dropItem(Region.RED_DISC.clone().add(.5, 0, .5), new ItemStack(Material.MUSIC_DISC_PIGSTEP));
    }

    public static void spawnGreenDisc() {
        Region.GREEN_DISC.getWorld().dropItem(Region.GREEN_DISC.clone().add(.5, 0, .5), new ItemStack(Material.MUSIC_DISC_CAT));
    }

    public static void addKill(Player p) {
        if (GameCore.kills.containsKey(p)) {
            GameCore.kills.put(p, GameCore.kills.get(p) + 1);
        } else {
            GameCore.kills.put(p, 1);
        }
    }

    public static int getKills(Player p) {
        return (GameCore.kills.getOrDefault(p, 0));
    }

    public static Player getMostKills() {
        int value = Collections.max(GameCore.kills.values());
        return GameCore.kills.keySet()
                .stream()
                .filter(key -> GameCore.kills.get(key).equals(value))
                .findFirst().get();
    }
}
