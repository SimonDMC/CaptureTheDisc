package com.simondmc.capturethedisc.util;

import com.simondmc.capturethedisc.game.Coins;
import com.simondmc.capturethedisc.region.Region;
import com.simondmc.capturethedisc.shop.ShopGUI;
import com.simondmc.capturethedisc.shop.Upgrade;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Utils {

    // check whether a location is within a region (two-member location array, lower and upper bounds) inclusive
    public static boolean inRegion(Location l, Location[] reg) {
        if (l.getBlock().getX() < reg[0].getBlockX()) return false;
        if (l.getBlock().getY() < reg[0].getBlockY()) return false;
        if (l.getBlock().getZ() < reg[0].getBlockZ()) return false;
        if (l.getBlock().getX() > reg[1].getBlockX()) return false;
        if (l.getBlock().getY() > reg[1].getBlockY()) return false;
        return l.getBlock().getZ() <= reg[1].getBlockZ();
    }

    // location contructor from a location, offset, yaw and pitch
    public static Location genLocation(World world, Location loc, float addX, float addY, float addZ, float yaw, float pitch) {
        return new Location(world, loc.getBlockX() + addX, loc.getBlockY() + addY, loc.getBlockZ() + addZ, yaw, pitch);
    }

    // simpler playsound method
    public static void playSound(Player p, Sound sound) {
        p.playSound(p.getLocation(), sound, 1, 1);
    }

    public static void playSound(Player p, Sound sound, float vol, float pitch) {
        p.playSound(p.getLocation(), sound, vol, pitch);
    }

    // returns the first occurence of an item of a material in a player's inventory, used in shop upgrades
    public static Integer findMatInInventory(Player p, Material mat) {
        ItemStack[] inventory = p.getInventory().getContents();
        // not foreach loop so we can keep index
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] == null) continue;
            if (inventory[i].getType() == mat) return i;
        }
        return null;
    }

    // upgrade utility method for replacing upgrades
    public static void replaceUpgrade(Player p, Upgrade toReplace, Upgrade replaceWith) {
        ShopGUI.upgrades.get(p).remove(toReplace);
        ShopGUI.upgrades.get(p).add(replaceWith);
    }

    // chestplate generation, purchase and equipment in a handy method for code readability sake
    public static void buyChestplate(Player p, Material chestplate, int cost) {
        ItemStack item = new ItemStack(chestplate);
        ItemMeta m = item.getItemMeta();
        m.setUnbreakable(true);
        item.setItemMeta(m);
        p.getInventory().setChestplate(item);
        Utils.playSound(p, Sound.ITEM_ARMOR_EQUIP_CHAIN);
        Coins.addCoins(p, -cost);
    }

    // returns amount of items of a material in a player's inventory, used for block and arrow regen
    public static int countItems(Material mat, Player p) {
        int amount = 0;
        for (ItemStack item : p.getInventory().getContents()) {
            if (item != null && item.getType() == mat) {
                amount += item.getAmount();
            }
        }
        return amount;
    }

    // converts milliseconds to m:ss format
    public static String convertMillisToMSS(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        seconds %= 60;
        return minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
    }

    // https://www.spigotmc.org/threads/free-code-sending-perfectly-centered-chat-message.95872/

    public static void sendCenteredMessage(Player player, String message){
        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for(char c : message.toCharArray()){
            if(c == '§'){
                previousCode = true;
                continue;
            }else if(previousCode == true){
                previousCode = false;
                if(c == 'l' || c == 'L'){
                    isBold = true;
                    continue;
                }else isBold = false;
            }else{
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }

        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = 154 - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while(compensated < toCompensate){
            sb.append(" ");
            compensated += spaceLength;
        }
        // messes up in newer version so compensate manually
        sb.append("   ");
        player.sendMessage(sb + message);
    }

    // fill all blocks in an area
    public static void fillRegion(Location[] reg, Material mat) {
        reg[0].setWorld(Region.getWorld());
        reg[1].setWorld(Region.getWorld());
        for (int x = reg[0].getBlockX(); x <= reg[1].getBlockX(); x++) {
            for (int y = reg[0].getBlockY(); y <= reg[1].getBlockY(); y++) {
                for (int z = reg[0].getBlockZ(); z <= reg[1].getBlockZ(); z++) {
                    reg[0].getWorld().getBlockAt(x, y, z).setType(mat);
                }
            }
        }
    }
}
