/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.buoy;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 *
 * @author andre
 */
public class BoatDriver implements Runnable {

    public enum Side {
        Red,
        Green
    }
    protected BuoyMain plugin;
    protected Boat fBoat;
    protected WaterPathItem fDestination;
    protected int fTaskId;
    protected Side fSide;
    protected WaterPathItem fLastItem = null;
    protected Vector fStartVector = null;
    protected boolean fStart;
    
    public BoatDriver(BuoyMain aPlugin, Boat aBoat, WaterPathItem aBuoy, Vector aBeatVector) {
        plugin = aPlugin;
        fBoat = aBoat;
        fDestination = aBuoy;
        fStartVector = aBeatVector;
        fStart = true;
    }
    
    @Override
    public void run() {
        Location lBoatLoc = fBoat.getLocation();
        if (fStart) {
            //TODO decide the side
            Vector lPos = fBoat.getLocation().toVector();
            Vector lRedVector = fDestination.red_position.getVector().subtract(lPos);
            Vector lGreenVector = fDestination.green_position.getVector().subtract(lPos);
            if (lRedVector.angle(fStartVector) < lGreenVector.angle(fStartVector)) {
                fSide = Side.Red;
            } else {
                fSide = Side.Green;
            }
            fStart = false;
        }
        Location lLoc;
        if (fSide == Side.Red) {
            lLoc = fDestination.way_red_position.getLocation(fBoat.getWorld());
        } else {
            lLoc = fDestination.way_green_position.getLocation(fBoat.getWorld());
        }
        lLoc.add(0.0f, 1.0f, 0.0f);
        double lDistance = lBoatLoc.distance(lLoc);
        if (lDistance > 1.0f) {
            Vector lVec = new Vector(lLoc.getX() - lBoatLoc.getX(), lLoc.getY() - lBoatLoc.getY(), lLoc.getZ() - lBoatLoc.getZ());
            double lFactor = (1 / lDistance) / 4.0f;
            if (lDistance < 4.0f) {
                lFactor = lFactor / (5.0f - lDistance);
            }
            lVec.multiply(lFactor);
            plugin.setBoatVelocity(fBoat, lVec);
        } else {
            //TODO search next buoy if one set as next if none deactivate
            if (fLastItem != null && fSide == Side.Red && fDestination.red_links.size() <= 1) {
                sendPlayer("No more destinations.");
                deactivate();
            } else if (fLastItem != null && fSide == Side.Green && fDestination.green_links.size() <= 1) {
                sendPlayer("No more destinations.");
                deactivate();
            } else {
                WaterPathItem lNextDestination = null;
                World lWorld = fBoat.getWorld();
                WaterPathDB lDB = plugin.getWaterPathDB(lWorld.getName());
                if (fLastItem != null) {
                    if (fSide == Side.Red) {
                        if (fDestination.red_links.size() > 2) {
                            sendPlayer("To many destinations.");
                            deactivate(); // to many destinations
                        } else {
                            for(String lKey : fDestination.red_links) {
                                if (!lKey.equals(fLastItem.key)) {
                                    lNextDestination = lDB.getRecord(lKey);
                                    break;
                                }
                            }
                        }
                    } else {
                        if (fDestination.green_links.size() > 2) {
                            sendPlayer("To many destinations.");
                            deactivate(); // to many destinations
                        } else {
                            for(String lKey : fDestination.red_links) {
                                if (!lKey.equals(fLastItem.key)) {
                                    lNextDestination = lDB.getRecord(lKey);
                                    break;
                                }
                            }
                        }
                    }
                } else {
                    // fStartVector holds the direction
                    if (fSide == Side.Red) {
                        WaterPathItem lNewItem = null;
                        double lMinAngle = Double.MAX_VALUE;
                        for(String lKey : fDestination.red_links) {
                            WaterPathItem lItem = lDB.getRecord(lKey);
                            if (lItem != null) {
                                Vector lVector = lItem.red_position.getVector().subtract(fDestination.red_position.getVector());
                                double lAngle = lVector.angle(fStartVector);
                                if (lAngle < lMinAngle) {
                                    lMinAngle = lAngle;
                                    lNewItem = lItem;
                                }
                            }
                        }
                        lNextDestination = lNewItem;
                    } else {
                        WaterPathItem lNewItem = null;
                        double lMinAngle = Double.MAX_VALUE;
                        for(String lKey : fDestination.green_links) {
                            WaterPathItem lItem = lDB.getRecord(lKey);
                            if (lItem != null) {
                                Vector lVector = lItem.green_position.getVector().subtract(fDestination.green_position.getVector());
                                double lAngle = lVector.angle(fStartVector);
                                if (lAngle < lMinAngle) {
                                    lMinAngle = lAngle;
                                    lNewItem = lItem;
                                }
                            }
                        }
                        lNextDestination = lNewItem;
                    }
                }
                if (lNextDestination != null) {
                    fLastItem = fDestination;
                    fDestination = lNextDestination;
                }
            }
        }
    }
    
    protected void deactivate() {
        plugin.deactivateBoatMovement(fBoat);
        //plugin.getServer().getScheduler().cancelTask(fTaskId);
        plugin.getLogger().info("boat stopped.");
    }

    public void setTaskId(int aTaskId) {
        fTaskId = aTaskId;
    }
    
    public int getTaskId() {
        return fTaskId;
    }
    
    public void sendPlayer(String aText) {
        Entity lPassenger = fBoat.getPassenger();
        if (lPassenger != null && lPassenger instanceof Player) {
            Player lPlayer = (Player)lPassenger;
            lPlayer.sendMessage(aText);
        }
    }
}
