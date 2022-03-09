package com.simondmc.eventctw.shop;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShopGUI {
    private static final Inventory shop = Bukkit.createInventory(null, 45, "Shop");
    public static final List<Integer> shopSlots = new ArrayList<>(Arrays.asList(10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25));
    public static List<ShopItem> shopItems;

    public static void initShop() {
        ItemStack border = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta iMeta = border.getItemMeta();
        iMeta.setDisplayName("§k");
        border.setItemMeta(iMeta);
        // fill border with glass
        for (int i : new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 37, 38, 39, 41, 42, 43, 44})
            shop.setItem(i, border);
        ItemStack close = new ItemStack(Material.BARRIER);
        iMeta.setDisplayName("§cClose");
        close.setItemMeta(iMeta);
        shop.setItem(40, close);
        fillShop();
    }

    private static void fillShop() {
        // DEFINE ITEMS HERE
        ItemStack sword = new ItemStack(Material.IRON_SWORD);

        // ADD SHOP ITEMS HERE
        shopItems = new ArrayList<>(Arrays.asList(
                // TODO: balance costs
                new ShopItem(Material.IRON_SWORD, "Sword Upgrade", 20, CustomItem.UPGRADE_SWORD),
                new ShopItem(Material.CHAINMAIL_CHESTPLATE, "Chestplate Upgrade", 20, CustomItem.UPGRADE_CHESTPLATE),
                new ShopItem(Material.IRON_AXE, "Axe Upgrade 1", 20, CustomItem.UPGRADE_AXE_1),
                new ShopItem(Material.DIAMOND_AXE, "Axe Upgrade 2", 20, CustomItem.UPGRADE_AXE_2),
                new ShopItem(Material.GOLDEN_APPLE, "Golden Apple", 10),
                new ShopItem(Material.ARROW, "Arrow", 15, 16),
                new ShopItem(Material.TNT, "TNT", 10)
        ));

        for (ShopItem item : shopItems)
            shop.setItem(shopSlots.get(shopItems.indexOf(item)), item.getShopItem());
    }

    public static Inventory getShopGui() {
        return shop;
    }
}
