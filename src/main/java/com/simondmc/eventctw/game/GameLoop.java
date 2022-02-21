package com.simondmc.eventctw.game;

import com.simondmc.eventctw.EventCTW;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

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
                    if (!GameCore.dead.containsKey(p) && Teams.getPlayers().contains(p)) {
                        ActionBarHandler.displayCoins(p);
                    }
                }
            }
        }.runTaskTimer(EventCTW.plugin, 0, 5);
    }
}
