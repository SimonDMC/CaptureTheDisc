package com.simondmc.eventctw.command;

import com.simondmc.eventctw.command.template.CommandType;
import com.simondmc.eventctw.command.template.SuperCommand;
import com.simondmc.eventctw.game.Coins;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CoinsCommand implements SuperCommand {
    public String getLabel() {return "addcoins";}
    public CommandType getType() {return CommandType.ADMIN_COMMAND;}

    public void runCommand(Player p, String[] args) {
        if (args.length == 0) return;
        float coins;
        if (args.length == 1) {
            try {
                coins = Float.parseFloat(args[0]);
            } catch (Exception e) {
                return;
            }
            Coins.addCoins(p, coins);
            return;
        }
        if (args.length == 2) {
            Player p2;
            try {
                p2 = Bukkit.getPlayer(args[0]);
                coins = Float.parseFloat(args[1]);
            } catch (Exception e) {
                return;
            }
            Coins.addCoins(p2, coins);
        }
    }
}
