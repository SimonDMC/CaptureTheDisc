package com.simondmc.eventctw.command;

import com.simondmc.eventctw.EventCTW;
import com.simondmc.eventctw.command.template.CommandType;
import com.simondmc.eventctw.command.template.SuperCommand;
import com.simondmc.eventctw.util.Config;
import org.bukkit.entity.Player;

public class ToggleDevCommand implements SuperCommand {
    public String getLabel() {
        return "togglectwdev";
    }

    public CommandType getType() {
        return CommandType.ADMIN_COMMAND;
    }

    public void runCommand(Player p, String[] args) {
        if (!EventCTW.plugin.getConfig().getStringList("devs").contains(p.getUniqueId().toString())) {
            Config.addString("devs", p.getUniqueId().toString());
            p.sendMessage("§aWoooo! You are now marked as a CTW developer!");
        } else {
            Config.removeString("devs", p.getUniqueId().toString());
            p.sendMessage("§aYou are no longer marked as a CTW developer.");
        }
    }
}
