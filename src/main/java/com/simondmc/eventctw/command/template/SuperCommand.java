package com.simondmc.eventctw.command.template;

import org.bukkit.entity.Player;

public interface SuperCommand {
    String getLabel();
    CommandType getType();
    void runCommand(Player p, String[] args);
}
