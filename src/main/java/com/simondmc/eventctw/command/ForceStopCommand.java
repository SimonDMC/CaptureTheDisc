package com.simondmc.eventctw.command;

import com.simondmc.eventctw.command.template.CommandType;
import com.simondmc.eventctw.command.template.SuperCommand;
import com.simondmc.eventctw.game.GameCore;
import org.bukkit.entity.Player;

public class ForceStopCommand implements SuperCommand {
    public String getLabel() {return "forcestopctw";}
    public CommandType getType() {return CommandType.ADMIN_COMMAND;}

    public void runCommand(Player p, String[] args) {
        // ignore all checks, force stop in case something goes wrong
        GameCore.stopGame();
    }
}