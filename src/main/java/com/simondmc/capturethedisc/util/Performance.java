package com.simondmc.capturethedisc.util;

import java.text.DecimalFormat;
import java.util.Map;

public class Performance {

    private static Map<String, Long> times = new java.util.HashMap<>();
    private static final DecimalFormat df = new DecimalFormat("0.00");

    public static void start(String name) {
        times.put(name, System.nanoTime());
    }

    public static void stop(String name) {
        long start = times.get(name);
        long end = System.nanoTime();
        float ms = (end - start) / 1000000f;
        String color = ms > 0.2 ? "§c" : "§7";
        Config.performanceInfo(color + name + ": " + df.format(ms) + "ms");
    }
}

