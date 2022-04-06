package com.simondmc.capturethedisc.command;

import com.simondmc.capturethedisc.command.template.CommandType;
import com.simondmc.capturethedisc.command.template.SuperCommand;
import com.simondmc.capturethedisc.game.GameCore;
import org.bukkit.entity.Player;

public class StartCommand implements SuperCommand {
    public String getLabel() {
        return "startctd";
    }

    public CommandType getType() {
        return CommandType.ADMIN_COMMAND;
    }

    public void runCommand(Player p, String[] args) {
        if (GameCore.isOn()) GameCore.stopGame();
        GameCore.setup(p);
        GameCore.startGame();

    }
}