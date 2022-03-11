package com.simondmc.eventctw.command;

import com.simondmc.eventctw.command.template.CommandType;
import com.simondmc.eventctw.command.template.SuperCommand;
import com.simondmc.eventctw.shop.ShopGUI;
import org.bukkit.entity.Player;

public class ShopCommand implements SuperCommand {
    public String getLabel() {
        return "openshop";
    }

    public CommandType getType() {
        return CommandType.ADMIN_COMMAND;
    }

    public void runCommand(Player p, String[] args) {
        //if (!GameCore.isOn()) return;
        ShopGUI.openShopGui(p);
    }
}
