package com.simondmc.eventctw.game;

import com.simondmc.eventctw.EventCTW;
import com.simondmc.eventctw.region.Region;
import com.simondmc.eventctw.shop.ShopGUI;
import com.simondmc.eventctw.shop.Upgrade;
import com.simondmc.eventctw.util.Utils;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.UUID;

public class GameUtils {
    public static void clearEntities(Player p) {
        for (Entity e : p.getWorld().getEntities()) {
            if (!(e instanceof Player) && Utils.inRegion(e.getLocation(), Region.MAP)) e.remove();
        }
    }

    public static void setupPlayers() {
        for (Player p : Teams.getPlayers()) {
            p.setGameMode(GameMode.SURVIVAL);
            p.setHealth(20);
            p.setFoodLevel(20);
            p.setSaturation(0);
            p.getInventory().clear();
            fillInv(p);
            if (Teams.getRed().contains(p)) {
                p.teleport(Utils.genLocation(p.getWorld(), Region.RED_SPAWN, .5f, 0, .5f, -90, 0));
            } else {
                p.teleport(Utils.genLocation(p.getWorld(), Region.BLUE_SPAWN, .5f, 0, .5f, 90, 0));
            }
        }
    }

    public static void fillInv(Player p) {
        // TODO: rewrite all of this jesus christ
        ItemStack i;
        ItemMeta m;
        LeatherArmorMeta leather;

        if (ShopGUI.upgrades.get(p).contains(Upgrade.SWORD_2)) {
            i = new ItemStack(Material.DIAMOND_SWORD);
        } else if (ShopGUI.upgrades.get(p).contains(Upgrade.SWORD_1)) {
            i = new ItemStack(Material.IRON_SWORD);
        } else {
            i = new ItemStack(Material.STONE_SWORD);
        }

        m = i.getItemMeta();
        m.setUnbreakable(true);
        i.setItemMeta(m);

        p.getInventory().setItem(0, i);

        if (ShopGUI.upgrades.get(p).contains(Upgrade.AXE_2)) {
            i = new ItemStack(Material.DIAMOND_AXE);
        } else if (ShopGUI.upgrades.get(p).contains(Upgrade.AXE_1)) {
            i = new ItemStack(Material.IRON_AXE);
        } else {
            i = new ItemStack(Material.STONE_AXE);
        }
        m = i.getItemMeta();
        m.setUnbreakable(true);
        AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", 0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        m.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, modifier);
        i.setItemMeta(m);

        p.getInventory().setItem(1, i);


        p.getInventory().setItem(2, new ItemStack(Material.OAK_PLANKS, 64));
        p.getInventory().setItem(3, new ItemStack(Material.OAK_PLANKS, 64));
        p.getInventory().setItem(4, new ItemStack(Material.OAK_PLANKS, 64));

        i = new ItemStack(Material.GOLDEN_APPLE);
        m = i.getItemMeta();
        m.setDisplayName("§rGolden Apple");
        i.setItemMeta(m);

        p.getInventory().setItem(8, i);

        Color color = Teams.getRed().contains(p) ? Color.fromRGB(255, 0, 0) : Color.fromRGB(0, 0, 255);

        i = new ItemStack(Material.LEATHER_HELMET);
        leather = (LeatherArmorMeta) i.getItemMeta();
        leather.setColor(color);
        leather.setUnbreakable(true);
        i.setItemMeta(leather);
        p.getInventory().setHelmet(i);
        i.setType(Material.LEATHER_LEGGINGS);
        p.getInventory().setLeggings(i);
        i.setType(Material.LEATHER_BOOTS);
        p.getInventory().setBoots(i);

        if (ShopGUI.upgrades.get(p).contains(Upgrade.CHESTPLATE_3)) {
            i = new ItemStack(Material.DIAMOND_CHESTPLATE);
        } else if (ShopGUI.upgrades.get(p).contains(Upgrade.CHESTPLATE_2)) {
            i = new ItemStack(Material.IRON_CHESTPLATE);
        } else if (ShopGUI.upgrades.get(p).contains(Upgrade.CHESTPLATE_1)) {
            i = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
        } else {
            i.setType(Material.LEATHER_CHESTPLATE);
            p.getInventory().setChestplate(i);
            return;
        }

        m = i.getItemMeta();
        m.setUnbreakable(true);
        i.setItemMeta(m);
        p.getInventory().setChestplate(i);
    }

    public static void spawnRedDisc() {
        Item disc = Region.RED_DISC.getWorld().dropItem(Region.RED_DISC.add(.5, 0, .5), new ItemStack(Material.MUSIC_DISC_PIGSTEP));
        new BukkitRunnable() {
            @Override
            public void run() {
                disc.setVelocity(new Vector(0, 0, 0));
            }
        }.runTaskLater(EventCTW.plugin, 1);
    }

    public static void spawnBlueDisc() {
        Item disc = Region.BLUE_DISC.getWorld().dropItem(Region.BLUE_DISC.add(.5, 0, .5), new ItemStack(Material.MUSIC_DISC_CAT));
        new BukkitRunnable() {
            @Override
            public void run() {
                disc.setVelocity(new Vector(0, 0, 0));
            }
        }.runTaskLater(EventCTW.plugin, 1);
    }
}
