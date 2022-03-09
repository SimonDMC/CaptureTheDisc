package com.simondmc.eventctw.map;

import com.simondmc.eventctw.EventCTW;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.Location;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Schematic {
    public static void loadSchematic(String name, Location l, Boolean ignoreAir) throws IOException, WorldEditException {
        File file = new File(EventCTW.plugin.getDataFolder() + "/" + name);
        System.out.println(file.getName());
        ClipboardFormat format = ClipboardFormats.findByFile(file);
        ClipboardReader reader = null;
        reader = format.getReader(new FileInputStream(file));
        Clipboard clipboard;
        clipboard = reader.read();
        com.sk89q.worldedit.world.World adaptedWorld = BukkitAdapter.adapt(l.getWorld());
        EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(adaptedWorld, -1);
        Operation operation = new ClipboardHolder(clipboard).createPaste(editSession)
                .to(BlockVector3.at(l.getBlockX(), l.getBlockY(), l.getBlockZ()))
                .ignoreAirBlocks(ignoreAir)
                .build();
        Operations.complete(operation);
        editSession.flushSession();
    }
}
