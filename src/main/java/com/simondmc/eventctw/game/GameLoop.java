package com.simondmc.eventctw.game;

import com.simondmc.eventctw.EventCTW;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
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

                    // ACTIONBAR HANDLER
                    // TODO: move this into its own class
                    // https://www.spigotmc.org/threads/257845/
                    float coins = Coins.getCoins(p);
                    // remove decimal point if whole number
                    if (coins == Math.round(coins)) p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§6" + Math.round(coins) + "¢"));
                    // hopefully get rid of floating point precision error istg
                    else p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§6" + Math.round(coins*10)/10f + "¢"));
                }
            }
        }.runTaskTimer(EventCTW.plugin, 0, 5);
    }
}
