package com.simondmc.eventctw.shop;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShopItem {
    public static HashMap<Integer, ItemStack> shopMap = new HashMap<>();

    public static ItemStack create(Material itemMaterial, String itemName, int cost) {
        addToMap(new ItemStack(itemMaterial), itemName);
        return createShopItem(new ItemStack(itemMaterial), itemName, cost);
    }
    public static ItemStack create(Material itemMaterial, String itemName, int cost, int count) {
        addToMap(new ItemStack(itemMaterial, count), itemName);
        return createShopItem(new ItemStack(itemMaterial, count), itemName, cost);
    }
    public static ItemStack create(Material itemMaterial, String itemName, int cost, int count, ItemMeta iMeta, ItemStack itemToRecieve) {
        ItemStack displayItem = new ItemStack(itemMaterial, count);
        displayItem.setItemMeta(iMeta);
        addToMap(itemToRecieve, itemName);
        return createShopItem(displayItem, itemName, cost);
    }

    private static ItemStack createShopItem(ItemStack item, String itemName, int cost) {
        ItemMeta iMeta = item.getItemMeta();
        if (item.getAmount() == 1)
            iMeta.setDisplayName("§e" + itemName);
        else
            iMeta.setDisplayName("§e" + item.getAmount() + "x " + itemName);
        List<String> lore = new ArrayList<>();
        lore.add("§7You will recieve " + item.getAmount() + "x " + itemName);
        lore.add("");
        lore.add("§7Cost: §6" + cost + " Coins");
        iMeta.setLore(lore);
        item.setItemMeta(iMeta);
        return item;
    }
    private static void addToMap(ItemStack item, String itemName) {
        // generate item
        ItemMeta meta = item.getItemMeta();
        // add to shop map
        for (int slot : ShopGUI.shopSlots) {
            if (!shopMap.containsKey(slot)) {
                shopMap.put(slot, item);
                return;
            }
        }
    }
}
