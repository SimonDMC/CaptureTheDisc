package com.simondmc.eventctw.game;

import com.simondmc.eventctw.EventCTW;
import com.simondmc.eventctw.region.Region;
import com.simondmc.eventctw.util.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.stream.Collectors;

public class GameLoop {

    public static void gameLoop() {
        new BukkitRunnable() {

            @Override
            public void run() {
                if (!GameCore.isOn()) return;

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

                    // ACTION BAR
                    if (!GameCore.dead.containsKey(p) && Teams.getPlayers().contains(p)) {
                        ActionBarHandler.displayCoins(p);
                    }

                    // NEGATIVE COINS PATCH (sometimes it subtracts coins past 0 but doesn't give item, purely visual)
                    if (Coins.getCoins(p) < 0) {
                        Coins.addCoins(p, -Coins.getCoins(p));
                    }
                }
            }
        }.runTaskTimer(EventCTW.plugin, 0, 5);

        new BukkitRunnable() {

            @Override
            public void run() {
                if (!GameCore.isOn()) return;

                // remove dupe discs
                List<Entity> red_discs = Region.RED_DISC.getWorld().getNearbyEntities(Region.RED_DISC, 1, 1, 1).stream().filter(e -> e instanceof Item).collect(Collectors.toList());
                if (red_discs.size() > 1) {
                    for (Entity e : red_discs) e.remove();
                    GameUtils.spawnRedDisc();
                }
                List<Entity> blue_discs = Region.BLUE_DISC.getWorld().getNearbyEntities(Region.BLUE_DISC, 1, 1, 1).stream().filter(e -> e instanceof Item).collect(Collectors.toList());
                if (blue_discs.size() > 1) {
                    for (Entity e : blue_discs) e.remove();
                    GameUtils.spawnBlueDisc();
                }
            }
        }.runTaskTimer(EventCTW.plugin, 0, 20);

        new BukkitRunnable() {

            @Override
            public void run() {
                if (!GameCore.isOn()) return;

                // give blocks
                for (Player p : Teams.getPlayers()) {
                    int blockCount = Utils.countItems(Material.OAK_PLANKS, p);
                    if (blockCount < 64) {
                        p.getInventory().addItem(new ItemStack(Material.OAK_PLANKS));
                    }
                }

            }
        }.runTaskTimer(EventCTW.plugin, 0, 40);
    }
}
