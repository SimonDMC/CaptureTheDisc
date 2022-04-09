package com.simondmc.capturethedisc.util;

import com.simondmc.capturethedisc.CaptureTheDisc;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.UUID;

public class Config {
    // add string to string list in config
    public static void addString(String path, String toAdd) {
        List<String> list = CaptureTheDisc.plugin.getConfig().getStringList(path);
        list.add(toAdd);
        CaptureTheDisc.plugin.getConfig().set(path, list);
        CaptureTheDisc.plugin.saveConfig();
    }

    // remove string from string list in config
    public static void removeString(String path, String toRemove) {
        List<String> list = CaptureTheDisc.plugin.getConfig().getStringList(path);
        list.remove(toRemove);
        CaptureTheDisc.plugin.getConfig().set(path, list);
        CaptureTheDisc.plugin.saveConfig();
    }

    // trigger a [DEV INFO]
    public static void devAnnounce(String message) {
        if (CaptureTheDisc.plugin.getConfig().get("devs") != null) {
            for (String uuid : (List<String>) CaptureTheDisc.plugin.getConfig().get("devs")) {
                try {
                    Bukkit.getPlayer(UUID.fromString(uuid)).sendMessage("§d§l[DEV INFO] §r" + message);
                } catch (Exception ignored) {}
            }
        }
    }

    // trigger a [PERFORMANCE]
    public static void performanceInfo(String message) {
        if (CaptureTheDisc.plugin.getConfig().get("performance") != null) {
            for (String uuid : (List<String>) CaptureTheDisc.plugin.getConfig().get("performance")) {
                try {
                    Bukkit.getPlayer(UUID.fromString(uuid)).sendMessage("§7§l[PERFORMANCE] §7" + message);
                } catch (Exception ignored) {}
            }
        }
    }
}
