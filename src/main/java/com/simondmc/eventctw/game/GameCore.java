package com.simondmc.eventctw.game;

import com.simondmc.eventctw.kits.Inventory;
import com.simondmc.eventctw.kits.KitNPC;
import com.simondmc.eventctw.region.Region;
import com.simondmc.eventctw.shop.ShopGUI;
import com.simondmc.eventctw.shop.ShopNPC;
import com.simondmc.eventctw.util.Utils;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class GameCore {
    private static boolean running = false;
    public static final HashMap<Player, Integer> dead = new HashMap<>();
    public static final HashMap<Player, Integer> kills = new HashMap<>();
    public static final HashMap<Player, TimestampHit> lastDamage = new HashMap<>();
    private static Player redDiscHolder, blueDiscHolder;

    public static void startGame() {
        running = true;
    }

    public static void stopGame() {
        running = false;
        for (Player p : Teams.getPlayers()) {
            p.setDisplayName(p.getName());
            // reset disc holder
            removeDiscHolder(p);
        }
        // reset teams
        Teams.getRed().clear();
        Teams.getBlue().clear();
        Teams.getPlayers().clear();
        for (String player : Teams.getRedTeam().getEntries()) {
            Teams.getRedTeam().removeEntry(player);
        }
        for (String player : Teams.getBlueTeam().getEntries()) {
            Teams.getBlueTeam().removeEntry(player);
        }
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
        // initalize all player upgrades
        ShopGUI.initUpgrades();
        // setup all player things, tp, inventory, etc
        GameUtils.setupPlayers();
        // spawn discs in team bases
        GameUtils.spawnRedDisc();
        GameUtils.spawnBlueDisc();
        // spawn npcs
        ShopNPC.initShopNpc();
        KitNPC.initKitNpcs();
    }

    public static void respawn(Player p) {
        p.setHealth(20);
        p.setFoodLevel(20);
        p.setSaturation(0);
        p.getInventory().clear();
        p.setGameMode(GameMode.SURVIVAL);
        Inventory.fillInv(p);
        if (Teams.getRed().contains(p)) {
            p.teleport(Utils.genLocation(p.getWorld(), Region.RED_SPAWN, .5f, 0, .5f, -90, 0));
        } else {
            p.teleport(Utils.genLocation(p.getWorld(), Region.BLUE_SPAWN, .5f, 0, .5f, 90, 0));
        }
    }

    public static void die(Player p) {
        // figure out if kill
        if (lastDamage.containsKey(p)) {
            // make sure last hit was less than 10 seconds ago
            if (System.currentTimeMillis() - lastDamage.get(p).timestamp > 10000) return; // 10,000ms = 10s
            Player damager = lastDamage.get(p).damager;
            String dColor = (Teams.getRed().contains(damager) ? "§c" : "§9");
            String pColor = (Teams.getRed().contains(p) ? "§c" : "§9");
            damager.sendMessage(pColor + p.getName() + " §ewas killed by " + dColor + damager.getName());
            Utils.playSound(damager, Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
            GameUtils.addKill(damager);
        }


        p.setGameMode(GameMode.SPECTATOR);
        dead.put(p, 100); // 100 ticks = 5 seconds
        p.teleport(Region.CENTER.clone().add(.5, 20, .5));

        if (isDiscHolder(p)) {
            for (Player player : Teams.getPlayers()) {
                Utils.playSound(player, Sound.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF);
                // blue disc holder
                if (Teams.getRed().contains(p)) {
                    player.sendMessage("§eThe §9§lBLUE §edisc was dropped!");
                }
                // red disc holder
                if (Teams.getBlue().contains(p)) {
                    player.sendMessage("§eThe §c§lRED §edisc was dropped!");
                }
            }
            removeDiscHolder(p);
        }
    }

    public static boolean isDiscHolder(Player p) {
        return redDiscHolder == p || blueDiscHolder == p;
    }

    public static void setDiscHolder(Player p) {
        if (Teams.getRed().contains(p)) blueDiscHolder = p;
        if (Teams.getBlue().contains(p)) redDiscHolder = p;
        p.setGlowing(true);
    }

    public static void removeDiscHolder(Player p) {
        if (redDiscHolder == p) {
            redDiscHolder = null;
            if (isOn()) GameUtils.spawnRedDisc();
        }
        if (blueDiscHolder == p) {
            blueDiscHolder = null;
            if (isOn()) GameUtils.spawnBlueDisc();
        }
        p.setGlowing(false);
    }
}
