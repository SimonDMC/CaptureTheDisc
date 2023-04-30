package com.simondmc.capturethedisc.command;

import com.simondmc.capturethedisc.command.template.CommandType;
import com.simondmc.capturethedisc.command.template.SuperCommand;
import com.simondmc.capturethedisc.game.GameCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

public class JoinCommand implements SuperCommand {
    public String getLabel() {
        return "joinctd";
    }

    public CommandType getType() {
        return CommandType.PUBLIC_COMMAND;
    }

    public void runCommand(Player p, String[] args) {
        if (!GameCore.isOn()) {
            p.sendMessage("§cThe game is not on!");
            return;
        }
        if (args.length == 0) {
            GameCore.joinGame(p);
            return;
        }
        if (!p.isOp()) {
            p.sendMessage("§cYou don't have permission to join other players!");
            return;
        }
        Player p2;
        try {
            p2 = Bukkit.getPlayer(args[0]);
        } catch (Exception e) {
            return;
        }
        GameCore.joinGame(p2);
    }
}
