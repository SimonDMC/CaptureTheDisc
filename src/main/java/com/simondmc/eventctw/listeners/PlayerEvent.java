package com.simondmc.eventctw.listeners;

import com.simondmc.eventctw.game.GameCore;
import com.simondmc.eventctw.game.Teams;
import com.simondmc.eventctw.region.Region;
import com.simondmc.eventctw.util.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerEvent implements Listener {

    @EventHandler
    public void food(FoodLevelChangeEvent e) {
        if (GameCore.isOn()) e.setCancelled(true);
    }

    @EventHandler
    public void damage(EntityDamageEvent e) {
        if (!GameCore.isOn()) return;
        if (!(e.getEntity() instanceof Player)) return;
        if (e.getCause().equals(EntityDamageEvent.DamageCause.FALL)) e.setDamage(e.getDamage() / 2);
        Player p = (Player) e.getEntity();
        // avoid damage taken on spawnpoint (mainly fall damage from tp)
        if (Teams.getRed().contains(p) && Utils.inRegion(p.getLocation(), Region.RED_GRACE)) {
            e.setCancelled(true);
            return;
        }
        if (Teams.getBlue().contains(p) && Utils.inRegion(p.getLocation(), Region.BLUE_GRACE)) {
            e.setCancelled(true);
            return;
        }
        if (p.getHealth() - e.getDamage() <= 0)  {
            e.setCancelled(true);
            GameCore.die(p);
        }
    }

    @EventHandler
    public void move(PlayerMoveEvent e) {
        if (!GameCore.isOn()) return;
        Player p = e.getPlayer();

        // void death
        if (e.getTo().getY() < 0) GameCore.die(p);

        // launch out of other teams base
        if ((Teams.getRed().contains(p) && Utils.inRegion(e.getTo(), Region.BLUE_GRACE)) || (Teams.getBlue().contains(p) && Utils.inRegion(e.getTo(), Region.RED_GRACE))) {
            Utils.launch(p, e.getTo(), e.getFrom(), 1.5f);
            p.sendMessage("§cYou cannot enter this area!");
        }
    }
}
