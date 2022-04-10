package com.simondmc.capturethedisc.kits;

import com.simondmc.capturethedisc.CaptureTheDisc;
import com.simondmc.capturethedisc.util.Utils;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RegeneratingItemHandler {

    private static final Map<Player, Integer> regeneratingItem = new HashMap<>();

    public static void startTactician(Player p) {
        int task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!regeneratingItem.containsKey(p)) return;
                if (!regeneratingItem.get(p).equals(this.getTaskId())) return;

                ItemStack pot = new ItemStack(Material.SPLASH_POTION);
                PotionMeta potmeta = (PotionMeta) pot.getItemMeta();
                // can't use basepotiondata because i can't get rid of the default 3-minute speed
                potmeta.setColor(Color.fromRGB(135,206,235)); // close enough idk i got this from searching sky green on google
                potmeta.setDisplayName("§rSplash Potion of Swiftness");
                potmeta.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 600, 0, false, true), false);
                ArrayList<String> lore = new ArrayList<>(Arrays.asList(" ", "§cNotice: You can only splash", "§cthis potion on yourself."));
                potmeta.setLore(lore);
                pot.setItemMeta(potmeta);

                p.getInventory().addItem(pot);
                Utils.playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
                p.sendMessage("§aYour swiftness potion has refilled!");

                regeneratingItem.remove(p);
            }
        }.runTaskLater(CaptureTheDisc.plugin, 1800).getTaskId(); // 1.5 minutes = 1800 ticks

        regeneratingItem.put(p, task);
    }

    public static void resetRegeneratingItem(Player p) {
        regeneratingItem.remove(p);
    }

}
