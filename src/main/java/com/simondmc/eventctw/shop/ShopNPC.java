package com.simondmc.eventctw.shop;

import com.simondmc.eventctw.region.Region;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;

public class ShopNPC {

    public static void initNpcs() {
        Villager[] shops = {(Villager) Region.RED_SHOP.getWorld().spawnEntity(Region.RED_SHOP, EntityType.VILLAGER),
                (Villager) Region.BLUE_SHOP.getWorld().spawnEntity(Region.BLUE_SHOP, EntityType.VILLAGER)};
        for (Villager v : shops) {
            v.setProfession(Villager.Profession.ARMORER);
            v.setCustomName("§e§lShop");
            v.setCustomNameVisible(true);
            v.setVillagerLevel(5);
            v.setAI(false);
            v.setSilent(true);
        }
    }
}
