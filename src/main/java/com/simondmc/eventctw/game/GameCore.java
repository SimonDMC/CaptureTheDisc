package com.simondmc.eventctw.game;

import com.simondmc.eventctw.kits.Inventory;
import com.simondmc.eventctw.kits.KitNPC;
import com.simondmc.eventctw.kits.Kits;
import com.simondmc.eventctw.region.Region;
import com.simondmc.eventctw.shop.ShopGUI;
import com.simondmc.eventctw.shop.ShopNPC;
import com.simondmc.eventctw.util.Config;
import com.simondmc.eventctw.util.Utils;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.HashMap;

public class GameCore {
    private static boolean running = false;
    public static final HashMap<Player, Integer> dead = new HashMap<>();
    public static final HashMap<Player, Integer> kills = new HashMap<>();
    public static final HashMap<Player, TimestampHit> lastDamage = new HashMap<>();
    public static long startTime;
    private static Player redDiscHolder, greenDiscHolder;

    public static void startGame() {
        running = true;
    }

    public static void stopGame() {
        running = false;
        // devinfo
        Config.devAnnounce("§aGame finished! Took " + Math.round((System.currentTimeMillis() - GameCore.startTime)/1000) + "s.");
        // stats
        for (Player player : Teams.getPlayers()) {
            player.sendMessage("§e§lMost kills: §a" + GameUtils.getMostKills().getName() + " §7- §c" + GameUtils.getKills(GameUtils.getMostKills()) + "⚔");
            player.sendMessage("§7Your kills: §c" + GameUtils.getKills(player) +"⚔");
        }
        for (Player p : Teams.getPlayers()) {
            p.setDisplayName(p.getName());
            p.setGameMode(GameMode.SURVIVAL);
            // reset disc holder
            removeDiscHolder(p);
            // remove all active effects
            for (PotionEffect eff : p.getActivePotionEffects()) p.removePotionEffect(eff.getType());
        }
        // reset teams
        Teams.getRed().clear();
        Teams.getGreen().clear();
        Teams.getPlayers().clear();
        for (String player : Teams.getRedTeam().getEntries()) {
            Teams.getRedTeam().removeEntry(player);
        }
        for (String player : Teams.getGreenTeam().getEntries()) {
            Teams.getGreenTeam().removeEntry(player);
        }
        // reset offline players
        Teams.getOffline().clear();
        // reset kits
        Kits.resetKits();
        // reset death timer
        dead.clear();
        // reset kills
        GameCore.kills.clear();
    }

    public static boolean isOn() {
        return running;
    }

    public static void setup(Player p) {
        // PRIORITY!!! initialize world
        Region.initWorld(p.getWorld());

        // set start time for devinfo
        startTime = System.currentTimeMillis();
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
        GameUtils.spawnGreenDisc();
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
            p.teleport(Utils.genLocation(p.getWorld(), Region.GREEN_SPAWN, .5f, 0, .5f, 90, 0));
        }
    }

    public static void die(Player p) {
        // figure out if kill
        if (lastDamage.containsKey(p)) {
            // make sure last hit was less than 10 seconds ago
            if (System.currentTimeMillis() - lastDamage.get(p).timestamp < 10000) {// 10,000ms = 10s
                Player damager = lastDamage.get(p).damager;
                String dColor = (Teams.getRed().contains(damager) ? "§c" : "§a");
                String pColor = (Teams.getRed().contains(p) ? "§c" : "§a");
                damager.sendMessage(pColor + p.getName() + " §ewas killed by " + dColor + damager.getName());
                Utils.playSound(damager, Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
                GameUtils.addKill(damager);
            }
        }

        // remove all active effects
        for (PotionEffect eff : p.getActivePotionEffects()) p.removePotionEffect(eff.getType());

        p.setGameMode(GameMode.SPECTATOR);
        dead.put(p, 100); // 100 ticks = 5 seconds
        p.teleport(Region.CENTER.clone().add(.5, 20, .5));

        if (isDiscHolder(p)) {
            for (Player player : Teams.getPlayers()) {
                Utils.playSound(player, Sound.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF);
                // green disc holder
                if (Teams.getRed().contains(p)) {
                    player.sendMessage("§eThe §a§lGREEN §edisc was dropped!");
                }
                // red disc holder
                if (Teams.getGreen().contains(p)) {
                    player.sendMessage("§eThe §c§lRED §edisc was dropped!");
                }
            }
            removeDiscHolder(p);
        }
    }

    public static boolean isDiscHolder(Player p) {
        return redDiscHolder == p || greenDiscHolder == p;
    }

    public static void setDiscHolder(Player p) {
        if (Teams.getRed().contains(p)) greenDiscHolder = p;
        if (Teams.getGreen().contains(p)) redDiscHolder = p;
        p.setGlowing(true);
    }

    public static void removeDiscHolder(Player p) {
        if (redDiscHolder == p) {
            redDiscHolder = null;
            if (isOn()) GameUtils.spawnRedDisc();
        }
        if (greenDiscHolder == p) {
            greenDiscHolder = null;
            if (isOn()) GameUtils.spawnGreenDisc();
        }
        p.setGlowing(false);
    }
}
