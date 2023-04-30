package com.simondmc.capturethedisc.listeners;

import com.simondmc.capturethedisc.CaptureTheDisc;
import com.simondmc.capturethedisc.CoreHolder;
import com.simondmc.capturethedisc.CoreManager;
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
import java.util.logging.Logger;

public class PlayerEvent implements Listener {

    List<Material> droppable = new ArrayList<>(Arrays.asList(
            Material.GOLDEN_APPLE,
            Material.TNT,
            Material.ENDER_PEARL,
            Material.SHIELD,
            Material.BOW,
            Material.TIPPED_ARROW,
            Material.MILK_BUCKET,
            Material.MUSIC_DISC_CAT,
            Material.MUSIC_DISC_PIGSTEP
    ));

    Logger logger = CaptureTheDisc.plugin.getLogger();

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
                if (i != null &&
                        droppable.contains(i.getType()) &&
                        i.getType() != Material.MUSIC_DISC_CAT &&
                        i.getType() != Material.MUSIC_DISC_PIGSTEP) {
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
            // send warning message if damager holding axe
            if (p.getInventory().getItemInMainHand().getType().toString().contains("AXE")) {
                p.sendMessage("§cAxes do not deal damage.");
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
        if (!Teams.getPlayers().contains(p) && p.getWorld().equals(Region.getWorld())) {
            if (!Utils.inRegion(p.getLocation(), Region.MAP)) {
                Location l = Region.CENTER.clone();
                l.setWorld(Region.getWorld());
                p.teleport(l);
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

        // capture disc
        if (Teams.getRed().contains(p) && Utils.inRegion(e.getTo(), Region.RED_CAPTURE) && GameCore.isDiscHolder(p)) {
            GameCore.redDiscCaptures++;
            if (GameCore.redDiscCaptures == GameCore.discGoal) {
                // final disc
                for (Player player : Teams.getRed()) {
                    player.sendTitle("§aYou won!", "", 20, 100, 20);
                    player.sendMessage("§c" + p.getName() + " §ecaptured the §a§lGREEN §edisc! §7(" + GameCore.redDiscCaptures + "/" + GameCore.discGoal + ")");
                    Utils.playSound(player, Sound.UI_TOAST_CHALLENGE_COMPLETE);
                }
                for (Player player : Teams.getGreen()) {
                    player.sendTitle("§cYou lost!", "", 20, 100, 20);
                    player.sendMessage("§c" + p.getName() + " §ecaptured the §a§lGREEN §edisc! §7(" + GameCore.redDiscCaptures + "/" + GameCore.discGoal + ")");
                    Utils.playSound(player, Sound.ENTITY_ENDER_DRAGON_GROWL);
                }
                logger.info(p.getName() + " captured the green disc - stopping game");
                GameCore.stopGame();
            } else {
                // non-final disc
                for (Player player : Teams.getRed()) {
                    player.sendTitle("§aGreen disc captured!", "§e" + GameCore.redDiscCaptures + "/" + GameCore.discGoal, 20, 100, 20);
                    player.sendMessage("§c" + p.getName() + " §ecaptured the §a§lGREEN §edisc! §7(" + GameCore.redDiscCaptures + "/" + GameCore.discGoal + ")");
                    Utils.playSound(player, Sound.ENTITY_PLAYER_LEVELUP);
                }
                for (Player player : Teams.getGreen()) {
                    player.sendTitle("§cGreen disc captured!", "§e" + GameCore.redDiscCaptures + "/" + GameCore.discGoal, 20, 100, 20);
                    player.sendMessage("§c" + p.getName() + " §ecaptured the §a§lGREEN §edisc! §7(" + GameCore.redDiscCaptures + "/" + GameCore.discGoal + ")");
                    Utils.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 0);
                }
                logger.info(p.getName() + " captured the green disc - " + GameCore.redDiscCaptures + "/" + GameCore.discGoal);
                GameUtils.captureDisc(p);
            }
        }
        if (Teams.getGreen().contains(p) && Utils.inRegion(e.getTo(), Region.GREEN_CAPTURE) && GameCore.isDiscHolder(p)) {
            GameCore.greenDiscCaptures++;
            if (GameCore.greenDiscCaptures == GameCore.discGoal) {
                // final disc
                for (Player player : Teams.getGreen()) {
                    player.sendTitle("§aYou won!", "", 20, 100, 20);
                    player.sendMessage("§a" + p.getName() + " §ecaptured the §c§lRED §edisc! §7(" + GameCore.greenDiscCaptures + "/" + GameCore.discGoal + ")");
                    Utils.playSound(player, Sound.UI_TOAST_CHALLENGE_COMPLETE);
                }
                for (Player player : Teams.getRed()) {
                    player.sendTitle("§cYou lost!", "", 20, 100, 20);
                    player.sendMessage("§a" + p.getName() + " §ecaptured the §c§lRED §edisc! §7(" + GameCore.greenDiscCaptures + "/" + GameCore.discGoal + ")");
                    Utils.playSound(player, Sound.ENTITY_ENDER_DRAGON_GROWL);
                }
                logger.info(p.getName() + " captured the red disc - stopping game");
                GameCore.stopGame();
            } else {
                // non-final disc
                for (Player player : Teams.getGreen()) {
                    player.sendTitle("§aRed disc captured!", "§e" + GameCore.greenDiscCaptures + "/" + GameCore.discGoal, 20, 100, 20);
                    player.sendMessage("§a" + p.getName() + " §ecaptured the §c§lRED §edisc! §7(" + GameCore.greenDiscCaptures + "/" + GameCore.discGoal + ")");
                    Utils.playSound(player, Sound.ENTITY_PLAYER_LEVELUP);
                }
                for (Player player : Teams.getRed()) {
                    player.sendTitle("§cRed disc captured!", "§e" + GameCore.greenDiscCaptures + "/" + GameCore.discGoal, 20, 100, 20);
                    player.sendMessage("§a" + p.getName() + " §ecaptured the §c§lRED §edisc! §7(" + GameCore.greenDiscCaptures + "/" + GameCore.discGoal + ")");
                    Utils.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 0);
                }
                logger.info(p.getName() + " captured the red disc - " + GameCore.greenDiscCaptures + "/" + GameCore.discGoal);
                GameUtils.captureDisc(p);
            }
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

        // cancel pickup and respawn if own disc
        if (e.getItem().getItemStack().getType().equals(Material.MUSIC_DISC_PIGSTEP) && Teams.getRed().contains(who_picked)) {
            e.setCancelled(true);
            e.getItem().remove();
            for (Player player : Teams.getGreen()) {
                Utils.playSound(player, Sound.BLOCK_ANVIL_LAND);
                player.sendMessage("§c" + who_picked.getName() + " §eretrieved their own disc - §lRespawning!");
            }
            for (Player player : Teams.getRed()) {
                Utils.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
                player.sendMessage("§c" + who_picked.getName() + " §eretrieved their own disc - §lRespawning!");
            }
            GameUtils.spawnRedDisc();
            return;
        }
        if (e.getItem().getItemStack().getType().equals(Material.MUSIC_DISC_CAT) && Teams.getGreen().contains(who_picked)) {
            e.setCancelled(true);
            e.getItem().remove();
            for (Player player : Teams.getRed()) {
                Utils.playSound(player, Sound.BLOCK_ANVIL_LAND);
                player.sendMessage("§a" + who_picked.getName() + " §eretrieved their own disc - §lRespawning!");
            }
            for (Player player : Teams.getGreen()) {
                Utils.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
                player.sendMessage("§a" + who_picked.getName() + " §eretrieved their own disc - §lRespawning!");
            }
            GameUtils.spawnGreenDisc();
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
            // give slowness
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
            // give slowness
            who_picked.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1000000, 0));
            // cover beacons
            Utils.fillRegion(Region.GREEN_DISC_BEACON_COVER, Material.POLISHED_BLACKSTONE);
        }
    }

