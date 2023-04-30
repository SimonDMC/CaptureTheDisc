package com.simondmc.capturethedisc.game;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class Teams {
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

    public static List<OfflinePlayer> getOffline() {
        return offline;
    }

    public static List<Player>[] assignTeams() {

        players = new ArrayList<>(Bukkit.getOnlinePlayers());
        //players.removeIf(p -> p.getGameMode().equals(GameMode.SPECTATOR));
        Collections.shuffle(players);
        // whether to start with red or green
        boolean red = new Random().nextFloat() < 0.5;
        for (Player p : players) {
            setTeam(p, red);
            red = !red;
        }
        return new List[]{getRed(), getGreen()};
    }

    public static void setTeam(Player p, boolean isRed) {
        if (isRed) {
            red.add(p);
            return;
        }
        green.add(p);
    }
}
