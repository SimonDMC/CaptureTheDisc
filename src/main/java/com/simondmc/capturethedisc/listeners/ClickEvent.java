package com.simondmc.capturethedisc.listeners;

import com.simondmc.capturethedisc.CaptureTheDisc;
import com.simondmc.capturethedisc.game.Coins;
import com.simondmc.capturethedisc.game.GameCore;
import com.simondmc.capturethedisc.game.Teams;
import com.simondmc.capturethedisc.kits.Inventory;
import com.simondmc.capturethedisc.kits.Kit;
import com.simondmc.capturethedisc.kits.Kits;
import com.simondmc.capturethedisc.region.Region;
import com.simondmc.capturethedisc.shop.ShopGUI;
import com.simondmc.capturethedisc.shop.ShopItem;
import com.simondmc.capturethedisc.shop.SlotItem;
import com.simondmc.capturethedisc.util.Utils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class ClickEvent implements Listener {

    @EventHandler
    public void clickShopOrKit(InventoryClickEvent e) {
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
                // clicking an empty slot throws an error ¯\_(ツ)_/¯
                ShopItem shopItem;
                try {
                    shopItem = ShopGUI.buildCurrentShopItems(p).get(slot);
                } catch (IndexOutOfBoundsException ex) {
                    return;
                }
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
                        // if hotkeyed, set to that slot
                        if (e.getClick() == ClickType.NUMBER_KEY && p.getInventory().getItem(e.getHotbarButton()) == null) {
                            p.getInventory().setItem(e.getHotbarButton(), item.item);
                        } else {
                            p.getInventory().addItem(item.item);
                        }
                    }
                }

                // TNT message
                if (item != null && item.item.getType() == Material.TNT) p.sendMessage("§aTip: Sneak while placing TNT to explode it later!");

                // reload shop cuz something could have changed
                ShopGUI.openShopGui(p);
                return;
            }
        }
        if (Objects.equals(e.getClickedInventory(), Kits.getKitGui())) {
            e.setCancelled(true);
            // get rid of that stupid inventory error
            if (e.getClickedInventory() == null) return;
            if (e.getClickedInventory().getType() == InventoryType.PLAYER) return;
            Player p = (Player) e.getWhoClicked();

            // select kit based on click slot
            switch (e.getSlot()) {
                case 11:
                    Kits.setKit(p, Kit.ARCHER);
                    Inventory.giveArcher(p);
                    Utils.playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
                    Kits.selected.add(p);
                    p.closeInventory();
                    return;
                case 13:
                    Kits.setKit(p, Kit.TACTICIAN);
                    Inventory.giveTactician(p);
                    Utils.playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
                    Kits.selected.add(p);
                    p.closeInventory();
                    return;
                case 15:
                    Kits.setKit(p, Kit.TANK);
                    Inventory.giveTank(p);
                    Utils.playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
                    Kits.selected.add(p);
                    p.closeInventory();
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
        // check for spectator
        if (!Teams.getPlayers().contains(p)) return;

        // selection sound + clear speed
        if (!v.getProfession().equals(Villager.Profession.WEAPONSMITH)) {
            // prevent from selecting kits in other team's base
            if ((Teams.getRed().contains(p) && p.getLocation().getZ() > Region.CENTER.getZ()) || (Teams.getGreen().contains(p) && p.getLocation().getZ() < Region.CENTER.getZ())) {
                p.sendMessage("§cYou can only select kits in your team's base!");
                Utils.playSound(p, Sound.ENTITY_VILLAGER_NO);
                return;
            }
            Utils.playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
            p.removePotionEffect(PotionEffectType.SPEED);
            p.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
            // remove thrown potions
            for (Entity e : p.getNearbyEntities(10, 10, 10)) {
                if (!(e instanceof ThrownPotion)) continue;
                ThrownPotion pot = (ThrownPotion) e;
                if (!(pot.getShooter() instanceof Player)) continue;
                if (p.equals(pot.getShooter())) e.remove();
            }
        }

        switch (v.getProfession()) {
            case WEAPONSMITH:
                ShopGUI.openShopGui(p);
                break;
            case FLETCHER:
                Inventory.giveArcher(p);
                Kits.setKit(p, Kit.ARCHER);
                p.sendMessage("§eSelected the §aArcher §ekit!");
                break;
            case LIBRARIAN:
                Inventory.giveTactician(p);
                Kits.setKit(p, Kit.TACTICIAN);
                p.sendMessage("§eSelected the §aTactician §ekit!");
                break;
            case ARMORER:
                Inventory.giveTank(p);
                Kits.setKit(p, Kit.TANK);
                Utils.playSound(p, Sound.ITEM_ARMOR_EQUIP_IRON);
                p.sendMessage("§eSelected the §aTank §ekit!");
                break;
        }
    }

    @EventHandler
    public void closeInv(InventoryCloseEvent e) {
        if (!GameCore.isOn()) return;
        Player p = (Player) e.getPlayer();
        if (!Kits.selected.contains(p) && Teams.getPlayers().contains(p)) {
            // i am just not dealing with this
            Kits.setKit(p, Kit.ARCHER);
            Inventory.giveArcher(p);
            Utils.playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
            Kits.selected.add(p);
            p.sendMessage("§dYou closed the kit selection screen, so you were given the Archer Kit. Change your kit by clicking one of the Kit NPCs.");
        }
    }
}
