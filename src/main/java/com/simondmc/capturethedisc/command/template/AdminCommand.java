package com.simondmc.capturethedisc.command.template;

import com.simondmc.capturethedisc.CaptureTheDisc;
import com.simondmc.capturethedisc.util.Config;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

// player is operator check
public class AdminCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        for (SuperCommand cmd : CaptureTheDisc.commands) {
            if (!label.equalsIgnoreCase(cmd.getLabel()) && !label.equalsIgnoreCase(CaptureTheDisc.plugin.getName() + ":" + cmd.getLabel()))
                continue;
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cNot a player!");
                continue;
            }
            if (!sender.isOp()) {
                sender.sendMessage("§cYou don't have permission to use this command!");
                continue;
            }

            Player player = (Player) sender;
            try {
                cmd.runCommand(player, args);
            } catch (Exception e) {
                player.sendMessage("§cAn error occurred while running this command! Please report this to SimonDMC#6662.");
                e.printStackTrace();
            }

            // dev announcement
            Config.devAnnounce("§a" + player.getName() + " §eran the command §a/" + label + " " + String.join(" ", args));

            return true;
        }
        return false;
    }
}
