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
    public static final Location CENTER = new Location(null, 122, -38, -149);

    // points of map with lowest and highest XYZ
    public static final Location[] MAP = {
            new Location(null, 53, -42, -299),
            new Location(null, 191, -16, 1)
    };

    // team spawnpoints
    public static final Location RED_SPAWN = new Location(null, 122.5, -34, -294.5, 0, 0);
    public static final Location GREEN_SPAWN = new Location(null, 122.5, -34, -2.5, 180, 0);

    // grace area for teams, where the other one can't go (to avoid spawncamping)
    public static final Location[] RED_GRACE = {
            new Location(null, 112, -34, -300),
            new Location(null, 132, 10, -291),
    };
    public static final Location[] GREEN_GRACE = {
            new Location(null, 112, -34, -7),
            new Location(null, 132, 10, 2),
    };

    // spawn area where you can't place blocks
    public static final Location[] RED_NO_BLOCKS = {
            new Location(null, 112, -35, -300),
            new Location(null, 132, 10, -284),
    };
    public static final Location[] GREEN_NO_BLOCKS = {
            new Location(null, 112, -35, -14),
            new Location(null, 132, 10, 2),
    };

    // disc locations
    public static final Location RED_DISC = new Location(null, 178, -23, -271);
    public static final Location GREEN_DISC = new Location(null, 67, -23, -26);

    // disc surroundings where you can't place blocks and team members can't enter
    public static final Location[] RED_DISC_AREA = {
            new Location(null, 176, -23, -273),
            new Location(null, 179, -19, -270),
    };
    public static final Location[] GREEN_DISC_AREA = {
            new Location(null, 65, -23, -28),
            new Location(null, 68, -19, -25),
    };

    // capturing platforms
    public static final Location[] GREEN_CAPTURE = {
            new Location(null, 112, -34, -7),
            new Location(null, 132, -32, 2),
    };
    public static final Location[] RED_CAPTURE = {
            new Location(null, 112, -34, -300),
            new Location(null, 132, -32, -291),
    };

    // void level
    public static final int VOID_LEVEL = -44;

    // shop npcs
    public static final Location RED_SHOP = new Location(null, 118.5, -35, -285.5, -135, 0);
    public static final Location GREEN_SHOP = new Location(null, 126.5, -35, -11.5, 45, 0);

    // kit npcs
    public static final Location RED_KITS = new Location(null, 126.5, -35, -285.5, 135, 0);
    public static final Location GREEN_KITS = new Location(null, 118.5, -35, -11.5, -45, 0);

    // waiting lobby
    public static final Location LOBBY = new Location(null, 122.5, -38, -148.5);
}
