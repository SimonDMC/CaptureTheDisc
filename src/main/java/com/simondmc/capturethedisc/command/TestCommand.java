package com.simondmc.capturethedisc.command;

import com.simondmc.capturethedisc.command.template.CommandType;
import com.simondmc.capturethedisc.command.template.SuperCommand;
import org.bukkit.entity.Player;

public class TestCommand implements SuperCommand {
    public String getLabel() {
        return "testcmd";
    }

    public CommandType getType() {
        return CommandType.ADMIN_COMMAND;
    }

    public void runCommand(Player p, String[] args) {
        p.sendMessage("§ao/");
    }
}
