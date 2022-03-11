package com.simondmc.eventctw.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class Utils {
    public static boolean inRegion(Location l, Location[] reg) {
        if (l.getBlock().getX() < reg[0].getBlockX()) return false;
        if (l.getBlock().getY() < reg[0].getBlockY()) return false;
        if (l.getBlock().getZ() < reg[0].getBlockZ()) return false;
        if (l.getBlock().getX() > reg[1].getBlockX()) return false;
        if (l.getBlock().getY() > reg[1].getBlockY()) return false;
        return l.getBlock().getZ() <= reg[1].getBlockZ();
    }

    public static boolean inBlock(Location l1, Location l2) {
        return (l1.getBlock().getX() == l2.getBlock().getX() && l1.getBlock().getY() == l2.getBlock().getY() && l1.getBlock().getZ() == l2.getBlock().getZ());
    }

    public static void launch(Player p, Location l1, Location l2, float strength) {
        try {
            p.setVelocity(l2.toVector().subtract(l1.toVector()).normalize().setY(0.3333D).multiply(strength));
        } catch (Exception ignored) {
        }
    }

    public static Location genLocation(World world, Location loc, float addX, float addY, float addZ) {
        return new Location(world, loc.getBlockX() + addX, loc.getBlockY() + addY, loc.getBlockZ() + addZ);
    }

    public static Location genLocation(World world, Location loc, float addX, float addY, float addZ, float yaw, float pitch) {
        return new Location(world, loc.getBlockX() + addX, loc.getBlockY() + addY, loc.getBlockZ() + addZ, yaw, pitch);
    }

    public static void playSound(Player p, Sound sound) {
        p.playSound(p.getLocation(), sound, 1, 1);
    }

    public static void playSound(Player p, Sound sound, float vol, float pitch) {
        p.playSound(p.getLocation(), sound, vol, pitch);
    }

    public static Integer findMatInInventory(Player p, Material mat) {
        ItemStack[] inventory = p.getInventory().getContents();
        // not foreach loop so we can keep index
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] == null) continue;
            if (inventory[i].getType() == mat) return i;
        }
        return null;
    }
}
