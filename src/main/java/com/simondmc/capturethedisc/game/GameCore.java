package com.simondmc.capturethedisc.game;

import com.simondmc.capturethedisc.CaptureTheDisc;
import com.simondmc.capturethedisc.CoreHolder;
import com.simondmc.capturethedisc.CoreManager;
import com.simondmc.capturethedisc.kits.Inventory;
import com.simondmc.capturethedisc.kits.KitNPC;
import com.simondmc.capturethedisc.kits.Kits;
import com.simondmc.capturethedisc.kits.RegeneratingItemHandler;
import com.simondmc.capturethedisc.region.Region;
import com.simondmc.capturethedisc.shop.ShopGUI;
import com.simondmc.capturethedisc.shop.ShopNPC;
import com.simondmc.capturethedisc.shop.Upgrade;
import com.simondmc.capturethedisc.util.Config;
import com.simondmc.capturethedisc.util.Utils;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class GameCore {
    private static boolean running = false;
    public static final HashMap<Player, Integer> dead = new HashMap<>();
    public static final HashMap<Player, Integer> kills = new HashMap<>();
    public static final HashMap<Player, TimestampHit> lastDamage = new HashMap<>();
    public static long startTime;
    private static Player redDiscHolder, greenDiscHolder;
    public static Item redDisc, greenDisc;
    public static int discGoal = 1;
    public static int redDiscCaptures = 0, greenDiscCaptures = 0;

    public static void startGame() {
        running = true;
    }

    public static void stopGame() {
        running = false;
        // devinfo
        Config.devAnnounce("§aGame finished! Took " + Math.round((System.currentTimeMillis() - startTime)/1000) + "s.");
        // stats
        for (Player player : Teams.getPlayers()) {
            player.sendMessage("§e§lMost kills: §a" + GameUtils.getMostKills().getName() + " §7- §c" + GameUtils.getKills(GameUtils.getMostKills()) + "⚔");
            player.sendMessage("§7Your kills: §c" + GameUtils.getKills(player) + "⚔");
        }
        if (CaptureTheDisc.coreEnabled) {
            CoreHolder.endGame();
        } else {
            for (Player p : Teams.getPlayers()) {
                p.setDisplayName(p.getName());
                p.setGameMode(GameMode.SURVIVAL);
                // reset disc holder
                removeDiscHolder(p);
                // remove all active effects
                for (PotionEffect eff : p.getActivePotionEffects()) p.removePotionEffect(eff.getType());
                // reset team and sidebar
                p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
                // cancel regenerating potion
                RegeneratingItemHandler.resetRegeneratingItem(p);
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
            dead.clear();
            // reset kills
            GameCore.kills.clear();
            // reset sidebar
            SidebarHandler.reset();
            // reset captures
            greenDiscCaptures = 0;
            redDiscCaptures = 0;
        }

    }

    public static boolean isOn() {
        return running;
    }

    public static void setup() {
        // PRIORITY!!! initialize world
        World w = Bukkit.getWorld("ctd-world");
        Region.initWorld(w);

        // set start time for devinfo
        startTime = System.currentTimeMillis();
        if (!CaptureTheDisc.coreEnabled) {
            // assign teams randomly
            Teams.assignTeams();
        }
        // initalize all player upgrades
        ShopGUI.initUpgrades();
        // setup all player things, tp, inventory, etc
        GameUtils.setupPlayers();
        // spawn discs in team bases
        GameUtils.spawnRedDisc();
        GameUtils.spawnGreenDisc();
        // spawn npcs
        ShopNPC.initShopNpc();
        KitNPC.initKitNpcs();
    }

    public static void respawn(Player p) {
        p.setHealth(20);
        p.setFoodLevel(20);
        p.setSaturation(0);
        p.getInventory().clear();
        p.setGameMode(GameMode.SURVIVAL);
        Inventory.fillInv(p);
        Location l;
        if (Teams.getRed().contains(p)) {
            l = Region.RED_SPAWN.clone();
        } else {
            l = Region.GREEN_SPAWN.clone();
        }
        l.setWorld(Region.getWorld());
        p.teleport(l);
        if (Kits.getKit(p) == null) {
            Kits.openKitGui(p);
        }
    }

    public static void die(Player p, EntityDamageEvent.DamageCause cause) {
        boolean isDiscHolder = false;

        // cancel regenerating potion
        RegeneratingItemHandler.resetRegeneratingItem(p);

        // clear inventory
        p.getInventory().clear();
        p.setItemOnCursor(null);
        p.getOpenInventory().getTopInventory().clear();

        // remove all active effects
        for (PotionEffect eff : p.getActivePotionEffects()) p.removePotionEffect(eff.getType());

        p.setGameMode(GameMode.SPECTATOR);
        dead.put(p, 60); // 60 ticks = 3 seconds
        Location l = Region.CENTER.clone().add(.5, 20, .5);
        l.setWorld(Region.getWorld());
        p.teleport(l);

        // play void death sound
        new BukkitRunnable() {
            @Override
            public void run() {
                Utils.playSound(p, Sound.ENTITY_GENERIC_BIG_FALL);
            }
        }.runTaskLater(CaptureTheDisc.plugin, 2);

        if (isDiscHolder(p)) {
            for (Player player : Teams.getPlayers()) {
                Utils.playSound(player, Sound.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF);
                // green disc holder
                if (Teams.getRed().contains(p)) {
                    player.sendMessage("§c" + p.getName() +  " §edied with the §a§lGREEN §edisc!");
                    GameUtils.spawnGreenDisc();
                }
                // red disc holder
                if (Teams.getGreen().contains(p)) {
                    player.sendMessage("§a" + p.getName() +  " §edied with the §c§lRED §edisc!");
                    GameUtils.spawnRedDisc();
                }
            }
            removeDiscHolder(p);
            isDiscHolder = true;
        }

        // figure out if kill
        if (lastDamage.containsKey(p)) {
            List<Player> players = Teams.getPlayers().size() <= 5 ? Teams.getPlayers() : Arrays.asList(p, lastDamage.get(p).damager);

            // make sure last hit was less than 10 seconds ago
            if (System.currentTimeMillis() - lastDamage.get(p).timestamp < 10000) {// 10,000ms = 10s
                Player damager = lastDamage.get(p).damager;
                String dColor = (Teams.getRed().contains(damager) ? "§c" : "§a");
                String pColor = (Teams.getRed().contains(p) ? "§c" : "§a");
                GameUtils.addKill(damager);
                Utils.playSound(damager, Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
                if (isDiscHolder) return;
                for (Player reciever : players) {
                    switch (cause) {
                        case PROJECTILE:
                            reciever.sendMessage(pColor + p.getName() + " §ewas shot by " + dColor + damager.getName() + "§e.");
                            break;
                        case FALL:
                            reciever.sendMessage(pColor + p.getName() + " §ewas pushed off by " + dColor + damager.getName() + "§e.");
                            break;
                        case VOID:
                            reciever.sendMessage(pColor + p.getName() + " §ewas knocked onto the floor by " + dColor + damager.getName() + "§e.");
                            break;
                        case ENTITY_EXPLOSION:
                            reciever.sendMessage(pColor + p.getName() + " §ewas blown up by " + dColor + damager.getName() + "§e.");
                            break;
                        case CUSTOM:
                            break;
                        default:
                            reciever.sendMessage(pColor + p.getName() + " §ewas killed by " + dColor + damager.getName() + "§e.");
                            break;
                    }
                }
                return;
            }
        }

        // if not kill
        List<Player> players = Teams.getPlayers().size() <= 5 ? Teams.getPlayers() : Collections.singletonList(p);
        String pColor = (Teams.getRed().contains(p) ? "§c" : "§a");
        if (isDiscHolder) return;
        for (Player reciever : players) {
            switch (cause) {
                case PROJECTILE:
                    reciever.sendMessage(pColor + p.getName() + " §ewas shot.");
                    break;
                case FALL:
                    reciever.sendMessage(pColor + p.getName() + " §efell off.");
                    break;
                case VOID:
                    reciever.sendMessage(pColor + p.getName() + " §efell onto the floor.");
                    break;
                case ENTITY_EXPLOSION:
                    reciever.sendMessage(pColor + p.getName() + " §eblew up.");
                    break;
                case CUSTOM:
                    break;
                default:
                    reciever.sendMessage(pColor + p.getName() + " §edied.");
                    break;
            }
        }
    }

    public static boolean isDiscHolder(Player p) {
        return redDiscHolder == p || greenDiscHolder == p;
    }

    public static void setDiscHolder(Player p) {
        if (Teams.getRed().contains(p)) greenDiscHolder = p;
        if (Teams.getGreen().contains(p)) redDiscHolder = p;
        p.setGlowing(true);
    }

    public static void removeDiscHolder(Player p) {
        if (redDiscHolder == p) {
            redDiscHolder = null;
        }
        if (greenDiscHolder == p) {
            greenDiscHolder = null;
        }
        p.setGlowing(false);
    }

    public static boolean existsRedDiscHolder() {
        return redDiscHolder != null;
    }

    public static boolean existsGreenDiscHolder() {
        return greenDiscHolder != null;
    }

    public static void joinGame(Player p) {
        // check if player is in any team already
        if (Teams.getRed().contains(p) || Teams.getGreen().contains(p)) {
            p.sendMessage("§cYou are already in the game.");
            return;
        }
        // add player to smaller team
        Teams.setTeam(p, Teams.getRed().size() <= Teams.getGreen().size());
        Teams.getPlayers().add(p);
        // init player upgrades
        List<Upgrade> upgradeList = new ArrayList<>();
        upgradeList.addAll(Arrays.asList(Upgrade.SWORD_NONE, Upgrade.CHESTPLATE_NONE, Upgrade.AXE_NONE));
        ShopGUI.upgrades.put(p, upgradeList);
        // respawn player
        GameCore.respawn(p);
        p.sendMessage("§eYou have joined the game.");
    }

    public static void joinWorld(Player p) {
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
}
