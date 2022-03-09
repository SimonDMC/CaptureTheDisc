package com.simondmc.eventctw.game;

import com.simondmc.eventctw.EventCTW;
import com.simondmc.eventctw.region.Region;
import com.simondmc.eventctw.util.Utils;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

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
            fillInv(p);
            if (Teams.getRed().contains(p)) {
                p.teleport(Utils.genLocation(p.getWorld(), Region.RED_SPAWN, .5f, 0, .5f, -90, 0));
            } else {
                p.teleport(Utils.genLocation(p.getWorld(), Region.BLUE_SPAWN, .5f, 0, .5f, 90, 0));
            }
        }
    }

    public static void fillInv(Player p) {
        // TODO: rewrite all of this jesus christ
        ItemStack i;
        ItemMeta m;
        LeatherArmorMeta leather;

        i = new ItemStack(Material.STONE_SWORD);
        m = i.getItemMeta();
        m.setUnbreakable(true);
        i.setItemMeta(m);

        p.getInventory().setItem(0, i);

        i = new ItemStack(Material.BOW);
        m = i.getItemMeta();
        m.setUnbreakable(true);
        i.setItemMeta(m);

        p.getInventory().setItem(1, i);
        p.getInventory().setItem(2, new ItemStack(Material.OAK_PLANKS, 64));
        p.getInventory().setItem(3, new ItemStack(Material.OAK_PLANKS, 64));
        p.getInventory().setItem(4, new ItemStack(Material.OAK_PLANKS, 64));
        p.getInventory().setItem(5, new ItemStack(Material.AIR));

        i = new ItemStack(Material.GOLDEN_APPLE);
        m = i.getItemMeta();
        m.setDisplayName("§rGolden Apple");
        i.setItemMeta(m);

        p.getInventory().setItem(6, i);

        i = new ItemStack(Material.STONE_AXE);
        m = i.getItemMeta();
        m.setUnbreakable(true);
        i.setItemMeta(m);

        p.getInventory().setItem(7, i);

        i = new ItemStack(Material.ARROW, 8);
        m = i.getItemMeta();
        m.setDisplayName("§rArrow");
        i.setItemMeta(m);

        p.getInventory().setItem(8, i);

        Color color = Teams.getRed().contains(p) ? Color.fromRGB(255, 0, 0) : Color.fromRGB(0, 0, 255);

        i = new ItemStack(Material.LEATHER_HELMET);
        leather = (LeatherArmorMeta) i.getItemMeta();
        leather.setColor(color);
        i.setItemMeta(leather);
        p.getInventory().setHelmet(i);
        i.setType(Material.LEATHER_CHESTPLATE);
        p.getInventory().setChestplate(i);
        i.setType(Material.LEATHER_LEGGINGS);
        p.getInventory().setLeggings(i);
        i.setType(Material.LEATHER_BOOTS);
        p.getInventory().setBoots(i);
    }

    public static void spawnRedDisc() {
        Item disc = Region.RED_DISC.getWorld().dropItem(Region.RED_DISC.add(.5, 0, .5), new ItemStack(Material.MUSIC_DISC_PIGSTEP));
        disc.setGravity(false);
        new BukkitRunnable() {
            @Override
            public void run() {
                disc.setVelocity(new Vector(0, 0, 0));
            }
        }.runTaskLater(EventCTW.plugin, 1);
    }

    public static void spawnBlueDisc() {
        Item disc = Region.BLUE_DISC.getWorld().dropItem(Region.BLUE_DISC.add(.5, 0, .5), new ItemStack(Material.MUSIC_DISC_CAT));
        disc.setGravity(false);
        new BukkitRunnable() {
            @Override
            public void run() {
                disc.setVelocity(new Vector(0, 0, 0));
            }
        }.runTaskLater(EventCTW.plugin, 1);
    }
}
