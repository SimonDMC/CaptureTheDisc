package com.simondmc.capturethedisc.command;

import com.simondmc.capturethedisc.CaptureTheDisc;
import com.simondmc.capturethedisc.command.template.CommandType;
import com.simondmc.capturethedisc.command.template.SuperCommand;
import com.simondmc.capturethedisc.util.Config;
import org.bukkit.entity.Player;

public class TogglePerformanceCommand implements SuperCommand {
    public String getLabel() {
        return "togglectdperformance";
    }

    public CommandType getType() {
        return CommandType.ADMIN_COMMAND;
    }

    public void runCommand(Player p, String[] args) {
        if (!CaptureTheDisc.plugin.getConfig().getStringList("performance").contains(p.getUniqueId().toString())) {
            Config.addString("performance", p.getUniqueId().toString());
            p.sendMessage("§aYou will now recieve performance updates.");
        } else {
            Config.removeString("performance", p.getUniqueId().toString());
            p.sendMessage("§aYou will no longer recieve performance updates.");
        }
    }
}
