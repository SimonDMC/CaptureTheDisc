package com.simondmc.eventctw.game;

import com.simondmc.eventctw.region.Region;
import com.simondmc.eventctw.util.Utils;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class GameCore {
    private static boolean running = false;
    public static final HashMap<Player, Integer> dead = new HashMap<>();

    public static void startGame() {
        running = true;
    }
    public static void stopGame() {
        running = false;
        for (Player p : Teams.getPlayers()) {
            p.setDisplayName(p.getName());
        }
        Teams.getRed().clear();
        Teams.getBlue().clear();
        Teams.getPlayers().clear();
    }
    public static boolean isOn() {
        return running;
    }
    public static void setup(Player p) {
        // gamerules, weather and time
        p.getWorld().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        p.getWorld().setGameRule(GameRule.DO_MOB_SPAWNING, false);
        p.getWorld().setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        p.getWorld().setTime(1000);
        p.getWorld().setStorm(false);
        // remove all entities on map
        GameUtils.clearEntities(p);
        // assign teams randomly
        Teams.assignTeams();
        // setup all player things, tp, inventory, etc
        GameUtils.setupPlayers();
    }

    public static void respawn(Player p) {
        p.setHealth(20);
        p.setFoodLevel(20);
        p.setSaturation(0);
        p.getInventory().clear();
        p.setGameMode(GameMode.SURVIVAL);
        GameUtils.fillInv(p);
        if (Teams.getRed().contains(p)) {
            p.teleport(Utils.genLocation(p.getWorld(), Region.RED_SPAWN, .5f, 0, .5f, -90, 0));
        } else {
            p.teleport(Utils.genLocation(p.getWorld(), Region.BLUE_SPAWN, .5f, 0, .5f, 90, 0));
        }
    }
    public static void die(Player p) {
        p.setGameMode(GameMode.SPECTATOR);
        dead.put(p, 100); // 100 ticks = 5 seconds
        p.teleport(Utils.genLocation(p.getWorld(), Region.CENTER, .5f, 20, .5f));
    }
}
