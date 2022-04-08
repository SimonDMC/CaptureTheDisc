package com.simondmc.capturethedisc.game;

import com.simondmc.capturethedisc.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class SidebarHandler {

    /*
        CAPTURE THE DISC
                            7
        Game Time: 3:17     6
                            5
        Red Disc: Safe      4
        Green Disc: Held    3
                            2
        Coins: 5¢           1
        Kills: 7⚔           0
     */

    public static void createSidebar(Player p) {
        // CREATE SIDEBAR
        Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        if (board.getObjective("ctd") != null) board.getObjective("ctd").unregister();
        Objective objective = board.registerNewObjective("ctd", "dummy", "  §a§lCapture The Disc  ");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        // EMPTY LINES
        objective.getScore("§1").setScore(7);
        objective.getScore("§2").setScore(5);
        objective.getScore("§3").setScore(2);

        // GAME TIME
        long time = System.currentTimeMillis() - GameCore.startTime;
        objective.getScore("§eGame Time: §a" + Utils.convertMillisToMSS(time)).setScore(6);

        // DISCS
        if (GameCore.existsRedDiscHolder()) {
            objective.getScore("§cRed Disc: §c§lHeld").setScore(4);
        } else {
            objective.getScore("§cRed Disc: §a§lSafe").setScore(4);
        }
        if (GameCore.existsGreenDiscHolder()) {
            objective.getScore("§aGreen Disc: §c§lHeld").setScore(3);
        } else {
            objective.getScore("§aGreen Disc: §a§lSafe").setScore(3);
        }

        // COINS AND KILLS
        float coins = Coins.getCoins(p);
        int kills = GameUtils.getKills(p);
        // remove decimal point if whole number (or like 0.0000000004 or whatever floating point precision is)
        if (Math.round(coins * 10) % 10 == 0) {
            objective.getScore("§6Coins: " + Math.round(coins) + "¢").setScore(1);
        } else {
            objective.getScore("§6Coins: " + Math.round(coins * 10) / 10f + "¢").setScore(1);
        }
        objective.getScore("§eKills: " + kills + "⚔").setScore(0);

        // SET SIDEBAR
        p.setScoreboard(board);
    }

    public static void reset() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }
    }
}
