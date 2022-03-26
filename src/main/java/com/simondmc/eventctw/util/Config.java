package com.simondmc.eventctw.util;

import com.simondmc.eventctw.EventCTW;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.UUID;

public class Config {
    // add string to string list in config
    public static void addString(String path, String toAdd) {
        List<String> list = EventCTW.plugin.getConfig().getStringList(path);
        list.add(toAdd);
        EventCTW.plugin.getConfig().set(path, list);
        EventCTW.plugin.saveConfig();
    }

    // remove string from string list in config
    public static void removeString(String path, String toRemove) {
        List<String> list = EventCTW.plugin.getConfig().getStringList(path);
        list.remove(toRemove);
        EventCTW.plugin.getConfig().set(path, list);
        EventCTW.plugin.saveConfig();
    }

    // trigger a [DEV INFO]
    public static void devAnnounce(String message) {
        if (EventCTW.plugin.getConfig().get("devs") != null) {
            for (String uuid : (List<String>) EventCTW.plugin.getConfig().get("devs")) {
                try {
                    Bukkit.getPlayer(UUID.fromString(uuid)).sendMessage("§d[DEV INFO] " + message);
                } catch (Exception ignored) {}
            }
        }
    }
}