    // cancel item dropping
    @EventHandler
    public void dropItem(PlayerDropItemEvent e) {
        if (!GameCore.isOn()) return;
        if (!droppable.contains(e.getItemDrop().getItemStack().getType())) {
            e.setCancelled(true);
            return;
        }
        // dropped disc
        if (e.getItemDrop().getItemStack().getType().equals(Material.MUSIC_DISC_PIGSTEP)) {
            for (Player p : Teams.getPlayers()) {
                Utils.playSound(p, Sound.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF);
                p.sendMessage("§a" + e.getPlayer().getName() + "§e dropped the §c§lRED§e disc!");
            }
            e.getItemDrop().setGlowing(true);
            GameCore.redDisc = e.getItemDrop();
            SidebarHandler.redTeam.addEntry(GameCore.redDisc.getUniqueId().toString());
            for (Player p : Teams.getPlayers()) {
                p.setScoreboard(SidebarHandler.board);
            }
            GameCore.removeDiscHolder(e.getPlayer());
        }
        if (e.getItemDrop().getItemStack().getType().equals(Material.MUSIC_DISC_CAT)) {
            for (Player p : Teams.getPlayers()) {
                Utils.playSound(p, Sound.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF);
                p.sendMessage("§c" + e.getPlayer().getName() + "§e dropped the §a§lGREEN§e disc!");
            }
            e.getItemDrop().setGlowing(true);
            GameCore.greenDisc = e.getItemDrop();
            SidebarHandler.greenTeam.addEntry(GameCore.greenDisc.getUniqueId().toString());
            for (Player p : Teams.getPlayers()) {
                p.setScoreboard(SidebarHandler.board);
            }
            GameCore.removeDiscHolder(e.getPlayer());
        }
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
                    Utils.inRegion(e.getEntity().getLocation(), Region.GREEN_CAPTURE) ||
                    e.getEntity().getLocation().getY() < Region.VOID_LEVEL) {
                EnderPearl pearl = (EnderPearl) e.getEntity();
                Player p = (Player) pearl.getShooter();
                p.sendMessage("§cYou can't teleport there!");
                if (e.getEntity().getLocation().getY() > Region.VOID_LEVEL)
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
        if (CaptureTheDisc.coreEnabled) return;
        Player p = e.getPlayer();
        logger.info(p.getName() + " joined CaptureTheDisc");

        GameCore.joinWorld(p);
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

            logger.info(p.getName() + " left CaptureTheDisc");

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
