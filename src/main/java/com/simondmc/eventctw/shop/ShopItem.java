package com.simondmc.eventctw.shop;

import com.simondmc.eventctw.game.Coins;
import com.simondmc.eventctw.util.Utils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ShopItem {
    Material itemMaterial;
    String itemName;
    int cost;
    int count = 1;
    ItemMeta itemMeta;
    ItemStack itemToRecieve;
    CustomItem customShopItem;

    public ShopItem(Material itemMaterial, String itemName, int cost) {
        this.itemMaterial = itemMaterial;
        this.itemName = itemName;
        this.cost = cost;
    }

    public ShopItem(Material itemMaterial, String itemName, int cost, CustomItem customShopItem) {
        this.itemMaterial = itemMaterial;
        this.itemName = itemName;
        this.cost = cost;
        this.customShopItem = customShopItem;
    }

    public ShopItem(Material itemMaterial, String itemName, int cost, int count) {
        this.itemMaterial = itemMaterial;
        this.itemName = itemName;
        this.cost = cost;
        this.count = count;
    }

    public ShopItem(Material itemMaterial, String itemName, int cost, int count, ItemStack itemToRecieve) {
        this.itemMaterial = itemMaterial;
        this.itemName = itemName;
        this.cost = cost;
        this.count = count;
        this.itemToRecieve = itemToRecieve;
    }

    public ShopItem(Material itemMaterial, String itemName, int cost, int count, ItemMeta itemMeta, ItemStack itemToRecieve) {
        this.itemMaterial = itemMaterial;
        this.itemName = itemName;
        this.cost = cost;
        this.count = count;
        this.itemMeta = itemMeta;
        this.itemToRecieve = itemToRecieve;
    }

    // generate an item that will display in shop
    ItemStack getShopItem() {
        ItemStack item = new ItemStack(itemMaterial, count);
        ItemMeta itemMeta = (this.itemMeta != null ? this.itemMeta : item.getItemMeta());
        if (item.getAmount() == 1) itemMeta.setDisplayName("§e" + itemName);
        else itemMeta.setDisplayName("§e" + item.getAmount() + "x " + itemName);
        List<String> lore = new ArrayList<>(Arrays.asList(
                "§7You will recieve " + item.getAmount() + "x " + itemName,
                "",
                "§7Cost: §6" + cost + " Coins"
        ));
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }

    // give actual item to recieve when buying
    ItemStack getItemToRecieve(Player p) {
        // this code is completely garbage but its not worth improving
        if (customShopItem != null)
            // TODO: make a list of purchased upgrades to persist and make only buyable once
            switch (customShopItem) {
                case UPGRADE_SWORD:
                    p.getInventory().remove(Material.STONE_SWORD);
                    break;
                case UPGRADE_CHESTPLATE:
                    ItemStack item = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
                    ItemMeta m = item.getItemMeta();
                    m.setUnbreakable(true);
                    item.setItemMeta(m);
                    p.getInventory().setChestplate(item);
                    Utils.playSound(p, Sound.ITEM_ARMOR_EQUIP_CHAIN);
                    Coins.addCoins(p, -cost);
                    // return null but that throws an error
                    return new ItemStack(Material.AIR);
                case UPGRADE_AXE_1:
                    p.getInventory().remove(Material.STONE_AXE);
                    break;
                case UPGRADE_AXE_2:
                    // detection for upgrade 1 bought
                    if (false) {
                        p.sendMessage("§cYou haven't unlocked the first axe upgrade yet!");
                        return new ItemStack(Material.AIR);
                    }
                    p.getInventory().remove(Material.IRON_AXE);
                    break;
            }

        if (itemToRecieve != null) return itemToRecieve;
        ItemStack item = new ItemStack(itemMaterial, count);
        ItemMeta itemMeta = item.getItemMeta();
        // if item can be broken, make it unbreakable
        if (itemMaterial.getMaxDurability() > 0) {
            itemMeta.setUnbreakable(true);
        }
        // if axe set damage to 0
        if (itemMaterial.toString().contains("AXE")) { // fires for waxed copper but who cares
            AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", 0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
            itemMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, modifier);
        }
        if (customShopItem == null) itemMeta.setDisplayName("§r" + itemName);
        item.setItemMeta(itemMeta);

        // subtract cost from balance
        Coins.addCoins(p, -cost);
        Utils.playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1.5f);

        return item;
    }
}
