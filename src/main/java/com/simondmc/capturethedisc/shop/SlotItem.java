package com.simondmc.capturethedisc.shop;

import org.bukkit.inventory.ItemStack;

public class SlotItem {
    public Integer slot;
    public ItemStack item;

    public SlotItem(ItemStack item, Integer slot) {
        this.slot = slot;
        this.item = item;
    }
}
