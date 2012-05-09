/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.buoy;

import java.io.File;
import java.util.HashMap;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author andre
 */
public class BuoyMain extends JavaPlugin {

    protected HashMap<String, WaterPathDB> fWaterPathDBs;
    
     /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    }

    @Override
    public void onEnable() { 
        this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getCommand("listbuoys").setExecutor(new CommandListBuoys(this));
    }
    
    public WaterPathDB getWaterPathDB(String aWorldName) {
        if (fWaterPathDBs == null) {
            fWaterPathDBs = new HashMap<String, WaterPathDB>();
        }
        if (!fWaterPathDBs.containsKey(aWorldName)) {
            World lWorld = getServer().getWorld(aWorldName);
            File lFolder = lWorld.getWorldFolder();
            String lPath = lFolder.getPath();
            lPath = lPath + File.separatorChar + "buoy.csv";
            File lFile = new File(lPath);
            WaterPathDB lDB = new WaterPathDB(lFile);
            lDB.load();
            fWaterPathDBs.put(aWorldName, lDB);
        }
        return fWaterPathDBs.get(aWorldName);
    }
}
