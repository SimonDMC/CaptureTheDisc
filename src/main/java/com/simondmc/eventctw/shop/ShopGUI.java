package com.simondmc.eventctw.shop;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShopGUI {
    public static final List<Integer> shopSlots = new ArrayList<>(Arrays.asList(10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25));
    public static List<ShopItem> shopItems = new ArrayList<>(Arrays.asList(
            // TODO: balance costs
            new ShopItem(Material.IRON_SWORD, "Sword Upgrade I", 20, CustomItem.UPGRADE_SWORD),
            new ShopItem(Material.CHAINMAIL_CHESTPLATE, "Chestplate Upgrade I", 20, CustomItem.UPGRADE_CHESTPLATE),
            new ShopItem(Material.IRON_AXE, "Axe Upgrade I", 20, CustomItem.UPGRADE_AXE),
            new ShopItem(Material.GOLDEN_APPLE, "Golden Apple", 10),
            new ShopItem(Material.ARROW, "Arrow", 15, 16),
            new ShopItem(Material.TNT, "TNT", 10)
    ));

    public static Inventory generateShop(Player p) {
        Inventory shop = Bukkit.createInventory(p, 45, "Shop§k"); // hacky way of avoiding false positive
        ItemStack border = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta iMeta = border.getItemMeta();
        iMeta.setDisplayName("§k"); // if it works, it works
        border.setItemMeta(iMeta);
        // fill border with glass
        for (int i : new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 37, 38, 39, 41, 42, 43, 44})
            shop.setItem(i, border);
        ItemStack close = new ItemStack(Material.BARRIER);
        iMeta.setDisplayName("§cClose");
        close.setItemMeta(iMeta);
        shop.setItem(40, close);

        for (ShopItem item : shopItems)
            shop.setItem(shopSlots.get(shopItems.indexOf(item)), item.getShopItem());

        // permanent upgrade check goes here

        return shop;
    }

    public static void openShopGui(Player p) {
        p.openInventory(generateShop(p));
    }
}
