package com.simondmc.capturethedisc;

import com.nametbd.core.api.GameManager;
import com.nametbd.core.api.GameRegister;
import org.bukkit.Bukkit;

public class CoreHolder {
    static GameManager core;

    static void registerCore() {
        core = new CoreManager();
        GameRegister.getInstance().registerGameManager(core);
    }

    static void unregisterCore() {
        GameRegister.getInstance().unRegisterGameManager(core);
    }

    public static void endGame() {
        Bukkit.getScheduler().runTaskLater(CaptureTheDisc.plugin, () -> {
            CaptureTheDisc.plugin.getLogger().info("Received end game request, stopping game...");
            core.endGame();
            }, 100);
    }
}
