package com.simondmc.eventctw.command;

import com.simondmc.eventctw.command.template.CommandType;
import com.simondmc.eventctw.command.template.SuperCommand;
import com.simondmc.eventctw.game.GameCore;
import org.bukkit.entity.Player;

public class StartCommand implements SuperCommand {
    public String getLabel() {return "startctw";}
    public CommandType getType() {return CommandType.ADMIN_COMMAND;}

    public void runCommand(Player p, String[] args) {
        if (GameCore.isOn()) {
            p.sendMessage("§cThe game is already running! Use /forcestopctw to stop it.");
            return;
        }
        GameCore.setup(p);
        GameCore.startGame();

    }
}