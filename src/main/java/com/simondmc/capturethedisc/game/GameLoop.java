package com.simondmc.capturethedisc.game;

import com.simondmc.capturethedisc.CaptureTheDisc;
import com.simondmc.capturethedisc.kits.Kit;
import com.simondmc.capturethedisc.kits.Kits;
import com.simondmc.capturethedisc.region.Region;
import com.simondmc.capturethedisc.util.Performance;
import com.simondmc.capturethedisc.util.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.stream.Collectors;

public class GameLoop {

    public static void gameLoop() {
        // EVERY 5 TICKS (4 loops/s)
        new BukkitRunnable() {

            @Override
            public void run() {
                if (!GameCore.isOn()) return;
                Performance.start("5t/loop");

                for (Player p : Teams.getPlayers()) {

                    // DEATH SYSTEM
                    if (GameCore.dead.containsKey(p)) {
                        int remainingTicks = GameCore.dead.get(p);
                        if (remainingTicks <= 5) {
                            GameCore.dead.remove(p);
                            GameCore.respawn(p);
                            continue;
                        }
                        GameCore.dead.put(p, remainingTicks - 5);
                        // yeah idk numbers are strange
                        p.sendTitle("§c" + Math.round(Math.ceil((float) remainingTicks / 20)), "", 0, 10, 0);
                    }

                    // NEGATIVE COINS PATCH (sometimes it subtracts coins past 0 but doesn't give item, purely visual)
                    if (Coins.getCoins(p) < 0) {
                        Coins.addCoins(p, -Coins.getCoins(p));
                    }
                }

                // PARTICLES ON SHOP
                for (Location l : new Location[]{Region.RED_SHOP, Region.GREEN_SHOP}) {
                    Region.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, l.clone().add(0, 2.7, 0), 5);
                }

                Performance.stop("5t/loop");
            }
        }.runTaskTimer(CaptureTheDisc.plugin, 0, 5);

        // EVERY 20 TICKS (1 loop/s)
        new BukkitRunnable() {

            @Override
            public void run() {
                if (!GameCore.isOn()) return;
                Performance.start("20t/loop");

                // SIDEBAR & ACTIONBAR
                for (Player p : Teams.getPlayers()) {
                    SidebarHandler.createSidebar(p);
                    ActionBarHandler.displayStats(p);
                }

                Performance.stop("20t/loop");
            }
        }.runTaskTimer(CaptureTheDisc.plugin, 0, 20);

        // EVERY 40 TICKS (2 s/loop)
        new BukkitRunnable() {

            @Override
            public void run() {
                if (!GameCore.isOn()) return;
                Performance.start("40t/loop");

                // give blocks
                for (Player p : Teams.getPlayers()) {
                    if (GameCore.dead.containsKey(p)) continue;
                    int blockCount = Utils.countItems(Material.BIRCH_PLANKS, p);
                    if (blockCount < 64) {
                        p.getInventory().addItem(new ItemStack(Material.BIRCH_PLANKS));
                    }
                }

                // remove dupe discs

                Location l = Region.RED_DISC.clone();
                l.setWorld(Region.getWorld());

                List<Entity> red_discs = Region.getWorld().getNearbyEntities(l, 3, 3, 3).stream().filter(e -> e instanceof Item).collect(Collectors.toList());
                if (red_discs.size() > 1) {
                    for (Entity e : red_discs) e.remove();
                    GameUtils.spawnRedDisc();
                }

                l = Region.GREEN_DISC.clone();
                l.setWorld(Region.getWorld());

                List<Entity> green_discs = Region.getWorld().getNearbyEntities(l, 3, 3, 3).stream().filter(e -> e instanceof Item).collect(Collectors.toList());
                if (green_discs.size() > 1) {
                    for (Entity e : green_discs) e.remove();
                    GameUtils.spawnGreenDisc();
                }

                Performance.stop("40t/loop");
            }
        }.runTaskTimer(CaptureTheDisc.plugin, 0, 40);

        // EVERY 200 TICKS (10 s/loop)
        new BukkitRunnable() {

            @Override
            public void run() {
                if (!GameCore.isOn()) return;
                Performance.start("200t/loop");

                // give arrow to every bow guy
                for (Player p : Kits.getKitMembers(Kit.ARCHER)) {
                    int arrowCount = Utils.countItems(Material.ARROW, p);
                    if (arrowCount < 3) {
                        p.getInventory().addItem(new ItemStack(Material.ARROW));
                    }
                }

                Performance.stop("200t/loop");
            }
        }.runTaskTimer(CaptureTheDisc.plugin, 0, 200);
    }
}
