package com.simondmc.capturethedisc.region;

import org.bukkit.Location;
import org.bukkit.World;

public class Region {
    private static World world;

    // set world on game start
    public static void initWorld(World w) {
        world = w;
    }

    public static World getWorld() {
        return world;
    }

    // center of map
    public static final Location CENTER = new Location(null, 0, 69, 0);

    // points of map with lowest and highest XYZ
    public static final Location[] MAP = {
            new Location(null, -145, 54, -50),
            new Location(null, 145, 101, 50)
    };

    // team spawnpoints
    public static final Location RED_SPAWN = new Location(null, -128, 69, 0);
    public static final Location GREEN_SPAWN = new Location(null, 128, 69, 0);

    // grace area for teams, where the other one can't go (to avoid spawncamping)
    public static final Location[] RED_GRACE = {
            new Location(null, -143, 64, -15),
            new Location(null, -113, 105, 15),
    };
    public static final Location[] GREEN_GRACE = {
            new Location(null, 113, 64, -15),
            new Location(null, 143, 105, 15),
    };

    // spawn area where you can't place blocks
    public static final Location[] RED_NO_BLOCKS = {
            new Location(null, -143, 64, -15),
            new Location(null, -88, 81, 15),
    };
    public static final Location[] GREEN_NO_BLOCKS = {
            new Location(null, 88, 64, -15),
            new Location(null, 143, 81, 15),
    };

    // disc locations
    public static final Location RED_DISC = new Location(null, -137, 79, -40);
    public static final Location GREEN_DISC = new Location(null, 137, 79, 40);

    // disc surroundings where you can't place blocks and team members can't enter
    public static final Location[] RED_DISC_AREA = {
            new Location(null, -140, 79, -43),
            new Location(null, -134, 84, -37),
    };
    public static final Location[] GREEN_DISC_AREA = {
            new Location(null, 134, 79, 37),
            new Location(null, 140, 84, 43),
    };

    // capturing platforms
    public static final Location[] GREEN_CAPTURE = {
            new Location(null, 119, 69, -1),
            new Location(null, 121, 71, 1),
    };
    public static final Location[] RED_CAPTURE = {
            new Location(null, -121, 69, -1),
            new Location(null, -119, 71, 1),
    };

    // shop npcs
    public static final Location RED_SHOP = new Location(null, -90.5, 64, 3.5, 135, 0);
    public static final Location GREEN_SHOP = new Location(null, 91.5, 64, -2.5, -45, 0);

    // kit npcs
    public static final Location RED_KITS = new Location(null, -90.5, 64, -2.5, 45, 0);
    public static final Location GREEN_KITS = new Location(null, 91.5, 64, 3.5, -135, 0);

    // waiting lobby
    public static final Location LOBBY = new Location(null, 0.5, 69, 0.5);
}
