package com.simondmc.capturethedisc.kits;

import com.simondmc.capturethedisc.region.Region;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;

public class KitNPC {
    public static void initKitNpcs() {
        Villager[] kits = {
                (Villager) Region.getWorld().spawnEntity(Region.RED_KITS, EntityType.VILLAGER),
                (Villager) Region.getWorld().spawnEntity(Region.GREEN_KITS, EntityType.VILLAGER),
                (Villager) Region.getWorld().spawnEntity(Region.RED_KITS.clone().add(2, 0, 0), EntityType.VILLAGER),
                (Villager) Region.getWorld().spawnEntity(Region.GREEN_KITS.clone().add(-2, 0, 0), EntityType.VILLAGER),
                (Villager) Region.getWorld().spawnEntity(Region.RED_KITS.clone().add(4, 0, 0), EntityType.VILLAGER),
                (Villager) Region.getWorld().spawnEntity(Region.GREEN_KITS.clone().add(-4, 0, 0), EntityType.VILLAGER),
        };
        // iterate through kit npcs
        int iterator = 0;
        for (Villager v : kits) {
            // general setup
            v.setVillagerLevel(5);
            v.setAI(false);
            v.setSilent(true);
            v.setCustomNameVisible(true);

            // first two
            if (iterator < 2) {
                v.setProfession(Villager.Profession.FLETCHER);
                v.setCustomName("§eArcher Kit");
            } else if (iterator < 4) {
                v.setProfession(Villager.Profession.LIBRARIAN);
                v.setCustomName("§eTactician Kit");
            } else {
                v.setProfession(Villager.Profession.ARMORER);
                v.setCustomName("§eTank Kit");
            }

            iterator++;
        }
    }
}
