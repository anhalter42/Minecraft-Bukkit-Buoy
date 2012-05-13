/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.buoy;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;
import org.bukkit.World;
import org.bukkit.entity.Boat;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

/**
 *
 * @author andre
 */
public class BuoyMain extends JavaPlugin {

    protected HashMap<String, WaterPathDB> fWaterPathDBs;
    protected BoatAutomatic fBoatAutomatic;
    
     /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ArrayList<String> lKeys = new ArrayList<String>();
        lKeys.add("1");
        if (!lKeys.contains("1")) lKeys.add("2");
        String lStr = DBRecord.arrayToKeys(lKeys);
        Logger.getLogger("keys").info(lStr);
        
        //for(BlockPosition lPos : new WorldLineWalk(new BlockPosition(0, 0, 0), new BlockPosition(10, 20, 30))) {
        //    Logger.getLogger("xyz").info(lPos.toString());
        //}
    }

    @Override
    public void onEnable() { 
        fBoatAutomatic = new BoatAutomatic(this);
        getCommand("buoy_list").setExecutor(new CommandListBuoys(this));
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, fBoatAutomatic, 10, 10);
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
            WaterPathDB lDB = new WaterPathDB(lWorld, lFile);
            lDB.load();
            getLogger().info("Datafile " + lFile.toString() + " loaded. (Records:" + new Integer(lDB.size()).toString() + ")");
            fWaterPathDBs.put(aWorldName, lDB);
        }
        return fWaterPathDBs.get(aWorldName);
    }
    
    public void setBoatVelocity(Boat aBoat, Vector aVelocity) {
        fBoatAutomatic.setMovement(aBoat, aVelocity);
    }
    
    public void deactivateBoatMovement(Boat aBoat) {
        fBoatAutomatic.deactivateMovement(aBoat);
    }
}
