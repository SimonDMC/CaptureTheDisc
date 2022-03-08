package com.simondmc.eventctw.game;

import com.simondmc.eventctw.region.Region;
import com.simondmc.eventctw.util.Utils;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class GameCore {
    private static boolean running = false;
    public static final HashMap<Player, Integer> dead = new HashMap<>();
    private static Player redDiscHolder, blueDiscHolder;

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
        // PRIORITY!!! initialize world
        Region.initWorld(p.getWorld());

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
        // spawn discs in team bases
        GameUtils.spawnRedDisc();
        GameUtils.spawnBlueDisc();
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

        if (isDiscHolder(p)) {
            for (Player player : Teams.getPlayers()) {
                Utils.playSound(player, Sound.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF);
                // blue disc holder
                if (Teams.getRed().contains(p)) {
                    p.sendMessage("§eThe §9BLUE §edisc was dropped!");
                }
                // red disc holder
                if (Teams.getBlue().contains(p)) {
                    p.sendMessage("§eThe §cRED §edisc was dropped!");
                }
            }

            if (Teams.getRed().contains(p)) {
                blueDiscHolder = null;
                GameUtils.spawnBlueDisc();
            }
            if (Teams.getBlue().contains(p)) {
                redDiscHolder = null;
                GameUtils.spawnRedDisc();
            }
        }
    }
    public static boolean isDiscHolder(Player p) {
        return redDiscHolder.equals(p) || blueDiscHolder.equals(p);
    }
    public static void setDiscHolder(Player p) {
        if (Teams.getRed().contains(p)) blueDiscHolder = p;
        if (Teams.getBlue().contains(p)) redDiscHolder = p;
    }
}
