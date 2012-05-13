/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.buoy;

import org.bukkit.Location;
import org.bukkit.entity.Boat;
import org.bukkit.util.Vector;

/**
 *
 * @author andre
 */
public class BoatDriver implements Runnable {

    protected BuoyMain plugin;
    protected Boat fBoat;
    protected WaterPathItem fDestination;
    protected int fTaskId;
    
    public BoatDriver(BuoyMain aPlugin, Boat aBoat, WaterPathItem aBuoy) {
        plugin = aPlugin;
        fBoat = aBoat;
        fDestination = aBuoy;
    }
    
    @Override
    public void run() {
        Location lBoatLoc = fBoat.getLocation();
        Location lLoc = fDestination.mid_position.getLocation(fBoat.getWorld());
        lLoc.add(0.0f, 1.0f, 0.0f);
        double lDistance = lBoatLoc.distance(lLoc);
        if (lDistance > 1.0f) {
            Vector lVec = new Vector(lLoc.getX() - lBoatLoc.getX(), lLoc.getY() - lBoatLoc.getY(), lLoc.getZ() - lBoatLoc.getZ());
            //double lLength = lVec.length();
            //if (lLength > 1) {
                lVec.multiply((1 / lDistance) / 2.0f);
                plugin.setBoatVelocity(fBoat, lVec);
            //} else {
            //    plugin.deactivateBoatMovement(fBoat);
            //    plugin.getServer().getScheduler().cancelTask(fTaskId);
            //}
        } else {
            //TODO search next buoy if one set as next if none deactivate
            plugin.deactivateBoatMovement(fBoat);
            plugin.getServer().getScheduler().cancelTask(fTaskId);
        }
    }

    public void setTaskId(int aTaskId) {
        fTaskId = aTaskId;
    }
    
}
