package com.simondmc.eventctw.command;

import com.simondmc.eventctw.command.template.CommandType;
import com.simondmc.eventctw.command.template.SuperCommand;
import com.simondmc.eventctw.game.GameCore;
import com.simondmc.eventctw.shop.ShopGUI;
import com.simondmc.eventctw.shop.Upgrade;
import org.bukkit.entity.Player;

public class TestUpgradesCommand implements SuperCommand {
    public String getLabel() {
        return "debugupgrades";
    }

    public CommandType getType() {
        return CommandType.ADMIN_COMMAND;
    }

    public void runCommand(Player p, String[] args) {
        p.sendMessage("Your current CTW upgrades: ");
        for (Upgrade u : ShopGUI.upgrades.get(p)) {
            p.sendMessage(u.toString());
        }
    }
}
