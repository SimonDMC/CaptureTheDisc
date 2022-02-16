package com.simondmc.eventctw.shop;

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
            if (ShopItem.shopMap.containsKey(e.getSlot())) {
                Player p = (Player) e.getWhoClicked();
                p.getInventory().addItem(ShopItem.shopMap.get(e.getSlot()));
                p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1.5f);
            }
        }
    }
}
