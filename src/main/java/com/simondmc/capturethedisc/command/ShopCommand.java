package com.simondmc.capturethedisc.command;

import com.simondmc.capturethedisc.command.template.CommandType;
import com.simondmc.capturethedisc.command.template.SuperCommand;
import com.simondmc.capturethedisc.game.GameCore;
import com.simondmc.capturethedisc.shop.ShopGUI;
import org.bukkit.entity.Player;

public class ShopCommand implements SuperCommand {
    public String getLabel() {
        return "openshop";
    }

    public CommandType getType() {
        return CommandType.ADMIN_COMMAND;
    }

    public void runCommand(Player p, String[] args) {
        if (!GameCore.isOn()) {
            p.sendMessage("§cThe game isn't on right now!");
            return;
        }
        ShopGUI.openShopGui(p);
    }
}
