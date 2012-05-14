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
    protected HashMap<Boat, BoatDriver> fBoatDrivers;
    
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
        fBoatDrivers = new HashMap<Boat, BoatDriver>();
        getCommand("buoy_list").setExecutor(new CommandListBuoys(this));
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, fBoatAutomatic, 10, 10);
    }
    
    @Override
    public void onDisable() { 
        if (fWaterPathDBs != null) {
            for(WaterPathDB lDB : fWaterPathDBs.values()) {
                lDB.save();
            }
        }
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
    
    public boolean isBoatTraveling(Boat aBoat) {
        return fBoatDrivers.containsKey(aBoat);
    }
    
    public void deactivateBoatMovement(Boat aBoat) {
        fBoatAutomatic.deactivateMovement(aBoat);
        if (fBoatDrivers.containsKey(aBoat)) {
            BoatDriver lDriver = fBoatDrivers.get(aBoat);
            getLogger().info("boat deactivated. " + new Integer(lDriver.getTaskId()).toString());
            getServer().getScheduler().cancelTask(lDriver.getTaskId());
            fBoatDrivers.remove(aBoat);
        }
    }
    
    public void startBuoyDriver(Boat aBoat, WaterPathItem aItem, BoatDriver.Side aSide) {
        BoatDriver lDriver = new BoatDriver(this, aBoat, aItem, aSide);
        int lTaskId = getServer().getScheduler().scheduleAsyncRepeatingTask(this, lDriver, 1, 10);
        lDriver.setTaskId(lTaskId);
        getLogger().info("boat activated. " + new Integer(lDriver.getTaskId()).toString());
        fBoatDrivers.put(aBoat, lDriver);
    }
}
