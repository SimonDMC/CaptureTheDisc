package com.simondmc.eventctw.game;

import com.simondmc.eventctw.EventCTW;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Config {
    /*

    AS OF RIGHT NOW THE CONFIG IS COMPLETELY UNUSED, FOR REGIONS USE

     */
    private static File file;
    private static FileConfiguration customFile;
    public static void createConfig(String configName) {
        File customConfigFile = new File(EventCTW.plugin.getDataFolder(), configName);
        if (!customConfigFile.exists()) {
            customConfigFile.getParentFile().mkdirs();
            EventCTW.plugin.saveResource(configName, false);
        }
        customFile = YamlConfiguration.loadConfiguration(customConfigFile);
    }
    public static FileConfiguration getFile() {
        return customFile;
    }
    public static void save() {
        try{
            customFile.save(file);
        }catch (IOException e){
            System.out.println("Couldn't save file");
        }
    }
    public static void reload() {
        customFile = YamlConfiguration.loadConfiguration(file);
    }
    public static void set(String path, Object o) {
        customFile.set(path, o);
        save();
    }
    public static Object get(String path) {
        return customFile.get(path);
    }
}
