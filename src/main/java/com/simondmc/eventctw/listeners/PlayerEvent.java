package com.simondmc.eventctw.listeners;

import com.simondmc.eventctw.game.*;
import com.simondmc.eventctw.game.OfflinePlayer;
import com.simondmc.eventctw.kits.Kits;
import com.simondmc.eventctw.region.Region;
import com.simondmc.eventctw.shop.ShopGUI;
import com.simondmc.eventctw.util.Config;
import com.simondmc.eventctw.util.Utils;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayerEvent implements Listener {

    List<Material> droppable = new ArrayList<>(Arrays.asList(
            Material.GOLDEN_APPLE,
            Material.TNT,
            Material.ENDER_PEARL,
            Material.SHIELD,
            Material.BOW,
            Material.TIPPED_ARROW
    ));

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
            e.setDamage(0);
            e.setCancelled(true);
            return;
        }
        if (Teams.getBlue().contains(p) && Utils.inRegion(p.getLocation(), Region.BLUE_GRACE)) {
            e.setDamage(0);
            e.setCancelled(true);
            return;
        }
        // death
        if (p.getHealth() - e.getDamage() <= 0) {
            e.setDamage(0);

            // drop everything bought from shop
            for (ItemStack i : p.getInventory().getContents()) {
                if (i != null && droppable.contains(i.getType())) {
                    p.getWorld().dropItemNaturally(p.getLocation(), i);
                }
            }
            GameCore.die(p);
        }
    }

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
            // add coins if damage done, haven't shot self and no shield blocking
            if (e.getDamage() == 0) return;
            if (p.equals(damaged)) return;
            if (e.getDamage(EntityDamageEvent.DamageModifier.BLOCKING) != 0) return;
            Coins.addCoins(p, (float) e.getDamage());
            // set last damager
            GameCore.lastDamage.put(damaged, new TimestampHit(System.currentTimeMillis(), p));
            return;
        }
        // shoot dmg
        if (e.getDamager() instanceof Arrow) {
            Arrow arrow = (Arrow) e.getDamager();
            if (!(arrow.getShooter() instanceof Player)) return;
            Player p = (Player) arrow.getShooter();
            // nerf arrow damage slightly
            e.setDamage(e.getDamage() * 3/4);
            // friendly fire (you can still shoot yourself tho)
            if (!p.equals(damaged) && ((Teams.getRed().contains(p) && Teams.getRed().contains(damaged)) || (Teams.getBlue().contains(p) && Teams.getBlue().contains(damaged)))) {
                arrow.remove();
                e.setCancelled(true);
                return;
            }
            // add coins if damage done, haven't shot self and no shield blocking
            if (e.getDamage() == 0) return;
            if (p.equals(damaged)) return;
            if (e.getDamage(EntityDamageEvent.DamageModifier.BLOCKING) != 0) return;
            Coins.addCoins(p, (float) e.getDamage());
            // set last damager
            GameCore.lastDamage.put(damaged, new TimestampHit(System.currentTimeMillis(), p));
        }
    }

    @EventHandler
    public void move(PlayerMoveEvent e) {
        if (!GameCore.isOn()) return;
        Player p = e.getPlayer();

        // void death
        if (e.getTo().getY() < 0) {
            GameCore.die(p);
            return;
        }

        // launch out of other teams base
        if ((Teams.getRed().contains(p) && Utils.inRegion(e.getTo(), Region.BLUE_GRACE)) || (Teams.getBlue().contains(p) && Utils.inRegion(e.getTo(), Region.RED_GRACE))) {
            e.setCancelled(true);
            p.sendMessage("§cYou cannot enter this area!");
        }

        // launch out of own disc area
        if ((Teams.getRed().contains(p) && Utils.inRegion(e.getTo(), Region.RED_DISC_AREA)) || (Teams.getBlue().contains(p) && Utils.inRegion(e.getTo(), Region.BLUE_DISC_AREA))) {
            e.setCancelled(true);
            p.sendMessage("§cYou cannot enter your own disc area!");
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

            // devinfo
            Config.devAnnounce("§aGame finished! Took " + Math.round((System.currentTimeMillis() - GameCore.startTime)/1000) + "s.");
            // stats
            for (Player player : Teams.getPlayers()) {
                player.sendMessage("§e§lMost kills: §a" + GameUtils.getMostKills().getName() + " §7- §c" + GameUtils.getKills(GameUtils.getMostKills()) + "⚔");
                player.sendMessage("§7Your kills: §c" + GameUtils.getKills(player) +"⚔");
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

            // devinfo
            Config.devAnnounce("§aGame finished! Took " + Math.round((System.currentTimeMillis() - GameCore.startTime)/1000) + "s.");
            // stats
            for (Player player : Teams.getPlayers()) {
                player.sendMessage("§e§lMost kills: §a" + GameUtils.getMostKills().getName() + " §7- §c" + GameUtils.getKills(GameUtils.getMostKills()) + "⚔");
                player.sendMessage("§7Your kills: §c" + GameUtils.getKills(player) +"⚔");
            }
            GameCore.stopGame();
        }
    }

    @EventHandler
    public void click(PlayerInteractEvent e) {
        if (!GameCore.isOn()) return;
        // light tnt
        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getHand().equals(EquipmentSlot.HAND) && e.getClickedBlock().getType().equals(Material.TNT)) {
            Location l = e.getClickedBlock().getLocation().add(.5, 0, .5);
            e.getClickedBlock().setType(Material.AIR);
            l.getWorld().spawnEntity(l, EntityType.PRIMED_TNT);
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
            // give regen
            who_picked.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 0));
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
            // give regen
            who_picked.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 0));
        }
    }

    // cancel item dropping
    @EventHandler
    public void dropItem(PlayerDropItemEvent e) {
        if (!GameCore.isOn()) return;
        if (!droppable.contains(e.getItemDrop().getItemStack().getType())) e.setCancelled(true);
    }

    @EventHandler
    public void shootBlock(ProjectileHitEvent e) {
        if (!GameCore.isOn()) return;
        if (e.getHitBlock() == null) return;

        // shoot tnt + kill arrows in block if arrow
        if (e.getEntity() instanceof Arrow) {
            e.getEntity().remove();
            if (!e.getHitBlock().getType().equals(Material.TNT)) return;
            Location l = e.getHitBlock().getLocation().add(.5, 0, .5);
            e.getHitBlock().setType(Material.AIR);
            l.getWorld().spawnEntity(l, EntityType.PRIMED_TNT);
        }

        // check illegal areas if pearl
        if (e.getEntity() instanceof EnderPearl) {
            if (Utils.inRegion(e.getEntity().getLocation(), Region.RED_GRACE) ||
                    Utils.inRegion(e.getEntity().getLocation(), Region.BLUE_GRACE) ||
                    Utils.inRegion(e.getEntity().getLocation(), Region.RED_DISC_AREA) ||
                    Utils.inRegion(e.getEntity().getLocation(), Region.BLUE_DISC_AREA) ||
                    Utils.inRegion(e.getEntity().getLocation(), Region.RED_CAPTURE) ||
                    Utils.inRegion(e.getEntity().getLocation(), Region.BLUE_CAPTURE)) {
                EnderPearl pearl = (EnderPearl) e.getEntity();
                Player p = (Player) pearl.getShooter();
                p.sendMessage("§cYou can't teleport there!");
                p.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
                pearl.remove();
            }
        }
    }

    @EventHandler
    public void splashPotion(PotionSplashEvent e) {
        if (!GameCore.isOn()) return;
        ThrownPotion pot = e.getEntity();
        if (!(pot.getShooter() instanceof Player)) return;
        Player thrower = (Player) pot.getShooter();
        // only sploosher gets effect
        for (LivingEntity ent : e.getAffectedEntities()) {
            if (!thrower.equals(ent)) e.setIntensity(ent, 0); // this is the worst way to cancel getting the potion but altering getAffectedEntities didn't work
        }

    }

    @EventHandler
    public void joinGame(PlayerJoinEvent e) {
        if (!GameCore.isOn()) return;
        Player p = e.getPlayer();

        // parse offlineplayer data
        for (OfflinePlayer op : Teams.getOffline()) {
            if (op.getUUID().equals(p.getUniqueId())) {
                // add back to game and die
                Teams.getPlayers().add(p);
                GameCore.die(p);

                // set kit
                if (op.getKit() == null) {
                    Kits.openKitGui(p);
                } else {
                    Kits.setKit(p, op.getKit());
                    Kits.selected.add(p);
                }

                // set team
                if (op.isRed()) {
                    Teams.getRed().add(p);
                } else {
                    Teams.getBlue().add(p);
                }

                // set upgrades
                ShopGUI.upgrades.put(p, op.getUpgrades());

                return;
            }
        }

        // if not in offlineplayer list, set as spectator
        p.sendMessage("§aCapture The Disc has already started! You can spectate the game.");
        p.setGameMode(GameMode.SPECTATOR);
        Utils.playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP);

    }

    @EventHandler
    public void leaveGame(PlayerQuitEvent e) {
        if (!GameCore.isOn()) return;
        Player p = e.getPlayer();
        if (Teams.getPlayers().contains(p)) {
            // generate a new offline player to retrieve when rejoins
            Teams.getOffline().add(new OfflinePlayer(p.getUniqueId(), Kits.getKit(p), Teams.getRed().contains(p), ShopGUI.upgrades.get(p)));

            // die so disc drops if player is disc holder
            GameCore.die(p);
        }
    }
}
