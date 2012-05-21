/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.buoy;

import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
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
    
    @EventHandler
    public void playerInteract(PlayerInteractEvent event) {
        Player lPlayer = event.getPlayer();
        World lWorld = lPlayer.getWorld();
        Material lInHand = event.getItem().getType();
        //TODO only with special item in hand
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (event.hasItem() && (
                       lInHand.equals(Material.STONE_SPADE)
                    || lInHand.equals(Material.IRON_SPADE)
                    || lInHand.equals(Material.DIAMOND_SPADE)
                    || lInHand.equals(Material.WOOD_SPADE))) {
                Block lBlock = event.getClickedBlock();
                if (lBlock != null) {
                    if (lBlock.getType().equals(Material.WOOL)) {
                        int lColor = lBlock.getData();
                        Location lLoc = lBlock.getLocation();
                        if ((lColor == plugin.configRedBouyColor) || (lColor == plugin.configGreenBouyColor)) { // red or green
                            if (!lPlayer.isSneaking()) {
                                int lTypeId = lBlock.getWorld().getBlockTypeIdAt(lLoc.getBlockX(), lLoc.getBlockY() - 1, lLoc.getBlockZ());
                                if ((lTypeId == 9) || (lTypeId == 8)) {
                                    //
                                    // insert new buoy
                                    //
                                    BuoyFinder lTask = new BuoyFinder(plugin, lBlock, event.getPlayer());
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
                                        lBuoy.red_links.clear();
                                        lPlayer.sendMessage("Clearing red destinations!");
                                    } else { // green
                                        lBuoy.green_links.clear();
                                        lPlayer.sendMessage("Clearing green destinations!");
                                    }
                                } else {
                                    lPlayer.sendMessage("No buoy for clearing destinations!");
                                }
                            }
                        }
                    }
                } else {
                    // no block
                }
            }
        } else if (event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            if (event.hasItem() && (
                       lInHand.equals(Material.STONE_SPADE)
                    || lInHand.equals(Material.IRON_SPADE)
                    || lInHand.equals(Material.DIAMOND_SPADE)
                    || lInHand.equals(Material.WOOD_SPADE))) {
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
                            lPlayer.sendMessage("Travel stopped!");
                        } else {
                            Vector lVector = new Vector(lBlock.getX() - lLocation.getBlockX(), lBlock.getY() - lLocation.getBlockY(), lBlock.getZ() - lLocation.getBlockZ());
                            ArrayList<WaterPathItem> lBuoys = lDB.getItemNearlyDirection(lLocation, plugin.configMaxDistanceForTravel, lVector, 0.0f, (float) ((plugin.configMaxAngleForTravel * Math.PI) / 180.0f));
                            if (lBuoys.size() > 0) {
                                for(WaterPathItem lItem : lBuoys) {
                                    plugin.startBuoyDriver(lBoat, lItem, lVector);
                                    break;
                                }
                            } else {
                                lPlayer.sendMessage("No buoy found in this direction!");
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
                                            for(WaterPathItem lItem : lBuoys) {
                                                lPlayer.sendMessage("Next buoy marked as destination.");
                                                if (lColor == plugin.configRedBouyColor) { // red
                                                    if (!lBuoy.red_links.contains(lItem.key)) lBuoy.red_links.add(lItem.key);
                                                } else { // green
                                                    if (!lBuoy.green_links.contains(lItem.key)) lBuoy.green_links.add(lItem.key);
                                                }
                                                break;
                                            }
                                        } else {
                                            lPlayer.sendMessage("No buoy found in this direction!");
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
    
        /*
    @EventHandler
    public void playerMove(PlayerMoveEvent aEvent) {
        Player lPlayer = aEvent.getPlayer();
        Vector lVel = lPlayer.getVelocity();
        if (lVel.length() > 0.5f) {
            lPlayer.sendMessage("Vel:" + lVel.toBlockVector().toString() + " l:" + lVel.length());
        }
    }
        */
}
