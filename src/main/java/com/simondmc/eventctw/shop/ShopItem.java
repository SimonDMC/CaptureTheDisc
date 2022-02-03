package com.simondmc.eventctw.shop;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ShopItem {

    public static ItemStack create(Material itemMaterial, String itemName, int cost) {
        return createShopItem(new ItemStack(itemMaterial), itemName, cost);
    }
    public static ItemStack create(Material itemMaterial, String itemName, int cost, int count) {
        return createShopItem(new ItemStack(itemMaterial, count), itemName, cost);
    }
    public static ItemStack create(Material itemMaterial, String itemName, int cost, int count, ItemMeta iMeta) {
        ItemStack item = new ItemStack(itemMaterial, count);
        item.setItemMeta(iMeta);
        return createShopItem(item, itemName, cost);
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
}
