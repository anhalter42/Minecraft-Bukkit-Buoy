/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.buoy;

import java.io.File;
import java.util.HashMap;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Boat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import org.dynmap.DynmapAPI;
import org.dynmap.markers.*;
/**
 *
 * @author andre
 */
public class BuoyMain extends JavaPlugin {

    private static class DBSaver implements Runnable {

        BuoyMain plugin;
        public DBSaver(BuoyMain aPlugin) {
            plugin = aPlugin;
        }

        @Override
        public void run() {
            plugin.saveDB();
        }
    }

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
    public int configTicksDBSave = 100;
    
    protected HashMap<String, WaterPathDB> fWaterPathDBs;
    protected BoatAutomatic fBoatAutomatic;
    protected HashMap<Boat, BoatDriver> fBoatDrivers;
    
    protected Plugin fDynmap;
     /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    }

    private class ServerListener implements Listener {
        private final BuoyMain plugin;
        
        public ServerListener(BuoyMain aPlugin) {
            plugin = aPlugin;
        }
        
        @EventHandler(priority=EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
            Plugin p = event.getPlugin();
            String name = p.getDescription().getName();
            if(name.equals("dynmap")) {
                plugin.activateDynMap();
            }
        }
    }    
    
    @Override
    public void onEnable() {
        readBuoyConfig();
        fBoatAutomatic = new BoatAutomatic(this);
        fBoatDrivers = new HashMap<Boat, BoatDriver>();
        getCommand("buoy_list").setExecutor(new CommandListBuoys(this));
        getCommand("buoy_remove").setExecutor(new CommandRemoveBuoys(this));
        getCommand("buoy_debug").setExecutor(new CommandDebugBuoys(this));
        
        getServer().getPluginManager().registerEvents(new ServerListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockListener(this), this);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, fBoatAutomatic, 10, configTicksBoatAutomatic);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new DBSaver(this), 10, configTicksDBSave);

        PluginManager pm = getServer().getPluginManager();
        /* Get dynmap */
        fDynmap = pm.getPlugin("dynmap");
        if(fDynmap != null && fDynmap.isEnabled()) {
            activateDynMap();
        }
    }

    private void activateDynMap() {
        DynmapAPI lDynmapAPI = (DynmapAPI)fDynmap; /* Get API */
        MarkerAPI lMarkerAPI = lDynmapAPI.getMarkerAPI();
        MarkerSet lMarkerSet = lMarkerAPI.getMarkerSet("Buoy");
        if (lMarkerSet == null) {
            lMarkerSet = lMarkerAPI.createMarkerSet("buoy.markerset", "Buoys", null, false);
        }
        WaterPathDB lDB = getWaterPathDB("world");
        for(WaterPathItem lItem : lDB) {
            CircleMarker lMarker = lMarkerSet.createCircleMarker(lItem.key, null, true, "world", lItem.mid_position.x, lItem.mid_position.y, lItem.mid_position.z, 3, 3, false);
        }
        /*
        double[] x = new double[2]; x[0] = 0.0; x[1] = 30.0;
        double[] y = new double[2]; y[0] = 64.0; y[1] = 64.0;
        double[] z = new double[2]; z[0] = 0.0; z[1] = 60.0;
        PolyLineMarker lMarker = lMarkerSet.createPolyLineMarker("S1", "S2", true, "world", x, y, z, false);
        //lMarker.se
        */
    }
    
    @Override
    public void onDisable() { 
        saveDB();
    }
    
    public void saveDB() { 
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
        configTicksDBSave = lConfig.getInt("TicksDBSave");
    }
}
