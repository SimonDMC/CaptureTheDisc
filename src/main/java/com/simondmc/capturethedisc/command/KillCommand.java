package com.simondmc.capturethedisc.command;

import com.simondmc.capturethedisc.command.template.CommandType;
import com.simondmc.capturethedisc.command.template.SuperCommand;
import com.simondmc.capturethedisc.game.GameCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class KillCommand implements SuperCommand {
    public String getLabel() {
        return "ctdkill";
    }

    public CommandType getType() {
        return CommandType.ADMIN_COMMAND;
    }

    public void runCommand(Player p, String[] args) {
        if (args.length == 0) {
            GameCore.die(p);
            return;
        }
        Player p2;
        try {
            p2 = Bukkit.getPlayer(args[0]);
        } catch (Exception e) {
            return;
        }
        GameCore.die(p2);
    }
}
