package com.simondmc.capturethedisc.command;

import com.simondmc.capturethedisc.command.template.CommandType;
import com.simondmc.capturethedisc.command.template.SuperCommand;
import com.simondmc.capturethedisc.game.GameCore;
import org.bukkit.entity.Player;

public class ForceStopCommand implements SuperCommand {
    public String getLabel() {
        return "forcestopctd";
    }

    public CommandType getType() {
        return CommandType.ADMIN_COMMAND;
    }

    public void runCommand(Player p, String[] args) {
        // ignore all checks, force stop in case something goes wrong
        GameCore.stopGame();
    }
}