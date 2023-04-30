package com.simondmc.capturethedisc.command;

import com.simondmc.capturethedisc.command.template.CommandType;
import com.simondmc.capturethedisc.command.template.SuperCommand;
import com.simondmc.capturethedisc.game.GameCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

public class StuckCommand implements SuperCommand {
    public String getLabel() {
        return "ctdstuck";
    }

    public CommandType getType() {
        return CommandType.PUBLIC_COMMAND;
    }

    public void runCommand(Player p, String[] args) {
        if (GameCore.lastDamage.containsKey(p) && System.currentTimeMillis() - GameCore.lastDamage.get(p).timestamp < 10000) {
            p.sendMessage("§cYou can't use this command while in combat!");
            return;
        }
        GameCore.die(p, EntityDamageEvent.DamageCause.SUICIDE);
    }
}
