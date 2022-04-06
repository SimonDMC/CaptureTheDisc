package com.simondmc.capturethedisc.region;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class Region {
    private static World world = Bukkit.getWorlds().get(0);

    // set world on game start
    public static void initWorld(World w) {
        world = w;
    }

    // center of map
    public static final Location CENTER = new Location(world, 0, 69, 0);

    // points of map with lowest and highest XYZ
    public static final Location[] MAP = {
            new Location(world, -145, 54, -50),
            new Location(world, 145, 101, 50)
    };

    // team spawnpoints
    public static final Location RED_SPAWN = new Location(world, -128, 69, 0);
    public static final Location GREEN_SPAWN = new Location(world, 128, 69, 0);

    // grace area for teams, where the other one can't go (to avoid spawncamping)
    public static final Location[] RED_GRACE = {
            new Location(world, -143, 64, -15),
            new Location(world, -113, 105, 15),
    };
    public static final Location[] GREEN_GRACE = {
            new Location(world, 113, 64, -15),
            new Location(world, 143, 105, 15),
    };

    // spawn area where you can't place blocks
    public static final Location[] RED_NO_BLOCKS = {
            new Location(world, -143, 64, -15),
            new Location(world, -88, 81, 15),
    };
    public static final Location[] GREEN_NO_BLOCKS = {
            new Location(world, 88, 64, -15),
            new Location(world, 143, 81, 15),
    };

    // disc locations
    public static final Location RED_DISC = new Location(world, -137, 79, -40);
    public static final Location GREEN_DISC = new Location(world, 137, 79, 40);

    // disc surroundings where you can't place blocks and team members can't enter
    public static final Location[] RED_DISC_AREA = {
            new Location(world, -140, 79, -43),
            new Location(world, -134, 84, -37),
    };
    public static final Location[] GREEN_DISC_AREA = {
            new Location(world, 134, 79, 37),
            new Location(world, 140, 84, 43),
    };

    // capturing platforms
    public static final Location[] GREEN_CAPTURE = {
            new Location(world, 119, 69, -1),
            new Location(world, 121, 71, 1),
    };
    public static final Location[] RED_CAPTURE = {
            new Location(world, -121, 69, -1),
            new Location(world, -119, 71, 1),
    };

    // shop npcs
    public static final Location RED_SHOP = new Location(world, -90.5, 64, 3.5, 135, 0);
    public static final Location GREEN_SHOP = new Location(world, 91.5, 64, -2.5, -45, 0);

    // kit npcs
    public static final Location RED_KITS = new Location(world, -90.5, 64, -2.5, 45, 0);
    public static final Location GREEN_KITS = new Location(world, 91.5, 64, 3.5, -135, 0);
}
