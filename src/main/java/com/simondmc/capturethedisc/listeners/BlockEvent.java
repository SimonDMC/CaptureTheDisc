package com.simondmc.capturethedisc.listeners;

import com.simondmc.capturethedisc.game.GameCore;
import com.simondmc.capturethedisc.region.Region;
import com.simondmc.capturethedisc.util.Utils;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.ArrayList;
import java.util.List;

public class BlockEvent implements Listener {
    public static List<Location> placedBlocks = new ArrayList<>();

    @EventHandler
    public void placeBlock(BlockPlaceEvent e) {
        if (!GameCore.isOn()) return;
        Player p = e.getPlayer();
        if (p.getGameMode().equals(GameMode.CREATIVE)) return;
        Location l = e.getBlock().getLocation();
        if (!Utils.inRegion(l, Region.MAP)) {
            e.getPlayer().sendMessage("§cYou can't place blocks outside of the map!");
            e.setCancelled(true);
            return;
        }
        if (Utils.inRegion(l, Region.RED_NO_BLOCKS) || Utils.inRegion(l, Region.GREEN_NO_BLOCKS)) {
            e.getPlayer().sendMessage("§cYou can't place blocks here!");
            e.setCancelled(true);
            return;
        }
        // no blocks in disc area
        if (Utils.inRegion(l, Region.RED_DISC_AREA) || Utils.inRegion(l, Region.GREEN_DISC_AREA)) {
            e.getPlayer().sendMessage("§cYou can't place blocks in a disc area!");
            e.setCancelled(true);
            return;
        }
        // no blocks above beacons
        if (Utils.inRegion(l, Region.RED_DISC_BEACON_AREA) || Utils.inRegion(l, Region.GREEN_DISC_BEACON_AREA)) {
            e.getPlayer().sendMessage("§cYou can't place blocks above a beacon!");
            e.setCancelled(true);
            return;
        }
        placedBlocks.add(e.getBlock().getLocation());

        // TNT
        if (e.getBlock().getType().equals(Material.TNT)) {
            if (e.getPlayer().isSneaking()) {
                Utils.playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
                p.sendMessage("§eRight Click or shoot a TNT block to light it!");
            } else {
                e.getBlockPlaced().setType(Material.AIR);
                e.getBlockPlaced().getWorld().spawnEntity(e.getBlockPlaced().getLocation().add(.5, 0, .5), EntityType.PRIMED_TNT);
            }
        }
    }

    @EventHandler
    public void breakBlock(BlockBreakEvent e) {
        // if in world before game starts, disable block breaking
        if (e.getPlayer().getWorld().getName().equals("ctd-world") && !GameCore.isOn()) {
            e.setCancelled(true);
            return;
        }
        if (!GameCore.isOn()) return;
        Player p = e.getPlayer();
        if (p.getGameMode().equals(GameMode.CREATIVE)) return;
        if (!placedBlocks.contains(e.getBlock().getLocation())) {
            e.setCancelled(true);
            return;
        }
        placedBlocks.remove(e.getBlock().getLocation());
    }

    @EventHandler
    public void blockExplode(EntityExplodeEvent e) {
        if (GameCore.isOn()) e.blockList().clear();
    }

    // not block but who cares ig
    @EventHandler
    public void itemDespawn(ItemDespawnEvent e) {
        if (GameCore.isOn()) e.setCancelled(true);
    }

    // stop tnt from blowing up items
    @EventHandler
    public void itemExplode(EntityDamageEvent e) {
        if (!GameCore.isOn()) return;
        if (e.getCause() != EntityDamageEvent.DamageCause.BLOCK_EXPLOSION && e.getCause() != EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) return;
        if (e.getEntityType() != EntityType.DROPPED_ITEM && !(e.getEntity() instanceof Item)) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void scaffoldingLand(EntityChangeBlockEvent e) {
        if (!GameCore.isOn()) return;
        if (e.getTo() != Material.SCAFFOLDING) return;
        if (e.getBlock().getLocation().getBlockY() == -60) {
            e.setCancelled(true);
            return;
        }
        placedBlocks.add(e.getBlock().getLocation());
    }
}
