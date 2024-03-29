package com.simondmc.capturethedisc.game;

import com.simondmc.capturethedisc.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class SidebarHandler {

    public static Scoreboard board;
    public static Team redTeam, greenTeam;

    /*
        CAPTURE THE DISC
                              10
        Game Time: 3:17        9
                               8
        Red Disc: Safe         7
        Green Disc: Held       6
                               5
        Red Captures: 0/3      4
        Green Captures: 0/3    3
                               2
        Coins: 5¢              1
        Kills: 7⚔              0
     */

    public static void createSidebar(Player p) {
        // CREATE SIDEBAR
        board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = board.registerNewObjective("ctd", "dummy", "  §a§lCapture The Disc  ");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        // SET TEAMS FOR GLOWING AND COLORED NAME
        redTeam = board.registerNewTeam("redCTD");
        greenTeam = board.registerNewTeam("greenCTD");
        redTeam.setColor(ChatColor.RED);
        greenTeam.setColor(ChatColor.GREEN);

        // DISABLE COLLISIONS
        redTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        greenTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);

        for (Player red : Teams.getRed()) {
            redTeam.addEntry(red.getName());
        }
        for (Player green : Teams.getGreen()) {
            greenTeam.addEntry(green.getName());
        }

        // ADD DISCS TO COLOR GLOWS
        redTeam.addEntry(GameCore.redDisc.getUniqueId().toString());
        greenTeam.addEntry(GameCore.greenDisc.getUniqueId().toString());

        // EMPTY LINES
        objective.getScore("§1").setScore(10);
        objective.getScore("§2").setScore(8);
        objective.getScore("§3").setScore(5);
        objective.getScore("§4").setScore(2);

        // GAME TIME
        long time = System.currentTimeMillis() - GameCore.startTime;
        objective.getScore("§eGame Time: §a" + Utils.convertMillisToMSS(time)).setScore(9);

        // DISCS
        if (GameCore.existsRedDiscHolder()) {
            objective.getScore("§cRed Disc: §c§lHeld").setScore(7);
        } else {
            objective.getScore("§cRed Disc: §a§lSafe").setScore(7);
        }
        if (GameCore.existsGreenDiscHolder()) {
            objective.getScore("§aGreen Disc: §c§lHeld").setScore(6);
        } else {
            objective.getScore("§aGreen Disc: §a§lSafe").setScore(6);
        }

        // CAPTURES
        int discGoal = GameCore.discGoal;
        int redDiscs = GameCore.redDiscCaptures;
        int greenDiscs = GameCore.greenDiscCaptures;
        objective.getScore("§cRed Captures: §e" + redDiscs + "/" + discGoal).setScore(4);
        objective.getScore("§aGreen Captures: §e" + greenDiscs + "/" + discGoal).setScore(3);

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
