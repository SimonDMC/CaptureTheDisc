package com.simondmc.eventctw.command;

import com.simondmc.eventctw.command.template.CommandType;
import com.simondmc.eventctw.command.template.SuperCommand;
import com.simondmc.eventctw.map.Map;
import com.simondmc.eventctw.util.Config;
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
        Config.devAnnounce("§aMap deleted! Took " + (System.currentTimeMillis() - time) + "ms.");
    }
}
