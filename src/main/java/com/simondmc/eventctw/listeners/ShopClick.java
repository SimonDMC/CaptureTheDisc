package com.simondmc.eventctw.listeners;

import com.simondmc.eventctw.game.Coins;
import com.simondmc.eventctw.game.GameCore;
import com.simondmc.eventctw.shop.ShopGUI;
import com.simondmc.eventctw.shop.ShopItem;
import com.simondmc.eventctw.util.Utils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class ShopClick implements Listener {

    @EventHandler
    public void clickShop(InventoryClickEvent e) {
        if (e.getView().getTitle().equals("Shop§k")) {
            e.setCancelled(true);
            // get rid of that stupid inventory error
            if (e.getClickedInventory() == null) return;
            if (e.getClickedInventory().getType() == InventoryType.PLAYER) return;
            // close inv if click on close slot
            if (e.getSlot() == 40) {
                e.getWhoClicked().closeInventory();
                return;
            }
            if (ShopGUI.shopSlots.contains(e.getSlot())) {
                int slot = ShopGUI.shopSlots.indexOf(e.getSlot());
                ShopItem shopItem = ShopGUI.shopItems.get(slot);
                Player p = (Player) e.getWhoClicked();
                if (!Coins.hasCoins(p, shopItem.cost)) {
                    p.sendMessage("§cYou cannot afford this!");
                    Utils.playSound(p, Sound.ENTITY_VILLAGER_NO);
                    return;
                }
                if (shopItem.getItemToRecieve(p).item == null) return;
                // replace if item has slot attributed to it
                if (shopItem.getItemToRecieve(p).slot != null) {
                    p.getInventory().setItem(shopItem.getItemToRecieve(p).slot, shopItem.getItemToRecieve(p).item);
                } else {
                    p.getInventory().addItem(shopItem.getItemToRecieve(p).item);
                }
            }
        }
    }

    // right click to open
    @EventHandler
    public void openShop(PlayerInteractEntityEvent e) {
        if (!GameCore.isOn()) return;
        if (!(e.getRightClicked() instanceof Villager)) return;
        e.setCancelled(true);
        ShopGUI.openShopGui(e.getPlayer());
    }

    // left click to open
    @EventHandler
    public void leftClickShop(EntityDamageByEntityEvent e) {
        if (!GameCore.isOn()) return;
        if (!(e.getEntity() instanceof Villager)) return;
        e.setCancelled(true);
        if (!(e.getDamager() instanceof Player)) return;
        Player p = (Player) e.getDamager();
        ShopGUI.openShopGui(p);
    }
}
