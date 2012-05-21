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
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Boat;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

/**
 *
 * @author andre
 */
public class BuoyMain extends JavaPlugin {

    /*
		colors.put("orange",1);
		colors.put("white",0);
		colors.put("magenta",2);
		colors.put("light_blue",3);
		colors.put("yellow",4);
		colors.put("lime",5);
		colors.put("pink",6);
		colors.put("gray",7);
		colors.put("light_gray",8);
		colors.put("cyan",9);
		colors.put("purple",10);
		colors.put("blue",11);
		colors.put("brown",12);
		colors.put("green",13);
		colors.put("red",14);
		colors.put("black",15);
                */
    public int configAirBeatY = 2;
    public int configMaxDistanceForTravel = 80;
    public int configMaxAngleForTravel = 45;
    public int configMaxDistanceSetDestination = 80;
    public int configMaxAngleSetDestination = 30;
    public int configTicksBoatDriver = 10;
    public int configTicksBoatAutomatic = 10;
    public int configMaxBuoyDistance = 60;
    public byte configRedBouyColor = 14;
    public byte configGreenBouyColor = 13;
    
    protected HashMap<String, WaterPathDB> fWaterPathDBs;
    protected BoatAutomatic fBoatAutomatic;
    protected HashMap<Boat, BoatDriver> fBoatDrivers;
    
     /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        File lFile = new File("/Users/andre/craftbukkit/plugins/Buoy/world_buoy.csv");
        WaterPathDB lDB = new WaterPathDB(null, lFile);
        lDB.load();
        Logger.getLogger("xx").info("size = " + new Integer(lDB.size()));
        WaterPathItem lItem = lDB.getRecord(3);
        lDB.remove(lItem);
        Logger.getLogger("xx").info("size = " + new Integer(lDB.size()));
    }

    @Override
    public void onEnable() {
        readBuoyConfig();
        fBoatAutomatic = new BoatAutomatic(this);
        fBoatDrivers = new HashMap<Boat, BoatDriver>();
        getCommand("buoy_list").setExecutor(new CommandListBuoys(this));
        getCommand("buoy_remove").setExecutor(new CommandRemoveBuoys(this));
        getCommand("buoy_debug").setExecutor(new CommandDebugBuoys(this));
        
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockListener(this), this);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, fBoatAutomatic, 10, configTicksBoatAutomatic);
    }
    
    @Override
    public void onDisable() { 
        if (fWaterPathDBs != null) {
            getLogger().info("Saving DBs...");
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
            File lFolder = getDataFolder();
            //File lFolder = lWorld.getWorldFolder();
            if (!lFolder.exists()) {
                lFolder.mkdirs();
            }
            String lPath = lFolder.getPath();
            lPath = lPath + File.separatorChar + aWorldName + "_buoy.csv";
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
    
    public void startBuoyDriver(Boat aBoat, WaterPathItem aItem, Vector aBeatVector) {
        BoatDriver lDriver = new BoatDriver(this, aBoat, aItem, aBeatVector);
        int lTaskId = getServer().getScheduler().scheduleAsyncRepeatingTask(this, lDriver, 1, configTicksBoatDriver);
        lDriver.setTaskId(lTaskId);
        getLogger().info("boat activated. " + new Integer(lDriver.getTaskId()).toString());
        fBoatDrivers.put(aBoat, lDriver);
    }

    private void readBuoyConfig() {
        FileConfiguration lConfig = getConfig();
        configAirBeatY = lConfig.getInt("AirBeatY");
        configMaxDistanceForTravel = lConfig.getInt("MaxDistanceForTravel");
        configMaxAngleForTravel = lConfig.getInt("MaxAngleForTravel");
        configMaxDistanceSetDestination = lConfig.getInt("MaxDistanceSetDestination");
        configMaxAngleSetDestination = lConfig.getInt("MaxAngleSetDestination");
        configTicksBoatDriver = lConfig.getInt("TicksBoatDriver");
        configTicksBoatAutomatic = lConfig.getInt("TicksBoatAutomatic");
        configMaxBuoyDistance = lConfig.getInt("MaxBuoyDistance");
        configRedBouyColor = (byte) lConfig.getInt("RedBouyColor");
        configGreenBouyColor = (byte) lConfig.getInt("GreenBouyColor");
    }
}
