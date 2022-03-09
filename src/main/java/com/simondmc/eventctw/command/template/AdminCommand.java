package com.simondmc.eventctw.command.template;

import com.simondmc.eventctw.EventCTW;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

// player is operator check
public class AdminCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        for (SuperCommand cmd : EventCTW.commands) {
            if (!label.equalsIgnoreCase(cmd.getLabel()) && !label.equalsIgnoreCase("eventctw:" + cmd.getLabel()))
                continue;
            //if (!cmd.getType().equals(CommandType.ADMIN_COMMAND)) continue;
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cNot a player!");
                continue;
            }
            if (!sender.isOp()) {
                sender.sendMessage("§cYou don't have permission to use this command!");
                continue;
            }
            Player player = (Player) sender;
            cmd.runCommand(player, args);
            return true;
        }
        return false;
    }
}
