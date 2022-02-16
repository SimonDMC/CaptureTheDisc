package com.simondmc.eventctw.shop;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ShopItem {
    Material itemMaterial;
    String itemName;
    int cost;
    int count = 1;
    ItemMeta itemMeta;
    ItemStack itemToRecieve;

    public ShopItem(Material itemMaterial, String itemName, int cost) {
        this.itemMaterial = itemMaterial;
        this.itemName = itemName;
        this.cost = cost;
    }
    public ShopItem(Material itemMaterial, String itemName, int cost, int count) {
        this.itemMaterial = itemMaterial;
        this.itemName = itemName;
        this.cost = cost;
        this.count = count;
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
    ItemStack getItemToRecieve() {
        if (itemToRecieve != null) return itemToRecieve;
        ItemStack item = new ItemStack(itemMaterial, count);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName("§r" + itemName);
        item.setItemMeta(itemMeta);
        return item;
    }
}
