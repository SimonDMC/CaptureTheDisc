package com.simondmc.capturethedisc.game;

import com.simondmc.capturethedisc.CaptureTheDisc;
import com.simondmc.capturethedisc.kits.Inventory;
import com.simondmc.capturethedisc.kits.Kits;
import com.simondmc.capturethedisc.region.Region;
import com.simondmc.capturethedisc.util.Utils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Collections;

public class GameUtils {
    public static void setupPlayers() {
        for (Player p : Teams.getPlayers()) {
            p.setGameMode(GameMode.SURVIVAL);
            p.setHealth(20);
            p.setFoodLevel(20);
            p.setSaturation(0);
            p.getInventory().clear();
            Inventory.fillInv(p);
            Location l;
            if (Teams.getRed().contains(p)) {
                l = Region.RED_SPAWN.clone();
            } else {
                l = Region.GREEN_SPAWN.clone();
            }
            l.setWorld(Region.getWorld());
            p.teleport(l);
            // reset coins and kills
            Coins.setCoins(p, 0);
            GameCore.kills.put(p, 0);
            // reset kit selected list
            Kits.selected.clear();
            // open kit selection
            Kits.openKitGui(p);
            // clear resistance
            p.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);

            // starting message
            p.sendMessage("§6§l§m---------------------------------------------");
            Utils.sendCenteredMessage(p, "§a§lCapture The Disc");
            p.sendMessage("");
            Utils.sendCenteredMessage(p, "§bWelcome to Capture The Disc!");
            Utils.sendCenteredMessage(p, "§eRun to the other base, pick up their disc and capture it.");
            Utils.sendCenteredMessage(p, "§eEarn coins by dealing damage, then buy items");
            Utils.sendCenteredMessage(p, "§eand permanent upgrades in the shop!");
            p.sendMessage("");
            Utils.sendCenteredMessage(p, "§cNote: Axes do not deal damage.");
            p.sendMessage("§6§l§m---------------------------------------------");

            // play starting sound
            Utils.playSound(p, Sound.BLOCK_NOTE_BLOCK_HARP);
        }
    }

    public static void spawnRedDisc() {
        GameCore.redDisc = Region.getWorld().dropItem(Region.RED_DISC, new ItemStack(Material.MUSIC_DISC_PIGSTEP));
        GameCore.redDisc.setGlowing(true);
        Utils.fillRegion(Region.RED_DISC_BEACON_COVER, Material.GLASS);
        if (SidebarHandler.board != null) {
            SidebarHandler.redTeam.addEntry(GameCore.redDisc.getUniqueId().toString());
            for (Player p : Teams.getPlayers()) {
                p.setScoreboard(SidebarHandler.board);
            }
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                GameCore.redDisc.setVelocity(new Vector(0, 0, 0));
            }
        }.runTaskLater(CaptureTheDisc.plugin, 1);
    }

    public static void spawnGreenDisc() {
        GameCore.greenDisc = Region.getWorld().dropItem(Region.GREEN_DISC, new ItemStack(Material.MUSIC_DISC_CAT));
        GameCore.greenDisc.setGlowing(true);
        Utils.fillRegion(Region.GREEN_DISC_BEACON_COVER, Material.GLASS);
        if (SidebarHandler.board != null) {
            SidebarHandler.greenTeam.addEntry(GameCore.greenDisc.getUniqueId().toString());
            for (Player p : Teams.getPlayers()) {
                p.setScoreboard(SidebarHandler.board);
            }
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                GameCore.greenDisc.setVelocity(new Vector(0, 0, 0));
            }
        }.runTaskLater(CaptureTheDisc.plugin, 1);
    }

    public static void addKill(Player p) {
        if (GameCore.kills.containsKey(p)) {
            GameCore.kills.put(p, GameCore.kills.get(p) + 1);
        } else {
            GameCore.kills.put(p, 1);
        }
    }

    public static int getKills(Player p) {
        return (GameCore.kills.getOrDefault(p, 0));
    }

    public static Player getMostKills() {
        int value = Collections.max(GameCore.kills.values());
        return GameCore.kills.keySet()
                .stream()
                .filter(key -> GameCore.kills.get(key).equals(value))
                .findFirst().get();
    }

    public static void captureDisc(Player p) {
        GameCore.removeDiscHolder(p);
        // clear disc from inventory
        p.getInventory().remove(Material.MUSIC_DISC_PIGSTEP);
        p.getInventory().remove(Material.MUSIC_DISC_CAT);
        // respawn disc
        if (Teams.getRed().contains(p)) {
            spawnGreenDisc();
        } else {
            spawnRedDisc();
        }
    }
}
