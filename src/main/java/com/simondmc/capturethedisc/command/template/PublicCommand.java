package com.simondmc.capturethedisc.command.template;

import com.simondmc.capturethedisc.CaptureTheDisc;
import com.simondmc.capturethedisc.util.Config;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

// no op check
public class PublicCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        for (SuperCommand cmd : CaptureTheDisc.commands) {
            if (!label.equalsIgnoreCase(cmd.getLabel()) && !label.equalsIgnoreCase(CaptureTheDisc.plugin.getName() + ":" + cmd.getLabel()))
                continue;
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cNot a player!");
                continue;
            }

            Player player = (Player) sender;
            cmd.runCommand(player, args);

            return true;
        }
        return false;
    }
}
