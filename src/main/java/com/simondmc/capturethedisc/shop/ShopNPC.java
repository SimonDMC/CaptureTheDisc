package com.simondmc.capturethedisc.shop;

import com.simondmc.capturethedisc.region.Region;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;

public class ShopNPC {

    public static void initShopNpc() {
        Villager[] shops = {(Villager) Region.getWorld().spawnEntity(Region.RED_SHOP, EntityType.VILLAGER),
                (Villager) Region.getWorld().spawnEntity(Region.GREEN_SHOP, EntityType.VILLAGER)};
        for (Villager v : shops) {
            v.setProfession(Villager.Profession.WEAPONSMITH);
            v.setCustomName("§e§lShop");
            v.setCustomNameVisible(true);
            v.setVillagerLevel(5);
            v.setAI(false);
            v.setSilent(true);
        }
    }
}
