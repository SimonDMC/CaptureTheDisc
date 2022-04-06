package com.simondmc.capturethedisc.command;

import com.simondmc.capturethedisc.command.template.CommandType;
import com.simondmc.capturethedisc.command.template.SuperCommand;
import org.bukkit.Material;
import org.bukkit.entity.Giant;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

// UNUSED
public class TestDiscCommand implements SuperCommand {
    public String getLabel() {
        return "testdisc";
    }

    public CommandType getType() {
        return CommandType.ADMIN_COMMAND;
    }

    public void runCommand(Player p, String[] args) {
        Giant g = p.getWorld().spawn(p.getLocation(), Giant.class);
        g.getEquipment().setItemInMainHand(new ItemStack(Material.MUSIC_DISC_MELLOHI));
        g.setInvulnerable(true);
        g.setAI(false);
        PotionEffect pot = new PotionEffect(PotionEffectType.INVISIBILITY, 1000000, 0, false, false);
        g.addPotionEffect(pot);
    }
}