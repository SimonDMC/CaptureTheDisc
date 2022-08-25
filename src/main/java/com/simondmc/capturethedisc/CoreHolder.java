package com.simondmc.capturethedisc;

import com.nametbd.core.api.GameManager;
import com.nametbd.core.api.GameRegister;

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
        core.endGame();
    }
}
