package com.simondmc.capturethedisc.map;

import com.simondmc.capturethedisc.CaptureTheDisc;
import com.simondmc.capturethedisc.region.Region;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;

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
                deleteDir(oldWorld);
                plugin.getLogger().log(Level.INFO, "Old world replaced");
            }

            // UNZIP MAP
            Zip.unzip(mapPath, spigotFolder);

            // REGISTER WORLD
            plugin.getServer().createWorld(new WorldCreator("ctd-world"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // https://stackoverflow.com/a/29175213
    private static void deleteDir(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                if (! Files.isSymbolicLink(f.toPath())) {
                    deleteDir(f);
                }
            }
        }
        file.delete();
    }
}
