/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.buoy;

import com.mahn42.framework.Framework;
import com.mahn42.framework.WorldDBList;
import java.util.HashMap;
import java.util.List;
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
import org.dynmap.markers.CircleMarker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;
import org.dynmap.markers.PolyLineMarker;
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
    public int configTicksDBSave = 100;
    public long configLeverTicks = 20;
    
    protected WorldDBList<WaterPathDB> fWaterPathDBs;
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
                plugin.updateDynMapBuoy();
            }
        }
    }    
    
    @Override
    public void onEnable() {
        readBuoyConfig();
        fWaterPathDBs = new WorldDBList<WaterPathDB>(WaterPathDB.class, this);
        Framework.plugin.registerSaver(fWaterPathDBs);
        fBoatAutomatic = new BoatAutomatic(this);
        fBoatDrivers = new HashMap<Boat, BoatDriver>();
        getCommand("buoy_list").setExecutor(new CommandListBuoys(this));
        getCommand("buoy_remove").setExecutor(new CommandRemoveBuoys(this));
        getCommand("buoy_debug").setExecutor(new CommandDebugBuoys(this));
        getCommand("buoy_dynmap").setExecutor(new CommandUpdateDynmap(this));
        
        getServer().getPluginManager().registerEvents(new ServerListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockListener(this), this);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, fBoatAutomatic, 10, configTicksBoatAutomatic);

        PluginManager pm = getServer().getPluginManager();
        /* Get dynmap */
        fDynmap = pm.getPlugin("dynmap");
        if(fDynmap != null && fDynmap.isEnabled()) {
            updateDynMapBuoy();
        }
    }

    public void updateDynMapBuoy() {
        if (fDynmap != null) {
            DynmapAPI lDynmapAPI = (DynmapAPI)fDynmap; /* Get API */
            MarkerAPI lMarkerAPI = lDynmapAPI.getMarkerAPI();
            MarkerSet lMarkerSet = lMarkerAPI.getMarkerSet("buoy.markerset");
            if (lMarkerSet != null) {
                /*Set<Marker> lMarkers = lMarkerSet.getMarkers();
                for(Marker lMarker : lMarkers) {
                    lMarker.deleteMarker();
                }*/
                lMarkerSet.deleteMarkerSet();
                /*lMarkerSet = lMarkerAPI.createMarkerSet("buoy.markerset", "Buoys", null, false);
                if (lMarkerSet == null) {
                    return;
                }*/
            }// else {
                lMarkerSet = lMarkerAPI.createMarkerSet("buoy.markerset", "Buoys", null, false);
                if (lMarkerSet == null) {
                    return;
                }
            //}
            //getLogger().info("update dynmap markers buoy");
            List<World> lWorlds = getServer().getWorlds();
            for(World lWorld : lWorlds) {
                WaterPathDB lDB = fWaterPathDBs.getDB(lWorld); // getWaterPathDB("world");
                double[] lXs = new double[2];
                double[] lYs = new double[2];
                double[] lZs = new double[2];
                for(WaterPathItem lItem : lDB) {
                    if (lItem.red_links.isEmpty() && lItem.green_links.isEmpty()) {
                        CircleMarker lMarker = lMarkerSet.createCircleMarker(lItem.key, "", true, lWorld.getName(), lItem.mid_position.x, lItem.mid_position.y, lItem.mid_position.z, 3, 3, false);
                        if (lMarker != null)
                            lMarker.setLineStyle(1, 0.75f, 0xFF8080);
                    } else {
                        lXs[0] = lItem.way_red_position.x;
                        lYs[0] = lItem.way_red_position.y;
                        lZs[0] = lItem.way_red_position.z;
                        for(String lKey : lItem.red_links) {
                            WaterPathItem lNextItem = lDB.getRecord(lKey);
                            if (lNextItem != null) {
                                lXs[1] = lNextItem.way_red_position.x;
                                lYs[1] = lNextItem.way_red_position.y;
                                lZs[1] = lNextItem.way_red_position.z;
                                PolyLineMarker lLine = lMarkerSet.createPolyLineMarker(lItem.key+lKey, "", true, lWorld.getName(), lXs, lYs, lZs, false);
                                if (lLine != null)
                                    lLine.setLineStyle(1, 0.75, 0xF04040);
                            }
                        }
                        lXs[0] = lItem.way_green_position.x;
                        lYs[0] = lItem.way_green_position.y;
                        lZs[0] = lItem.way_green_position.z;
                        for(String lKey : lItem.green_links) {
                            WaterPathItem lNextItem = lDB.getRecord(lKey);
                            if (lNextItem != null) {
                                lXs[1] = lNextItem.way_green_position.x;
                                lYs[1] = lNextItem.way_green_position.y;
                                lZs[1] = lNextItem.way_green_position.z;
                                PolyLineMarker lLine = lMarkerSet.createPolyLineMarker(lItem.key+lKey, "", true, lWorld.getName(), lXs, lYs, lZs, false);
                                if (lLine != null)
                                    lLine.setLineStyle(1, 0.75f, 0x40F040);
                            }
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public void onDisable() { 
    }
    
    public WaterPathDB getWaterPathDB(String aWorldName) {
        return fWaterPathDBs.getDB(aWorldName);
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
            //getLogger().info("boat deactivated. " + new Integer(lDriver.getTaskId()).toString());
            getServer().getScheduler().cancelTask(lDriver.getTaskId());
            fBoatDrivers.remove(aBoat);
        }
    }
    
    public void startBuoyDriver(Boat aBoat, WaterPathItem aItem, Vector aBeatVector) {
        BoatDriver lDriver = new BoatDriver(this, aBoat, aItem, aBeatVector);
        int lTaskId = getServer().getScheduler().scheduleAsyncRepeatingTask(this, lDriver, 1, configTicksBoatDriver);
        lDriver.setTaskId(lTaskId);
        //getLogger().info("boat activated. " + new Integer(lDriver.getTaskId()).toString());
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
        configLeverTicks = lConfig.getLong("LeverTicks");
    }
}
