package com.simondmc.capturethedisc.listeners;

import com.simondmc.capturethedisc.CaptureTheDisc;
import com.simondmc.capturethedisc.game.*;
import com.simondmc.capturethedisc.game.OfflinePlayer;
import com.simondmc.capturethedisc.kits.Kits;
import com.simondmc.capturethedisc.kits.RegeneratingItemHandler;
import com.simondmc.capturethedisc.region.Region;
import com.simondmc.capturethedisc.shop.ShopGUI;
import com.simondmc.capturethedisc.util.Config;
import com.simondmc.capturethedisc.util.Utils;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

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
            Material.TIPPED_ARROW,
            Material.MILK_BUCKET
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
        // avoid damage taken on spawnpoint
        if (Teams.getRed().contains(p) && Utils.inRegion(p.getLocation(), Region.RED_GRACE)) {
            e.setCancelled(true);
            return;
        }
        if (Teams.getGreen().contains(p) && Utils.inRegion(p.getLocation(), Region.GREEN_GRACE)) {
            e.setCancelled(true);
            return;
        }
        // death
        if (p.getHealth() - e.getFinalDamage() <= 0) {
            // add coins
            if (e instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent edbe = (EntityDamageByEntityEvent) e;
                if (edbe.getDamager() instanceof Player)
                    Coins.addCoins((Player) edbe.getDamager(), (float) e.getDamage());
            }

            // cancel death
            e.setDamage(0);

            // drop everything bought from shop
            for (ItemStack i : p.getInventory().getContents()) {
                if (i != null && droppable.contains(i.getType())) {
                    p.getWorld().dropItemNaturally(p.getLocation(), i);
                }
            }
            GameCore.die(p, e.getCause());
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
            if ((Teams.getRed().contains(p) && Teams.getRed().contains(damaged)) || (Teams.getGreen().contains(p) && Teams.getGreen().contains(damaged))) {
                e.setCancelled(true);
                return;
            }
            // add coins if haven't shot self, no shield blocking and not in base
            if (p.equals(damaged)) return;
            if (e.getDamage(EntityDamageEvent.DamageModifier.BLOCKING) != 0) return;
            if (Utils.inRegion(damaged.getLocation(), Region.RED_GRACE) || Utils.inRegion(damaged.getLocation(), Region.GREEN_GRACE)) return;

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
            if (!p.equals(damaged) && ((Teams.getRed().contains(p) && Teams.getRed().contains(damaged)) || (Teams.getGreen().contains(p) && Teams.getGreen().contains(damaged)))) {
                arrow.remove();
                e.setCancelled(true);
                return;
            }
            // add coins if haven't shot self, no shield blocking and not in base
            if (p.equals(damaged)) return;
            if (e.getDamage(EntityDamageEvent.DamageModifier.BLOCKING) != 0) return;
            if (Utils.inRegion(damaged.getLocation(), Region.RED_GRACE) || Utils.inRegion(damaged.getLocation(), Region.GREEN_GRACE)) return;

            Coins.addCoins(p, (float) e.getDamage());
            // set last damager
            GameCore.lastDamage.put(damaged, new TimestampHit(System.currentTimeMillis(), p));
        }
    }

    @EventHandler
    public void move(PlayerMoveEvent e) {
        if (!GameCore.isOn()) return;
        Player p = e.getPlayer();

        // check for spectator
        if (!Teams.getPlayers().contains(p)) {
            if (!Utils.inRegion(p.getLocation(), Region.MAP)) {
                p.teleport(Region.CENTER);
            }
            return;
        }

        // void death
        if (e.getTo().getY() < Region.VOID_LEVEL && !GameCore.dead.containsKey(p)) {
            GameCore.die(p, EntityDamageEvent.DamageCause.VOID);
            return;
        }

        // launch out of other teams base
        if ((Teams.getRed().contains(p) && Utils.inRegion(e.getTo(), Region.GREEN_GRACE)) || (Teams.getGreen().contains(p) && Utils.inRegion(e.getTo(), Region.RED_GRACE))) {
            e.setCancelled(true);
            // slightly boost away
            p.setVelocity(e.getFrom().toVector().subtract(e.getTo().toVector()).normalize().setY(.33).multiply(.5));
            p.sendMessage("§cYou cannot enter this area!");
        }

        // launch out of own disc area
        if ((Teams.getRed().contains(p) && Utils.inRegion(e.getTo(), Region.RED_DISC_AREA)) || (Teams.getGreen().contains(p) && Utils.inRegion(e.getTo(), Region.GREEN_DISC_AREA))) {
            e.setCancelled(true);
            p.sendMessage("§cYou cannot enter your own disc area!");
        }

        // capture disc - end game
        if (Teams.getRed().contains(p) && Utils.inRegion(e.getTo(), Region.RED_CAPTURE) && GameCore.isDiscHolder(p)) {
            for (Player player : Teams.getRed()) {
                player.sendTitle("§aYou won!", "", 20, 100, 20);
                player.sendMessage("§c" + p.getName() + " §ecaptured the §a§lGREEN §edisc!");
                Utils.playSound(player, Sound.UI_TOAST_CHALLENGE_COMPLETE);
            }
            for (Player player : Teams.getGreen()) {
                player.sendTitle("§cYou lost!", "", 20, 100, 20);
                player.sendMessage("§c" + p.getName() + " §ecaptured the §a§lGREEN §edisc!");
                Utils.playSound(player, Sound.ENTITY_ENDER_DRAGON_GROWL);
            }
            GameCore.stopGame();
        }
        if (Teams.getGreen().contains(p) && Utils.inRegion(e.getTo(), Region.GREEN_CAPTURE) && GameCore.isDiscHolder(p)) {
            for (Player player : Teams.getGreen()) {
                player.sendTitle("§aYou won!", "", 20, 100, 20);
                player.sendMessage("§a" + p.getName() + " §ecaptured the §c§lRED §edisc!");
                Utils.playSound(player, Sound.UI_TOAST_CHALLENGE_COMPLETE);
            }
            for (Player player : Teams.getRed()) {
                player.sendTitle("§cYou lost!", "", 20, 100, 20);
                player.sendMessage("§a" + p.getName() + " §ecaptured the §c§lRED §edisc!");
                Utils.playSound(player, Sound.ENTITY_ENDER_DRAGON_GROWL);
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
                (e.getItem().getItemStack().getType().equals(Material.MUSIC_DISC_CAT) && Teams.getGreen().contains((Player) e.getEntity()))) {
            e.setCancelled(true);
            return;
        }

        // green pickup red
        if (e.getItem().getItemStack().getType().equals(Material.MUSIC_DISC_PIGSTEP)) {
            for (Player p : Teams.getRed()) {
                Utils.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 0);
                p.sendMessage("§a" + who_picked.getName() + "§e picked up your disc!");
            }
            for (Player p : Teams.getGreen()) {
                Utils.playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
                p.sendMessage("§a" + who_picked.getName() + "§e picked up the §c§lRED§e disc!");
            }

            // mark player as disc holder
            GameCore.setDiscHolder(who_picked);
            // give regen and slowness
            who_picked.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 0));
            who_picked.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1000000, 0));
            // cover beacons
            Utils.fillRegion(Region.RED_DISC_BEACON_COVER, Material.POLISHED_BLACKSTONE);
        }

        // red pickup green
        if (e.getItem().getItemStack().getType().equals(Material.MUSIC_DISC_CAT)) {
            for (Player p : Teams.getGreen()) {
                Utils.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 0);
                p.sendMessage("§c" + who_picked.getName() + "§e picked up your disc!");
            }
            for (Player p : Teams.getRed()) {
                Utils.playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
                p.sendMessage("§c" + who_picked.getName() + "§e picked up the §a§lGREEN§e disc!");
            }

            // mark player as disc holder
            GameCore.setDiscHolder(who_picked);
            // give regen and slowness
            who_picked.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 0));
            who_picked.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1000000, 0));
            // cover beacons
            Utils.fillRegion(Region.GREEN_DISC_BEACON_COVER, Material.POLISHED_BLACKSTONE);
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
                    Utils.inRegion(e.getEntity().getLocation(), Region.GREEN_GRACE) ||
                    Utils.inRegion(e.getEntity().getLocation(), Region.RED_DISC_AREA) ||
                    Utils.inRegion(e.getEntity().getLocation(), Region.GREEN_DISC_AREA) ||
                    Utils.inRegion(e.getEntity().getLocation(), Region.RED_CAPTURE) ||
                    Utils.inRegion(e.getEntity().getLocation(), Region.GREEN_CAPTURE)) {
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
        // start regenerating potion
        RegeneratingItemHandler.startTactician(thrower);
    }

    @EventHandler
    public void joinGame(PlayerJoinEvent e) {
        if (!GameCore.isOn()) return;
        Player p = e.getPlayer();

        // parse offlineplayer data
        for (OfflinePlayer op : Teams.getOffline()) {
            if (op.getUUID().equals(p.getUniqueId())) {
                Config.devAnnounce("§ePlayer §a" + p.getName() + " §ereconnected");

                // add back to game and die
                Teams.getPlayers().add(p);
                GameCore.die(p, EntityDamageEvent.DamageCause.CUSTOM);

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
                    Teams.getGreen().add(p);
                }

                // set upgrades
                ShopGUI.upgrades.put(p, op.getUpgrades());

                // set coins
                Coins.setCoins(p, op.getCoins());

                // set kills
                GameCore.kills.put(p, op.getKills());

                return;
            }
        }

        // if not in offlineplayer list, set as spectator
        p.sendMessage("§aCapture The Disc has already started! You can spectate the game or join via §d/joinctd");
        p.setGameMode(GameMode.SPECTATOR);
        Utils.playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP);

        Location l = Region.CENTER;
        l.setWorld(Region.getWorld());
        p.teleport(l);

    }

    @EventHandler
    public void leaveGame(PlayerQuitEvent e) {
        if (!GameCore.isOn()) return;
        Player p = e.getPlayer();
        if (Teams.getPlayers().contains(p)) {
            // generate a new offline player to retrieve when rejoins
            OfflinePlayer op = new OfflinePlayer(
                    p.getUniqueId(),
                    Kits.getKit(p),
                    Teams.getRed().contains(p),
                    ShopGUI.upgrades.get(p),
                    Coins.getCoins(p),
                    GameCore.kills.get(p) == null ? 0 : GameCore.kills.get(p)
            );

            Teams.getOffline().add(op);

            Config.devAnnounce("§ePlayer §a" + p.getName() + " §edisconnected:\n" +
                    "§aKit: §e" + op.getKit() + "\n" +
                    "§aTeam: §e" + (op.isRed() ? "Red" : "Green") + "\n" +
                    "§aUpgrades: §e" + op.getUpgrades() + "\n" +
                    "§aCoins: §e" + op.getCoins() + "\n" +
                    "§aKills: §e" + op.getKills()
            );

            // die so disc drops if player is disc holder
            GameCore.die(p, EntityDamageEvent.DamageCause.CUSTOM);
        }
    }

    @EventHandler
    public void drink(PlayerItemConsumeEvent e) {
        if (!GameCore.isOn()) return;

        Player p = e.getPlayer();
        if (e.getItem().getType() == Material.MILK_BUCKET) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    p.getInventory().remove(Material.BUCKET);
                }
            }.runTaskLater(CaptureTheDisc.plugin, 1);
        }
        if (e.getItem().getType() == Material.POTION) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    p.getInventory().remove(Material.GLASS_BOTTLE);
                }
            }.runTaskLater(CaptureTheDisc.plugin, 1);
        }
    }

    @EventHandler
    public void fishItem(PlayerFishEvent e) {
        if (!GameCore.isOn()) return;
        if (!e.getState().equals(PlayerFishEvent.State.CAUGHT_ENTITY)) return;
        if (e.getCaught() instanceof Item) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void craftChest(CraftItemEvent e) {
        if (!GameCore.isOn()) return;
        if (e.getRecipe().getResult().getType().equals(Material.CHEST) || e.getRecipe().getResult().getType().equals(Material.BARREL)) {
            e.setCancelled(true);
        }
    }
}
