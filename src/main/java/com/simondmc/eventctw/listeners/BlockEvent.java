package com.simondmc.eventctw.listeners;

import com.simondmc.eventctw.game.GameCore;
import com.simondmc.eventctw.region.Region;
import com.simondmc.eventctw.util.Utils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ItemDespawnEvent;

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
        if (Utils.inRegion(l, Region.RED_NO_BLOCKS) || Utils.inRegion(l, Region.BLUE_NO_BLOCKS)) {
            e.getPlayer().sendMessage("§cYou can't place blocks here!");
            e.setCancelled(true);
            return;
        }
        // no blocks in disc area
        if (Utils.inRegion(l, Region.RED_DISC_AREA) || Utils.inRegion(l, Region.BLUE_DISC_AREA)) {
            e.getPlayer().sendMessage("§cYou can't place blocks in a disc area!");
            e.setCancelled(true);
            return;
        }
        placedBlocks.add(e.getBlock().getLocation());

        if (e.getBlock().getType().equals(Material.TNT)) {
            Utils.playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
            p.sendMessage("§eRight Click or shoot a TNT block to light it!");
            System.out.println("placed");
        }
    }

    @EventHandler
    public void breakBlock(BlockBreakEvent e) {
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
}
