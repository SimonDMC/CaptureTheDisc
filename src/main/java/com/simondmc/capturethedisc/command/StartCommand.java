package com.simondmc.capturethedisc.command;

import com.simondmc.capturethedisc.CaptureTheDisc;
import com.simondmc.capturethedisc.command.template.CommandType;
import com.simondmc.capturethedisc.command.template.SuperCommand;
import com.simondmc.capturethedisc.game.GameCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class StartCommand implements SuperCommand {
    public String getLabel() {
        return "startctd";
    }

    public CommandType getType() {
        return CommandType.ADMIN_COMMAND;
    }

    public void runCommand(Player p, String[] args) {
        if (CaptureTheDisc.coreEnabled) {
            p.sendMessage("§cCore is enabled, please use §e/event setup capture_the_disc §cfollowed by §e/event start capture_the_disc §cto start the game.");
            return;
        }
        if (GameCore.isOn()) {
            CaptureTheDisc.plugin.getLogger().warning("Game is already running, terminating.");
            GameCore.stopGame();
        }
        GameCore.setup();
        GameCore.startGame();

    }
}