package com.simondmc.eventctw.shop;

import com.simondmc.eventctw.game.Teams;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.*;

public class ShopGUI {
    public static final List<Integer> shopSlots = new ArrayList<>(Arrays.asList(10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25));
    public static List<ShopItem> shopItems = new ArrayList<>(Arrays.asList(
            new ShopItem(Material.IRON_SWORD, "Sword Upgrade I", 70, Upgrade.SWORD_NONE),
            new ShopItem(Material.DIAMOND_SWORD, "Sword Upgrade II", 150, Upgrade.SWORD_1),
            new ShopItem(Material.CHAINMAIL_CHESTPLATE, "Chestplate Upgrade I", 50, Upgrade.CHESTPLATE_NONE),
            new ShopItem(Material.IRON_CHESTPLATE, "Chestplate Upgrade II", 100, Upgrade.CHESTPLATE_1),
            new ShopItem(Material.DIAMOND_CHESTPLATE, "Chestplate Upgrade III", 150, Upgrade.CHESTPLATE_2),
            new ShopItem(Material.IRON_AXE, "Axe Upgrade I", 20, Upgrade.AXE_NONE),
            new ShopItem(Material.DIAMOND_AXE, "Axe Upgrade II", 50, Upgrade.AXE_1),
            new ShopItem(Material.GOLDEN_APPLE, "Golden Apple", 30),
            new ShopItem(Material.TNT, "TNT", 30),
            new ShopItem(Material.ENDER_PEARL, "Ender Pearl", 125),
            new ShopItem(Material.SHIELD, "Shield", 85),
            new ShopItem(Material.BOW, "Regular Bow", 60),
            new ShopItem(Material.TIPPED_ARROW, "Slow Falling Arrow", 40, 5, PotionType.SLOW_FALLING),
            new ShopItem(Material.TIPPED_ARROW, "Harming Arrow", 40, 5, PotionType.INSTANT_DAMAGE)
    ));

    public static Map<Player, List<Upgrade>> upgrades = new HashMap<>();

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

        List<ShopItem> items = buildCurrentShopItems(p);
        for (ShopItem item : items)
            shop.setItem(shopSlots.get(items.indexOf(item)), item.getShopItem());

        // permanent upgrade check goes here

        return shop;
    }

    public static void initUpgrades() {
        for (Player p : Teams.getPlayers()) {
            // done stupidly because otherwise it's immutable (https://stackoverflow.com/a/2965763)
            List<Upgrade> upgradeList = new ArrayList<>();
            upgradeList.addAll(Arrays.asList(Upgrade.SWORD_NONE, Upgrade.CHESTPLATE_NONE, Upgrade.AXE_NONE));
            upgrades.put(p, upgradeList);
        }
    }

    public static void openShopGui(Player p) {
        p.openInventory(generateShop(p));
    }

    // returns a list of all currently buyable items and upgrades from the shop
    public static List<ShopItem> buildCurrentShopItems(Player p) {
        List<ShopItem> builder = new ArrayList<>();
        for (ShopItem item : shopItems) {
            // if not upgradeable item or player has that specific upgrade, add it
            if (item.level == null || upgrades.get(p).contains(item.level)) {
                builder.add(item);
            }
        }
        return builder;
    }
}
