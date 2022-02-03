package com.simondmc.eventctw.command;

import com.simondmc.eventctw.command.template.CommandType;
import com.simondmc.eventctw.command.template.SuperCommand;
import org.bukkit.entity.Player;

public class TestCommand implements SuperCommand {
    public String getLabel() {return "testcmd";}
    public CommandType getType() {return CommandType.ADMIN_COMMAND;}

    public void runCommand(Player p, String[] args) {
        p.sendMessage("§ao/");
    }
}
