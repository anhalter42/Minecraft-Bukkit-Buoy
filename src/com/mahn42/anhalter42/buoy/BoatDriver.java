/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.buoy;

import org.bukkit.Location;
import org.bukkit.Material;
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
        World lWorld = fBoat.getWorld();
        if (fStart) {
            boolean lRedEmpty = fDestination.red_links.isEmpty();
            boolean lGreenEmpty = fDestination.green_links.isEmpty();
            Vector lPos = fBoat.getLocation().toVector();
            Vector lRedVector = fDestination.red_position.getVector().subtract(lPos);
            Vector lGreenVector = fDestination.green_position.getVector().subtract(lPos);
            if ((lRedVector.angle(fStartVector) < lGreenVector.angle(fStartVector) && !lRedEmpty) || lGreenEmpty) {
                fSide = Side.Red;
                sendPlayer("Lets travel the red way.");
            } else {
                fSide = Side.Green;
                sendPlayer("Lets travel the green way.");
            }
            fStart = false;
        }
        Location lLoc;
        double fSpeedFactor = 1.0f;
        if (fSide == Side.Red) {
            lLoc = fDestination.way_red_position.getLocation(lWorld);
            if (fDestination.red_position.getLocation(lWorld).add(0,1,0).getBlock().getType().equals(Material.TORCH)) {
                fSpeedFactor = 1.5f;
            }
        } else {
            lLoc = fDestination.way_green_position.getLocation(lWorld);
            if (fDestination.green_position.getLocation(lWorld).add(0,1,0).getBlock().getType().equals(Material.TORCH)) {
                fSpeedFactor = 1.5f;
            }
        }
        lLoc.add(0.0f, 1.0f, 0.0f);
        double lDistance = lBoatLoc.distance(lLoc);
        if (lDistance > 1.0f) {
            Vector lVec = new Vector(lLoc.getX() - lBoatLoc.getX(), lLoc.getY() - lBoatLoc.getY(), lLoc.getZ() - lBoatLoc.getZ());
            double lFactor = (1 / lDistance) / 4.0f;
            if (lDistance < 4.0f) {
                lFactor = lFactor / (5.0f - lDistance);
            }
            lVec.multiply(lFactor * fSpeedFactor);
            plugin.setBoatVelocity(fBoat, lVec);
        } else {
            if (fLastItem != null && fSide == Side.Red &&
                    ((fDestination.red_links.isEmpty())
                    || (fDestination.red_links.size() == 1 && fDestination.red_links.contains(fLastItem.key)) )) {
                sendPlayer("No more destinations.");
                deactivate();
            } else if (fLastItem != null && fSide == Side.Green &&
                    ((fDestination.green_links.isEmpty())
                    || (fDestination.green_links.size() == 1 && fDestination.green_links.contains(fLastItem.key)) )) {
                sendPlayer("No more destinations.");
                deactivate();
            } else {
                WaterPathItem lNextDestination = null;
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
                            for(String lKey : fDestination.green_links) {
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
                    plugin.getLogger().info(fDestination.toString());
                    if (fDestination.swapRedToGreen && fSide == Side.Red) {
                        sendPlayer("Now lets travel the green way.");
                        plugin.getLogger().info("swap to green");
                        fSide = Side.Green;
                    } else if (fDestination.swapGreenToRed && fSide == Side.Green) {
                        sendPlayer("Now lets travel the red way.");
                        plugin.getLogger().info("swap to red");
                        fSide = Side.Red;
                    }
                } else {
                    sendPlayer("No next destination.");
                    deactivate(); // to many destinations
                }
            }
        }
    }
    
    protected void deactivate() {
        plugin.deactivateBoatMovement(fBoat);
        //plugin.getLogger().info("boat stopped.");
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
