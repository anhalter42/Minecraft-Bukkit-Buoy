/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.buoy;

import com.mahn42.framework.Framework;
import com.mahn42.framework.PlayerPositionChangedEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Boat;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
//import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.util.Vector;

/**
 *
 * @author andre
 */

public class PlayerListener implements Listener {

    BuoyMain plugin;
    
    public PlayerListener(BuoyMain aPlugin) {
        plugin = aPlugin;
    }
    
    /*
    @EventHandler
    public void playerMove(PlayerMoveEvent aEvent) {
        
    }
    */
    
    @EventHandler
    public void playerVehicle(VehicleExitEvent aEvent) {
        LivingEntity lExited = aEvent.getExited();
        Vehicle lVehicle = aEvent.getVehicle();
        if (lExited instanceof Player && lVehicle instanceof Boat) {
            Player lPlayer = (Player)lExited;
            Boat lBoat = (Boat)lVehicle;
            if (plugin.isBoatTraveling(lBoat)) {
                lBoat.setVelocity(new Vector(0, 0, 0));
                plugin.deactivateBoatMovement(lBoat);
                lPlayer.sendMessage(BuoyMain.plugin.getText(lPlayer, "Travel stopped!"));
            }
        }
    }
    
    @EventHandler
    public void playerPositionChanged(PlayerPositionChangedEvent aEvent) {
        Player lPlayer = aEvent.getPlayer();
        if (lPlayer.isInsideVehicle() && lPlayer.getVehicle() instanceof Boat) {
            Boat lBoat = (Boat)lPlayer.getVehicle();
            WaterPathDB lDB = plugin.getWaterPathDB(lPlayer.getWorld().getName());
            WaterPathItem lBuoy = lDB.getItemForStart(aEvent.getTo());
            if (lBuoy != null) {
                long lWorldTime = aEvent.getPlayer().getWorld().getTime(); 
                long lTimeDiff = lWorldTime - lBuoy.lastDriveTime;
                if (lTimeDiff > 10) {
                    if (lBuoy.red_links.isEmpty() && lBuoy.green_links.isEmpty()) {
                        // kein weg 
                    } else if (!lBuoy.red_links.isEmpty() && lBuoy.green_links.isEmpty()) {
                        // roter weg
                        plugin.startBuoyDriver(lBoat, lBuoy, BoatDriver.Side.Red);
                    } else if (lBuoy.red_links.isEmpty() && !lBuoy.green_links.isEmpty()) {
                        // grüner weg
                        plugin.startBuoyDriver(lBoat, lBuoy, BoatDriver.Side.Green);
                    } else {
                        // weg entspr. blickrichtung
                        List<Block> lLastTwoTargetBlocks = lPlayer.getLastTwoTargetBlocks(null, 100);
                        Vector lV = lLastTwoTargetBlocks.get(0).getLocation().toVector().subtract(lPlayer.getLocation().toVector());
                        plugin.startBuoyDriver(lBoat, lBuoy, lV);
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void playerInteract(PlayerInteractEvent event) {
        Player lPlayer = event.getPlayer();
        World lWorld = lPlayer.getWorld();
        Material lInHand = null;
        if (event.hasItem()) {
            lInHand = event.getItem().getType();
        }
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (event.hasItem() && Framework.isSpade(lInHand)) {
                Block lBlock = event.getClickedBlock();
                if (lBlock != null) {
                    if (lBlock.getType().equals(Material.WOOL)) {
                        int lColor = lBlock.getData();
                        Location lLoc = lBlock.getLocation();
                        if ((lColor == plugin.configRedBouyColor) || (lColor == plugin.configGreenBouyColor)) { // red or green
                            if (!lPlayer.isSneaking()) {
                                Material lMat = lBlock.getWorld().getBlockAt(lLoc.getBlockX(), lLoc.getBlockY() - 1, lLoc.getBlockZ()).getType();
                                if (lMat.equals(Material.STATIONARY_WATER) || lMat.equals(Material.WATER)) {
                                    //
                                    // insert new buoy
                                    //
                                    BuoyFinder lTask = new BuoyFinder(plugin, lBlock, lPlayer);
                                    plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, lTask);
                                }
                            } else {
                                //
                                // clear destinations
                                //
                                WaterPathDB lDB = plugin.getWaterPathDB(lWorld.getName());
                                WaterPathItem lBuoy = lDB.getItemAt(lLoc);
                                if (lBuoy != null) {
                                    if (lColor == plugin.configRedBouyColor) { // red
                                        lPlayer.sendMessage(BuoyMain.plugin.getText(lPlayer, "Clearing red destinations!", lBuoy.red_links.size()));
                                        lBuoy.red_links.clear();
                                    } else { // green
                                        lPlayer.sendMessage(BuoyMain.plugin.getText(lPlayer, "Clearing green destinations!", lBuoy.green_links.size()));
                                        lBuoy.green_links.clear();
                                    }
                                    plugin.updateDynMapBuoy();
                                } else {
                                    lPlayer.sendMessage(BuoyMain.plugin.getText(lPlayer, "No buoy for clearing destinations!"));
                                }
                            }
                        }
                    }
                } else {
                    // no block
                }
            }
        } else if (event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            if (event.hasItem() && Framework.isSpade(lInHand)) {
                if (lPlayer.getVehicle() != null && lPlayer.getVehicle() instanceof Boat) {
                    //
                    // travel
                    //
                    Boat lBoat = (Boat)lPlayer.getVehicle();
                    WaterPathDB lDB = plugin.getWaterPathDB(lWorld.getName());
                    Location lLocation = lPlayer.getLocation();
                    Block lBlock = lPlayer.getTargetBlock(null, 20);
                    if ((lBlock != null) && (lBlock.getY() > (lPlayer.getLocation().getBlockY() + plugin.configAirBeatY))) { // schlag in die luft
                        if (plugin.isBoatTraveling(lBoat)) {
                            plugin.deactivateBoatMovement(lBoat);
                            lPlayer.sendMessage(BuoyMain.plugin.getText(lPlayer, "Travel stopped!"));
                        } else {
                            Vector lVector = new Vector(lBlock.getX() - lLocation.getBlockX(), lBlock.getY() - lLocation.getBlockY(), lBlock.getZ() - lLocation.getBlockZ());
                            ArrayList<WaterPathItem> lBuoys = lDB.getItemNearlyDirection(lLocation, plugin.configMaxDistanceForTravel, lVector, 0.0f, (float) ((plugin.configMaxAngleForTravel * Math.PI) / 180.0f));
                            if (lBuoys.size() > 0) {
                                for(WaterPathItem lItem : lBuoys) {
                                    plugin.startBuoyDriver(lBoat, lItem, lVector);
                                    break;
                                }
                            } else {
                                lPlayer.sendMessage(BuoyMain.plugin.getText(lPlayer, "No buoy found in this direction!"));
                            }
                        }
                    }
                } else {
                    //
                    // set destination
                    //
                    Location lLoc = lPlayer.getLocation().add(0, -1, 0);
                    Block lBlock = lLoc.getBlock(); // block on which we stand
                    if (lBlock != null) {
                        if (lBlock.getType().equals(Material.WOOL)) {
                            int lColor = lBlock.getData();
                            if ((lColor == plugin.configRedBouyColor) || (lColor == plugin.configGreenBouyColor)) { // red or green
                                Block lTargetBlock = lPlayer.getTargetBlock(null, 20);
                                if ((lTargetBlock != null) && (lTargetBlock.getY() > (lPlayer.getLocation().getBlockY() + plugin.configAirBeatY))) { // schlag in die luft
                                    WaterPathDB lDB = plugin.getWaterPathDB(lWorld.getName());
                                    WaterPathItem lBuoy = lDB.getItemAt(lLoc);
                                    if (lBuoy != null) {
                                        Vector lVector = new Vector(lTargetBlock.getX() - lLoc.getBlockX(), lTargetBlock.getY() - lLoc.getBlockY(), lTargetBlock.getZ() - lLoc.getBlockZ());
                                        ArrayList<WaterPathItem> lBuoys = lDB.getItemNearlyDirection(lLoc, plugin.configMaxDistanceSetDestination, lVector, 0.0f, (float) ((plugin.configMaxAngleSetDestination * Math.PI) / 180.0f) );
                                        if (lBuoys.size() > 0) {
                                            boolean lFound = false;
                                            for(WaterPathItem lItem : lBuoys) {
                                                if (lColor == plugin.configRedBouyColor) { // red
                                                    if (!lBuoy.red_links.contains(lItem.key)) {
                                                        lBuoy.red_links.add(lItem.key);
                                                        lPlayer.sendMessage(BuoyMain.plugin.getText(lPlayer, "Next buoy marked as destination."));
                                                        lFound = true;
                                                        break;
                                                    }
                                                } else { // green
                                                    if (!lBuoy.green_links.contains(lItem.key)) {
                                                        lBuoy.green_links.add(lItem.key);
                                                        lPlayer.sendMessage(BuoyMain.plugin.getText(lPlayer, "Next buoy marked as destination."));
                                                        lFound = true;
                                                        break;
                                                    }
                                                }
                                            }
                                            if (lFound) {
                                                plugin.updateDynMapBuoy();
                                            } else {
                                                lPlayer.sendMessage(BuoyMain.plugin.getText(lPlayer, "No buoy found in this direction!"));
                                            }
                                        } else {
                                            lPlayer.sendMessage(BuoyMain.plugin.getText(lPlayer, "No buoy found in this direction!"));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
