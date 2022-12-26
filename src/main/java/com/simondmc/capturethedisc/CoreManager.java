package com.simondmc.capturethedisc;

import com.nametbd.core.api.CoreTeam;
import com.nametbd.core.api.GameManager;
import com.simondmc.capturethedisc.game.GameCore;
import com.simondmc.capturethedisc.game.SidebarHandler;
import com.simondmc.capturethedisc.game.Teams;
import com.simondmc.capturethedisc.kits.Kits;
import com.simondmc.capturethedisc.kits.RegeneratingItemHandler;
import com.simondmc.capturethedisc.map.Map;
import com.simondmc.capturethedisc.region.Region;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class CoreManager extends GameManager {

    Logger logger = CaptureTheDisc.plugin.getLogger();

    @Override
    public String getId() {
        return "capture_the_disc";
    }

    @Override
    public Location getLobbyLocation() {
        Location l = Region.LOBBY.clone();
        World w;
        try {
            w = Bukkit.getWorld("ctd-world");
        } catch (NullPointerException e) {
            Map.createMap();
            w = Bukkit.getWorld("ctd-world");
        }
        l.setWorld(w);
        return l;
    }

    @Override
    public List<CoreTeam> startGame() {
        logger.info("Starting game...");
        logger.info("Assigning teams...");
        List<CoreTeam> coreTeams = new ArrayList<>();
        // assign teams randomly
        List<Player>[] teams = Teams.assignTeams();
        coreTeams.add(new CoreTeam(teams[0], ChatColor.RED, "Red"));
        coreTeams.add(new CoreTeam(teams[1], ChatColor.GREEN, "Green"));
        logger.info("Teams assigned, running internal game setup...");
        GameCore.setup();
        logger.info("Internal game setup complete, starting game.");
        GameCore.startGame();
        logger.info("Game started! Returning teams.");
        return coreTeams;
    }

    @Override
    public void cleanupGame() {
        logger.info("Cleaning up game...");
        for (Player p : Teams.getPlayers()) {
            p.setDisplayName(p.getName());
            p.setGameMode(GameMode.SURVIVAL);
            // reset disc holder
            GameCore.removeDiscHolder(p);
            // remove all active effects
            for (PotionEffect eff : p.getActivePotionEffects()) p.removePotionEffect(eff.getType());
            // reset team and sidebar
            p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
            // cancel regenerating potion
            RegeneratingItemHandler.resetRegeneratingItem(p);
            // clear inv
            p.getInventory().clear();
            // reset attack speed
            p.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(4);
        }
        // reset teams
        Teams.getRed().clear();
        Teams.getGreen().clear();
        Teams.getPlayers().clear();
        // reset offline players
        Teams.getOffline().clear();
        // reset kits
        Kits.resetKits();
        // reset death timer
        GameCore.dead.clear();
        // reset kills
        GameCore.kills.clear();
        // reset sidebar
        SidebarHandler.reset();
        logger.info("Game cleaned up!");
    }

    @Override
    public void setupGame(Runnable finishConsumer) {
        logger.info("Setting up game...");
        // reset map
        Map.createMap();
        for (Player p : CaptureTheDisc.plugin.getServer().getOnlinePlayers()) {
            Location l = Region.LOBBY.clone();
            l.setWorld(Bukkit.getWorld("ctd-world"));
            // don't teleport as that's handled by core
            p.setGameMode(GameMode.ADVENTURE);
            p.getInventory().clear();
            p.getInventory().setArmorContents(null);
            p.getInventory().setItemInOffHand(null);
            p.setHealth(20);
            p.setFoodLevel(20);
            p.setSaturation(20);
            p.setExp(0);
            p.setLevel(0);
            p.setFireTicks(0);
            p.setFallDistance(0);
            p.setAllowFlight(false);
            p.setFlying(false);
            p.setBedSpawnLocation(l, true);
            for (PotionEffect eff : p.getActivePotionEffects()) {
                p.removePotionEffect(eff.getType());
            }
        }
        logger.info("Game setup complete!");
        finishConsumer.run();
    }

    @Override
    public boolean setupRequired() {
        return true;
    }
}
