package com.simondmc.capturethedisc.command;

import com.simondmc.capturethedisc.command.template.CommandType;
import com.simondmc.capturethedisc.command.template.SuperCommand;
import com.simondmc.capturethedisc.shop.ShopGUI;
import com.simondmc.capturethedisc.shop.Upgrade;
import org.bukkit.entity.Player;

public class TestUpgradesCommand implements SuperCommand {
    public String getLabel() {
        return "debugupgrades";
    }

    public CommandType getType() {
        return CommandType.ADMIN_COMMAND;
    }

    public void runCommand(Player p, String[] args) {
        p.sendMessage("Your current CTD upgrades: ");
        for (Upgrade u : ShopGUI.upgrades.get(p)) {
            p.sendMessage(u.toString());
        }
    }
}
