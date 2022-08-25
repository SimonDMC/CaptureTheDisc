package com.simondmc.capturethedisc.command;

import com.simondmc.capturethedisc.command.template.CommandType;
import com.simondmc.capturethedisc.command.template.SuperCommand;
import com.simondmc.capturethedisc.listeners.ChatEvent;
import org.bukkit.entity.Player;

public class ToggleShoutCommand implements SuperCommand {
    public String getLabel() {
        return "toggleshout";
    }

    public CommandType getType() {
        return CommandType.PUBLIC_COMMAND;
    }

    public void runCommand(Player p, String[] args) {
        if (ChatEvent.toggledShout.contains(p)) {
            ChatEvent.toggledShout.remove(p);
            p.sendMessage("§eYou are no longer shouting.");
        } else {
            ChatEvent.toggledShout.add(p);
            p.sendMessage("§eYou are now shouting.");
        }
    }
}