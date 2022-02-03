package com.simondmc.eventctw.region;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class Region {
    private static final World world = Bukkit.getWorlds().get(0);

    // center of map
    public static final Location CENTER = new Location(world, 0, 69, 0);

    // points of map with lowest and highest XYZ
    public static final Location[] MAP = {
            new Location(world, -145, 54, -50),
            new Location(world, 145, 101, 50)
    };

    // team spawnpoints
    public static final Location RED_SPAWN = new Location(world, -128, 69, 0);
    public static final Location BLUE_SPAWN = new Location(world, 128, 69, 0);

    // grace area for teams, where the other one can't go (to avoid spawncamping)
    public static final Location[] RED_GRACE = {
            new Location(world, -143, 64, -15),
            new Location(world, -113, 79, 15),
    };
    public static final Location[] BLUE_GRACE = {
            new Location(world, 113, 64, -15),
            new Location(world, 143, 79, 15),
    };

    // spawn area where you can't place blocks
    public static final Location[] RED_NO_BLOCKS = {
            new Location(world, -143, 64, -15),
            new Location(world, -88, 81, 15),
    };
    public static final Location[] BLUE_NO_BLOCKS = {
            new Location(world, 88, 64, -15),
            new Location(world, 143, 81, 15),
    };
}
