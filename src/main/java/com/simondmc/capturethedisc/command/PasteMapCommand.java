package com.simondmc.capturethedisc.command;

import com.simondmc.capturethedisc.command.template.CommandType;
import com.simondmc.capturethedisc.command.template.SuperCommand;
import com.simondmc.capturethedisc.map.Map;
import com.simondmc.capturethedisc.util.Config;
import org.bukkit.entity.Player;

public class PasteMapCommand implements SuperCommand {
    public String getLabel() {
        return "pastemap";
    }

    public CommandType getType() {
        return CommandType.ADMIN_COMMAND;
    }

    public void runCommand(Player p, String[] args) {
        long time = System.currentTimeMillis();
        Map.pasteMapSchem(p);
        Config.devAnnounce("§aMap pasted! Took " + (System.currentTimeMillis() - time) + "ms.");
    }
}
