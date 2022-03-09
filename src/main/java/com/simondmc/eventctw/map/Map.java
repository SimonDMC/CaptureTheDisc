package com.simondmc.eventctw.map;

import com.simondmc.eventctw.region.Region;
import com.sk89q.worldedit.WorldEditException;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.io.IOException;

public class Map {
    // passing a player to get world
    public static void pasteMapSchem(Player p) {
        Location l = new Location(p.getWorld(), Region.CENTER.getBlockX(), Region.CENTER.getBlockY(), Region.CENTER.getBlockZ());
        try {
            Schematic.loadSchematic("map.schem", l, false);
        } catch (IOException | WorldEditException e) {
            e.printStackTrace();
        }
    }

    public static void deconstructMap(Player p) {
        for (int x = Region.MAP[0].getBlockX(); x <= Region.MAP[1].getBlockX(); x++) {
            for (int y = Region.MAP[0].getBlockY(); y <= Region.MAP[1].getBlockY(); y++) {
                for (int z = Region.MAP[0].getBlockZ(); z <= Region.MAP[1].getBlockZ(); z++) {
                    p.getWorld().getBlockAt(x, y, z).setType(Material.AIR);
                }
            }
        }
    }
}
