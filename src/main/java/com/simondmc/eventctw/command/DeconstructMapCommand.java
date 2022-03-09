package com.simondmc.eventctw.command;

import com.simondmc.eventctw.command.template.CommandType;
import com.simondmc.eventctw.command.template.SuperCommand;
import com.simondmc.eventctw.map.Map;
import org.bukkit.entity.Player;

public class DeconstructMapCommand implements SuperCommand {
    public String getLabel() {
        return "deconstructmap";
    }

    public CommandType getType() {
        return CommandType.ADMIN_COMMAND;
    }

    public void runCommand(Player p, String[] args) {
        long time = System.currentTimeMillis();
        Map.deconstructMap(p);
        p.sendMessage("§aDone! Took " + (System.currentTimeMillis() - time) + "ms.");
    }
}
