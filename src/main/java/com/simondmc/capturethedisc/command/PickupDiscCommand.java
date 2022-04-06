package com.simondmc.capturethedisc.command;

import com.simondmc.capturethedisc.command.template.CommandType;
import com.simondmc.capturethedisc.command.template.SuperCommand;
import com.simondmc.capturethedisc.game.GameCore;
import org.bukkit.entity.Player;

public class PickupDiscCommand implements SuperCommand {
    public String getLabel() {
        return "pickupdisc";
    }

    public CommandType getType() {
        return CommandType.ADMIN_COMMAND;
    }

    public void runCommand(Player p, String[] args) {
        GameCore.setDiscHolder(p);
        p.sendMessage("§7You are now marked as a disc holder (for testing purposes)");
    }
}