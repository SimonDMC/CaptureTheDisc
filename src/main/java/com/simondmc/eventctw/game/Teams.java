package com.simondmc.eventctw.game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class Teams {
    private static Team redTeam;
    private static Team blueTeam;
    private static final List<Player> red = new ArrayList<>();
    private static final List<Player> blue = new ArrayList<>();
    private static final List<OfflinePlayer> offline = new ArrayList<>();
    private static List<Player> players = new ArrayList<>();

    public static List<Player> getRed() {
        return red;
    }

    public static List<Player> getBlue() {
        return blue;
    }

    public static List<Player> getPlayers() {
        return players;
    }

    public static Team getRedTeam() {
        return redTeam;
    }

    public static Team getBlueTeam() {
        return blueTeam;
    }

    public static List<OfflinePlayer> getOffline() {
        return offline;
    }

    public static void assignTeams() {
        // create mc teams for glowing
        Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        redTeam = (board.getTeam("redCTW") == null ? board.registerNewTeam("redCTW") : board.getTeam("redCTW"));
        blueTeam = (board.getTeam("blueCTW") == null ? board.registerNewTeam("blueCTW") : board.getTeam("blueCTW"));
        redTeam.setColor(ChatColor.RED);
        blueTeam.setColor(ChatColor.BLUE);

        players = new ArrayList<>(Bukkit.getOnlinePlayers());
        //players.removeIf(p -> p.getGameMode().equals(GameMode.SPECTATOR));
        Collections.shuffle(players);
        // whether to start with red or blue
        boolean red = new Random().nextFloat() < 0.5;
        for (Player p : players) {
            setTeam(p, red);
            red = !red;
        }
    }

    public static void setTeam(Player p, boolean isRed) {
        if (isRed) {
            red.add(p);
            redTeam.addEntry(p.getName());
            p.setPlayerListName("§c" + p.getName());
            return;
        }
        blue.add(p);
        blueTeam.addEntry(p.getName());
        p.setPlayerListName("§9" + p.getName());
    }
}
