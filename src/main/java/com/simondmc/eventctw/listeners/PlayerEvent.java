package com.simondmc.eventctw.listeners;

import com.simondmc.eventctw.game.Coins;
import com.simondmc.eventctw.game.GameCore;
import com.simondmc.eventctw.game.Teams;
import com.simondmc.eventctw.region.Region;
import com.simondmc.eventctw.util.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;

public class PlayerEvent implements Listener {

    @EventHandler
    public void food(FoodLevelChangeEvent e) {
        if (GameCore.isOn()) e.setCancelled(true);
    }

    @EventHandler
    public void damage(EntityDamageEvent e) {
        if (!GameCore.isOn()) return;
        if (!(e.getEntity() instanceof Player)) return;
        // nerf fall damage
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
        if (p.getHealth() - e.getDamage() <= 0) {
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

        // launch out of own disc area
        if ((Teams.getRed().contains(p) && Utils.inRegion(e.getTo(), Region.RED_DISC_AREA)) || (Teams.getBlue().contains(p) && Utils.inRegion(e.getTo(), Region.BLUE_DISC_AREA))) {
            Utils.launch(p, e.getTo(), e.getFrom(), .8f);
            p.sendMessage("§cYou cannot your own disc area!");
        }

        // capture disc - end game
        if (Teams.getRed().contains(p) && Utils.inRegion(e.getTo(), Region.RED_CAPTURE) && GameCore.isDiscHolder(p)) {
            for (Player player : Teams.getRed()) {
                player.sendTitle("§aYou won!", "", 20, 100, 20);
                player.sendMessage("§c" + p.getName() + " §ecaptured the §9§lBLUE §edisc!");
                Utils.playSound(player, Sound.UI_TOAST_CHALLENGE_COMPLETE);
            }
            for (Player player : Teams.getBlue()) {
                player.sendTitle("§cYou lost!", "", 20, 100, 20);
                player.sendMessage("§c" + p.getName() + " §ecaptured the §9§lBLUE §edisc!");
                Utils.playSound(player, Sound.ENTITY_ENDER_DRAGON_GROWL);
            }
            GameCore.stopGame();
        }
        if (Teams.getBlue().contains(p) && Utils.inRegion(e.getTo(), Region.BLUE_CAPTURE) && GameCore.isDiscHolder(p)) {
            for (Player player : Teams.getBlue()) {
                player.sendTitle("§aYou won!", "", 20, 100, 20);
                player.sendMessage("§9" + p.getName() + " §ecaptured the §c§lRED §edisc!");
                Utils.playSound(player, Sound.UI_TOAST_CHALLENGE_COMPLETE);
            }
            for (Player player : Teams.getRed()) {
                player.sendTitle("§cYou lost!", "", 20, 100, 20);
                player.sendMessage("§9" + p.getName() + " §ecaptured the §c§lRED §edisc!");
                Utils.playSound(player, Sound.ENTITY_ENDER_DRAGON_GROWL);
            }
            GameCore.stopGame();
        }
    }

    @EventHandler
    public void click(PlayerInteractEvent e) {
        if (!GameCore.isOn()) return;
        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getHand().equals(EquipmentSlot.HAND) && e.getClickedBlock().getType().equals(Material.TNT)) {
            Location l = e.getClickedBlock().getLocation().add(.5, 0, .5);
            e.getClickedBlock().setType(Material.AIR);
            l.getWorld().spawnEntity(l, EntityType.PRIMED_TNT);
        }
    }

    // TODO: only make it work for players (all entities allowed rn for testing purposes)
    @EventHandler
    public void hit(EntityDamageByEntityEvent e) {
        if (!GameCore.isOn()) return;
        if (!(e.getEntity() instanceof Player)) return;
        Player damaged = (Player) e.getEntity();
        // stab dmg
        if (e.getDamager() instanceof Player) {
            Player p = (Player) e.getDamager();
            // friendly fire
            if ((Teams.getRed().contains(p) && Teams.getRed().contains(damaged)) || (Teams.getBlue().contains(p) && Teams.getBlue().contains(damaged))) {
                e.setCancelled(true);
                return;
            }
            Coins.addCoins(p, (float) e.getDamage());
            return;
        }
        // shoot dmg
        if (e.getDamager() instanceof Arrow) {
            Arrow arrow = (Arrow) e.getDamager();
            // nerf arrow damage cuz op af
            e.setDamage(e.getDamage() / 2);
            if (!(arrow.getShooter() instanceof Player)) return;
            Player p = (Player) arrow.getShooter();
            // friendly fire
            if ((Teams.getRed().contains(p) && Teams.getRed().contains(damaged)) || (Teams.getBlue().contains(p) && Teams.getBlue().contains(damaged))) {
                // kill arrow cuz it looks goofy :(
                arrow.remove();
                e.setCancelled(true);
                return;
            }
            Coins.addCoins(p, (float) e.getDamage());
        }
    }

    // disc pickup
    @EventHandler
    public void pickupDisc(EntityPickupItemEvent e) {
        if (!GameCore.isOn()) return;
        if (!(e.getEntity() instanceof Player)) return;
        Player who_picked = (Player) e.getEntity();

        // cancel pickup if own disc
        if ((e.getItem().getItemStack().getType().equals(Material.MUSIC_DISC_PIGSTEP) && Teams.getRed().contains((Player) e.getEntity())) ||
                (e.getItem().getItemStack().getType().equals(Material.MUSIC_DISC_CAT) && Teams.getBlue().contains((Player) e.getEntity()))) {
            e.setCancelled(true);
            return;
        }

        // blue pickup red
        if (e.getItem().getItemStack().getType().equals(Material.MUSIC_DISC_PIGSTEP)) {
            for (Player p : Teams.getRed()) {
                Utils.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 0);
                p.sendMessage("§9" + who_picked.getName() + "§e picked up your disc!");
            }
            for (Player p : Teams.getBlue()) {
                Utils.playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
                p.sendMessage("§9" + who_picked.getName() + "§e picked up the §c§lRED§e disc!");
            }

            // mark player as disc holder
            GameCore.setDiscHolder(who_picked);
        }

        // red pickup blue
        if (e.getItem().getItemStack().getType().equals(Material.MUSIC_DISC_CAT)) {
            for (Player p : Teams.getBlue()) {
                Utils.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 0);
                p.sendMessage("§c" + who_picked.getName() + "§e picked up your disc!");
            }
            for (Player p : Teams.getRed()) {
                Utils.playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
                p.sendMessage("§c" + who_picked.getName() + "§e picked up the §9§lBLUE§e disc!");
            }

            // mark player as disc holder
            GameCore.setDiscHolder(who_picked);
        }
    }

    // cancel item dropping
    @EventHandler
    public void dropItem(PlayerDropItemEvent e) {
        if (GameCore.isOn()) e.setCancelled(true);
    }

    // shoot tnt + kill arrows in block
    @EventHandler
    public void shootBlock(ProjectileHitEvent e) {
        if (!GameCore.isOn()) return;
        if (e.getHitBlock() == null) return;
        // kill arrow
        e.getEntity().remove();
        if (!e.getHitBlock().getType().equals(Material.TNT)) return;
        Location l = e.getHitBlock().getLocation().add(.5, 0, .5);
        e.getHitBlock().setType(Material.AIR);
        l.getWorld().spawnEntity(l, EntityType.PRIMED_TNT);
    }
}
