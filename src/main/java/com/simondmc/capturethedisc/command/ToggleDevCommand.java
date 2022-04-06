package com.simondmc.capturethedisc.command;

import com.simondmc.capturethedisc.CaptureTheDisc;
import com.simondmc.capturethedisc.command.template.CommandType;
import com.simondmc.capturethedisc.command.template.SuperCommand;
import com.simondmc.capturethedisc.util.Config;
import org.bukkit.entity.Player;

public class ToggleDevCommand implements SuperCommand {
    public String getLabel() {
        return "togglectddev";
    }

    public CommandType getType() {
        return CommandType.ADMIN_COMMAND;
    }

    public void runCommand(Player p, String[] args) {
        if (!CaptureTheDisc.plugin.getConfig().getStringList("devs").contains(p.getUniqueId().toString())) {
            Config.addString("devs", p.getUniqueId().toString());
            p.sendMessage("§aWoooo! You are now marked as a CTD developer!");
        } else {
            Config.removeString("devs", p.getUniqueId().toString());
            p.sendMessage("§aYou are no longer marked as a CTD developer.");
        }
    }
}
