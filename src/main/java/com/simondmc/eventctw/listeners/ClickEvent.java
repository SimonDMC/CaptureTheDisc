package com.simondmc.eventctw.listeners;

import com.simondmc.eventctw.game.Coins;
import com.simondmc.eventctw.game.GameCore;
import com.simondmc.eventctw.kits.Inventory;
import com.simondmc.eventctw.kits.Kit;
import com.simondmc.eventctw.kits.Kits;
import com.simondmc.eventctw.shop.ShopGUI;
import com.simondmc.eventctw.shop.ShopItem;
import com.simondmc.eventctw.shop.SlotItem;
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
import org.bukkit.potion.PotionEffectType;

public class ClickEvent implements Listener {

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
                Player p = (Player) e.getWhoClicked();
                ShopItem shopItem = ShopGUI.buildCurrentShopItems(p).get(slot);
                if (!Coins.hasCoins(p, shopItem.cost)) {
                    p.sendMessage("§cYou cannot afford this!");
                    Utils.playSound(p, Sound.ENTITY_VILLAGER_NO);
                    return;
                }

                SlotItem item = shopItem.getItemToRecieve(p);

                // if not chestplate (handled directly)
                if (item != null) {
                    // replace if item has slot attributed to it
                    if (item.slot != null) {
                        p.getInventory().setItem(item.slot, item.item);
                    } else {
                        p.getInventory().addItem(item.item);
                    }
                }

                // reload shop cuz something could have changed
                ShopGUI.openShopGui(p);
            }
        }
    }

    // right click to open
    @EventHandler
    public void openShop(PlayerInteractEntityEvent e) {
        if (!GameCore.isOn()) return;
        if (!(e.getRightClicked() instanceof Villager)) return;
        e.setCancelled(true);
        clickedVillager(e.getPlayer(), (Villager) e.getRightClicked());
    }

    // left click to open
    @EventHandler
    public void leftClickShop(EntityDamageByEntityEvent e) {
        if (!GameCore.isOn()) return;
        if (!(e.getEntity() instanceof Villager)) return;
        e.setCancelled(true);
        if (!(e.getDamager() instanceof Player)) return;
        clickedVillager((Player) e.getDamager(), (Villager) e.getEntity());
    }

    void clickedVillager(Player p, Villager v) {

        // selection sound + clear speed
        if (!v.getProfession().equals(Villager.Profession.WEAPONSMITH)) {
            Utils.playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
            p.removePotionEffect(PotionEffectType.SPEED);
        }

        switch (v.getProfession()) {
            case WEAPONSMITH:
                ShopGUI.openShopGui(p);
                break;
            case FLETCHER:
                Inventory.giveArcher(p);
                Kits.setKit(p, Kit.ARCHER);
                break;
            case LIBRARIAN:
                Inventory.giveTactician(p);
                Kits.setKit(p, Kit.TACTICIAN);
                break;
            case ARMORER:
                Inventory.giveTank(p);
                Kits.setKit(p, Kit.TANK);
                Utils.playSound(p, Sound.ITEM_ARMOR_EQUIP_IRON);
                break;
        }
    }
}
