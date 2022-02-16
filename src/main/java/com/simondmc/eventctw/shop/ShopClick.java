package com.simondmc.eventctw.shop;

import com.simondmc.eventctw.game.Coins;
import com.simondmc.eventctw.util.Utils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

public class ShopClick implements Listener {

    @EventHandler
    public void clickShop(InventoryClickEvent e) {
        if (e.getInventory().equals(ShopGUI.getShopGui())) {
            e.setCancelled(true);
            if(e.getClickedInventory().getType() == InventoryType.PLAYER) return;
            if (ShopGUI.shopSlots.contains(e.getSlot())) {
                int slot = ShopGUI.shopSlots.indexOf(e.getSlot());
                ShopItem shopItem = ShopGUI.shopItems.get(slot);
                Player p = (Player) e.getWhoClicked();
                if (!Coins.hasCoins(p, shopItem.cost)) {
                    p.sendMessage("§cYou cannot afford this!");
                    Utils.playSound(p, Sound.ENTITY_VILLAGER_NO);
                    return;
                }
                // subtract cost from balance
                Coins.addCoins(p, -shopItem.cost);
                p.getInventory().addItem(shopItem.getItemToRecieve());
                Utils.playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1.5f);
            }
        }
    }
}
