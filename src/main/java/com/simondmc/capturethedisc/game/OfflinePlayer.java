package com.simondmc.capturethedisc.game;

import com.simondmc.capturethedisc.kits.Kit;
import com.simondmc.capturethedisc.shop.Upgrade;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OfflinePlayer {
    private final UUID uuid;
    private final Kit kit;
    private final boolean isRed;
    private final float coins;
    private final int kills;
    private final List<Upgrade> upgrades;

    public OfflinePlayer(UUID uuid, Kit kit, boolean isRed, List<Upgrade> upgrades, float coins, int kills) {
        this.uuid = uuid;
        this.kit = kit;
        this.isRed = isRed;
        this.upgrades = upgrades;
        this.coins = coins;
        this.kills = kills;
    }

    public UUID getUUID() {
        return uuid;
    }

    public Kit getKit() {
        return kit;
    }

    public boolean isRed() {
        return isRed;
    }

    public List<Upgrade> getUpgrades() {
        return upgrades;
    }

    public float getCoins() {
        return coins;
    }

    public int getKills() {
        return kills;
    }
}
