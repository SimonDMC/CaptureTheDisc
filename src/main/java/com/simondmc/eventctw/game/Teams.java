package com.simondmc.eventctw.game;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Teams {
    private static final List<Player> red = new ArrayList<>();
    private static final List<Player> blue = new ArrayList<>();
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

    public static void assignTeams() {
        players = new ArrayList<>(Bukkit.getOnlinePlayers());
        players.removeIf(p -> p.getGameMode().equals(GameMode.SPECTATOR));
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
            p.setPlayerListName("§c" + p.getName());
            return;
        }
        blue.add(p);
        p.setPlayerListName("§9" + p.getName());
    }
}
