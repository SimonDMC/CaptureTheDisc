package com.simondmc.eventctw.shop;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ShopGUI {
    private static final Inventory shop = Bukkit.createInventory(null, 45, "Shop");
    public static final int[] shopSlots = {10,11,12,13,14,15,16,19,20,21,22,23,24,25};

    public static void initShop() {
        ItemStack border = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta iMeta = border.getItemMeta();
        iMeta.setDisplayName("§k");
        border.setItemMeta(iMeta);
        // fill border with glass
        for (int i : new int[]{0,1,2,3,4,5,6,7,8,9,17,18,26,27,35,36,37,38,39,41,42,43,44})
            shop.setItem(i, border);
        ItemStack close = new ItemStack(Material.BARRIER);
        iMeta.setDisplayName("§cClose");
        close.setItemMeta(iMeta);
        shop.setItem(40, close);
        fillShop();
    }
    private static void fillShop() {
        List<ItemStack> shopItems = new ArrayList<>();

        // DEFINE SHOP METAS HERE
        ItemMeta swordDisplay = new ItemStack(Material.IRON_SWORD).getItemMeta();
        swordDisplay.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
        swordDisplay.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        // DEFINE ITEMS HERE
        ItemMeta swordItemMeta = new ItemStack(Material.IRON_SWORD).getItemMeta();
        swordItemMeta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
        ItemStack swordItem = new ItemStack(Material.IRON_SWORD);
        swordItem.setItemMeta(swordItemMeta);

        // TODO: make shopitem an actual object
        // ADD SHOP ITEMS HERE
        shopItems.add(ShopItem.create(Material.IRON_SWORD, "Sharpness Sword", 20, 1, swordDisplay, swordItem));
        shopItems.add(ShopItem.create(Material.GOLDEN_APPLE, "Golden Apple", 10));
        shopItems.add(ShopItem.create(Material.ARROW, "Arrow", 15, 16));
        shopItems.add(ShopItem.create(Material.TNT, "TNT", 10));
        shopItems.add(ShopItem.create(Material.PAPER, "Placeholder Item", 69, 3));

        for (ItemStack item : shopItems)
            shop.setItem(shopSlots[shopItems.indexOf(item)], item);
    }
    public static Inventory getShopGui() {
        return shop;
    }
}
