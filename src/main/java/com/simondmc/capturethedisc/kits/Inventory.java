package com.simondmc.capturethedisc.kits;

import com.simondmc.capturethedisc.game.Teams;
import com.simondmc.capturethedisc.shop.ShopGUI;
import com.simondmc.capturethedisc.shop.Upgrade;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Inventory {

    public static void giveArcher(Player p) {
        resetKit(p);

        ItemStack crossbow = new ItemStack(Material.CROSSBOW);
        ItemMeta m = crossbow.getItemMeta();
        m.setUnbreakable(true);
        crossbow.setItemMeta(m);
        p.getInventory().addItem(crossbow);

        p.getInventory().addItem(new ItemStack(Material.ARROW));
    }

    public static void giveTactician(Player p) {
        resetKit(p);

        ItemStack rod = new ItemStack(Material.FISHING_ROD);
        ItemMeta m = rod.getItemMeta();
        m.setUnbreakable(true);
        rod.setItemMeta(m);
        p.getInventory().addItem(rod);

        ItemStack pot = new ItemStack(Material.SPLASH_POTION);
        PotionMeta potmeta = (PotionMeta) pot.getItemMeta();
        // can't use basepotiondata because i can't get rid of the default 3-minute speed
        potmeta.setColor(Color.fromRGB(135,206,235)); // close enough idk i got this from searching sky green on google
        potmeta.setDisplayName("§rSplash Potion of Swiftness");
        potmeta.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 600, 0, false, true), false);
        ArrayList<String> lore = new ArrayList<>(Arrays.asList(" ", "§cNotice: You can only splash", "§cthis potion on yourself."));
        potmeta.setLore(lore);
        pot.setItemMeta(potmeta);
        p.getInventory().addItem(pot);

        p.getInventory().addItem(new ItemStack(Material.SCAFFOLDING, 10));
    }

    public static void giveTank(Player p) {
        resetKit(p);

        ItemStack i = new ItemStack(Material.CHAINMAIL_HELMET);
        ItemMeta imeta = i.getItemMeta();
        imeta.setUnbreakable(true);

        i.setItemMeta(imeta);
        p.getInventory().setHelmet(i);

        i = new ItemStack(Material.IRON_BOOTS);
        i.setItemMeta(imeta);
        p.getInventory().setBoots(i);
    }

    private static void resetKit(Player p) {
        List<Material> toRemove = new ArrayList<>(Arrays.asList(
                Material.CHAINMAIL_HELMET,
                Material.IRON_BOOTS,
                Material.FISHING_ROD,
                Material.SPLASH_POTION,
                Material.SCAFFOLDING,
                Material.CROSSBOW,
                Material.ARROW,
                Material.LEATHER_HELMET,
                Material.LEATHER_CHESTPLATE,
                Material.LEATHER_LEGGINGS,
                Material.LEATHER_BOOTS
        ));
        // remove all kit relevant items
        for (ItemStack item : p.getInventory().getContents()) {
            if (item == null) continue;
            if (toRemove.contains(item.getType())) {
                p.getInventory().remove(item);
            }
        }
        // prevent offhand smuggle
        if (toRemove.contains(p.getInventory().getItemInOffHand().getType())) {
            p.getInventory().setItemInOffHand(null);
        }
        setArmor(p);
    }

    public static void fillInv(Player p) {
        ItemStack i;
        ItemMeta m;

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

        i = new ItemStack(Material.GOLDEN_APPLE);
        m = i.getItemMeta();
        m.setDisplayName("§rGolden Apple");
        i.setItemMeta(m);

        p.getInventory().setItem(8, i);

        setArmor(p);

        // give corresponding kit
        if (Kits.getKit(p) == null) return;
        switch (Kits.getKit(p)) {
            case ARCHER:
                giveArcher(p);
                break;
            case TACTICIAN:
                giveTactician(p);
                break;
            case TANK:
                giveTank(p);
                break;
        }
    }

    private static void setArmor(Player p) {
        Color color = Teams.getRed().contains(p) ? Color.fromRGB(255, 0, 0) : Color.fromRGB(0, 255, 0);

        ItemStack i = new ItemStack(Material.LEATHER_HELMET);
        LeatherArmorMeta leather = (LeatherArmorMeta) i.getItemMeta();
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
        ItemMeta m = i.getItemMeta();
        m.setUnbreakable(true);
        i.setItemMeta(m);
        p.getInventory().setChestplate(i);
    }
}
