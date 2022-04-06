package com.simondmc.capturethedisc.game;

import org.bukkit.entity.Player;

public class TimestampHit {
    public long timestamp;
    public Player damager;

    public TimestampHit(long timestamp, Player damager) {
        this.timestamp = timestamp;
        this.damager = damager;
    }
}
