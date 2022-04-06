package com.simondmc.capturethedisc.shop;

import com.simondmc.capturethedisc.game.Coins;
import com.simondmc.capturethedisc.util.Utils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ShopItem {
    Material itemMaterial;
    String itemName;
    public int cost; // idk why this is public but it fixes an error
    int count = 1;
    Upgrade level;
    PotionType tippedArrowEffect;

    public ShopItem(Material itemMaterial, String itemName, int cost) {
        this.itemMaterial = itemMaterial;
        this.itemName = itemName;
        this.cost = cost;
    }

    public ShopItem(Material itemMaterial, String itemName, int cost, Upgrade level) {
        this.itemMaterial = itemMaterial;
        this.itemName = itemName;
        this.cost = cost;
        this.level = level;
    }

    public ShopItem(Material itemMaterial, String itemName, int cost, int count) {
        this.itemMaterial = itemMaterial;
        this.itemName = itemName;
        this.cost = cost;
        this.count = count;
    }

    public ShopItem(Material itemMaterial, String itemName, int cost, int count, PotionType tippedArrowEffect) {
        this.itemMaterial = itemMaterial;
        this.itemName = itemName;
        this.cost = cost;
        this.count = count;
        this.tippedArrowEffect = tippedArrowEffect;
    }

    // generate an item that will display in shop
    ItemStack getShopItem() {
        ItemStack item = new ItemStack(itemMaterial, count);
        // if tipped arrow
        if (tippedArrowEffect != null) {
            PotionMeta itemMeta = (PotionMeta) item.getItemMeta();
            itemMeta.setBasePotionData(new PotionData(tippedArrowEffect));
            if (item.getAmount() == 1) itemMeta.setDisplayName("§e" + itemName);
            else itemMeta.setDisplayName("§e" + item.getAmount() + "x " + itemName);
            List<String> lore = new ArrayList<>(Arrays.asList(
                    "§7You will recieve " + item.getAmount() + "x " + itemName,
                    "",
                    "§7Cost: §6" + cost + " Coins"
            ));
            itemMeta.setLore(lore);
            item.setItemMeta(itemMeta);
        } else {
            ItemMeta itemMeta = item.getItemMeta();
            if (item.getAmount() == 1) itemMeta.setDisplayName("§e" + itemName);
            else itemMeta.setDisplayName("§e" + item.getAmount() + "x " + itemName);
            List<String> lore;
            if (level == null) {
                lore = new ArrayList<>(Arrays.asList(
                        "§7You will recieve " + item.getAmount() + "x " + itemName,
                        "",
                        "§7Cost: §6" + cost + " Coins"
                ));
            } else {
                lore = new ArrayList<>(Arrays.asList(
                        "§aThis is a permanent upgrade",
                        "",
                        "§7Cost: §6" + cost + " Coins"
                ));
            }
            // if axe set damage to 0
            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            itemMeta.setLore(lore);
            item.setItemMeta(itemMeta);
        }
        return item;
    }

    // give actual item to recieve when buying
    public SlotItem getItemToRecieve(Player p) {
        // not completely garbage anymore holy moly

        // not primitive for the sake of being nullable
        Integer toReplace = null;
        if (level != null)
            // TODO: make a list of purchased upgrades to persist and make only buyable once
            switch (level) {
                case SWORD_NONE:
                    toReplace = Utils.findMatInInventory(p, Material.STONE_SWORD);
                    Utils.replaceUpgrade(p, Upgrade.SWORD_NONE, Upgrade.SWORD_1);
                    break;
                case SWORD_1:
                    toReplace = Utils.findMatInInventory(p, Material.IRON_SWORD);
                    Utils.replaceUpgrade(p, Upgrade.SWORD_1, Upgrade.SWORD_2);
                    break;
                case CHESTPLATE_NONE:
                    Utils.buyChestplate(p, Material.CHAINMAIL_CHESTPLATE, cost);
                    Utils.replaceUpgrade(p, Upgrade.CHESTPLATE_NONE, Upgrade.CHESTPLATE_1);
                    return null;
                case CHESTPLATE_1:
                    Utils.buyChestplate(p, Material.IRON_CHESTPLATE, cost);
                    Utils.replaceUpgrade(p, Upgrade.CHESTPLATE_1, Upgrade.CHESTPLATE_2);
                    return null;
                case CHESTPLATE_2:
                    Utils.buyChestplate(p, Material.DIAMOND_CHESTPLATE, cost);
                    Utils.replaceUpgrade(p, Upgrade.CHESTPLATE_2, Upgrade.CHESTPLATE_3);
                    return null;
                case AXE_NONE:
                    toReplace = Utils.findMatInInventory(p, Material.STONE_AXE);
                    Utils.replaceUpgrade(p, Upgrade.AXE_NONE, Upgrade.AXE_1);
                    break;
                case AXE_1:
                    toReplace = Utils.findMatInInventory(p, Material.IRON_AXE);
                    Utils.replaceUpgrade(p, Upgrade.AXE_1, Upgrade.AXE_2);
                    break;
            }
        ItemStack item = new ItemStack(itemMaterial, count);

        // if tipped arrow
        if (tippedArrowEffect != null) {
            PotionMeta itemMeta = (PotionMeta) item.getItemMeta();
            itemMeta.setBasePotionData(new PotionData(tippedArrowEffect));
            itemMeta.setDisplayName("§r" + itemName);
            item.setItemMeta(itemMeta);
        } else {
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
            if (level == null) itemMeta.setDisplayName("§r" + itemName);
            item.setItemMeta(itemMeta);
        }

        // subtract cost from balance
        Coins.addCoins(p, -cost);
        Utils.playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1.5f);

        SlotItem slotitem = new SlotItem(item, toReplace);
        return slotitem;
    }
}
