package com.simondmc.capturethedisc.game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class Teams {
    private static Team redTeam;
    private static Team greenTeam;
    private static final List<Player> red = new ArrayList<>();
    private static final List<Player> green = new ArrayList<>();
    private static final List<OfflinePlayer> offline = new ArrayList<>();
    private static List<Player> players = new ArrayList<>();

    public static List<Player> getRed() {
        return red;
    }

    public static List<Player> getGreen() {
        return green;
    }

    public static List<Player> getPlayers() {
        return players;
    }

    public static Team getRedTeam() {
        return redTeam;
    }

    public static Team getGreenTeam() {
        return greenTeam;
    }

    public static List<OfflinePlayer> getOffline() {
        return offline;
    }

    public static void assignTeams() {
        // create mc teams for glowing
        Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        redTeam = (board.getTeam("redCTD") == null ? board.registerNewTeam("redCTD") : board.getTeam("redCTD"));
        greenTeam = (board.getTeam("greenCTD") == null ? board.registerNewTeam("greenCTD") : board.getTeam("greenCTD"));
        redTeam.setColor(ChatColor.RED);
        greenTeam.setColor(ChatColor.GREEN);

        players = new ArrayList<>(Bukkit.getOnlinePlayers());
        //players.removeIf(p -> p.getGameMode().equals(GameMode.SPECTATOR));
        Collections.shuffle(players);
        // whether to start with red or green
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
        green.add(p);
        greenTeam.addEntry(p.getName());
        p.setPlayerListName("§a" + p.getName());
    }
}
