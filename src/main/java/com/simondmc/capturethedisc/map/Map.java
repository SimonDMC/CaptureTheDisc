package com.simondmc.capturethedisc.map;

import com.simondmc.capturethedisc.CaptureTheDisc;
import com.simondmc.capturethedisc.region.Region;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.io.File;
import java.io.IOException;

public class Map {
    private static final CaptureTheDisc plugin = CaptureTheDisc.plugin;

    public static void createMap() {
        try {
            // GET PATHS
            String mapPath = plugin.getDataFolder() + File.separator + "world.zip";
            String spigotFolder = plugin.getDataFolder().getAbsoluteFile().getParentFile().getParent();

            // REMOVE OLD MAP IF IT EXISTS
            File oldWorld = new File(spigotFolder + File.separator + "ctd-world");
            if (oldWorld.exists()) {
                // TP EVERYONE OUT SO SERVER CAN UNLOAD WORLD
                for (Player p : plugin.getServer().getOnlinePlayers()) {
                    if (p.getWorld().getName().equals("ctd-world")) {
                        World w = Bukkit.getWorlds().get(0).getName().equals("ctd-world") ? Bukkit.getWorlds().get(1) : Bukkit.getWorlds().get(0);
                        Location l = Region.LOBBY.clone();
                        l.setWorld(w);
                        p.teleport(l);
                    }
                }
                plugin.getServer().unloadWorld("ctd-world", false);
                FileUtils.deleteDirectory(oldWorld);
                System.out.println("[CaptureTheDisc] Old world replaced");
            }

            // UNZIP MAP
            Zip.unzip(mapPath, spigotFolder);

            // REGISTER WORLD
            plugin.getServer().createWorld(new WorldCreator("ctd-world"));

            // TP EVERYONE INTO THE MAP AND RESET THEM
            for (Player p : plugin.getServer().getOnlinePlayers()) {
                Location l = Region.LOBBY.clone();
                l.setWorld(Bukkit.getWorld("ctd-world"));
                p.teleport(l);
                p.setGameMode(GameMode.ADVENTURE);
                p.getInventory().clear();
                p.getInventory().setArmorContents(null);
                p.getInventory().setItemInOffHand(null);
                p.setHealth(20);
                p.setFoodLevel(20);
                p.setSaturation(20);
                p.setExp(0);
                p.setLevel(0);
                p.setFireTicks(0);
                p.setFallDistance(0);
                p.setAllowFlight(false);
                p.setFlying(false);
                p.setBedSpawnLocation(l, true);
                for (PotionEffect eff : p.getActivePotionEffects()) {
                    p.removePotionEffect(eff.getType());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
