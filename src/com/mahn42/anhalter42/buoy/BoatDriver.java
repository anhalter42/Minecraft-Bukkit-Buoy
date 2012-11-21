/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.buoy;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
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
    
    protected boolean fInRun = false;
    
    @Override
    public void run() {
        if (!fInRun) {
            fInRun = true;
            try {
                Location lBoatLoc = fBoat.getLocation();
                World lWorld = fBoat.getWorld();
                if (fStart) {
                    boolean lRedEmpty = fDestination.red_links.isEmpty();
                    boolean lGreenEmpty = fDestination.green_links.isEmpty();
                    Vector lPos = fBoat.getLocation().toVector();
                    Vector lRedVector = fDestination.red_position.getVector().subtract(lPos);
                    Vector lGreenVector = fDestination.green_position.getVector().subtract(lPos);
                    int lRedSize = ((lRedVector.getBlockX() < 0 && fStartVector.getBlockX() < 0) ? 1 : 0)
                            + ((lRedVector.getBlockZ() < 0 && fStartVector.getBlockZ() < 0) ? 1 : 0)
                            + ((lRedVector.getBlockX() >= 0 && fStartVector.getBlockX() >= 0) ? 1 : 0)
                            + ((lRedVector.getBlockZ() >= 0 && fStartVector.getBlockZ() >= 0) ? 1 : 0);
                    int lGreenSize = ((lGreenVector.getBlockX() < 0 && fStartVector.getBlockX() < 0) ? 1 : 0)
                            + ((lGreenVector.getBlockZ() < 0 && fStartVector.getBlockZ() < 0) ? 1 : 0)
                            + ((lGreenVector.getBlockX() >= 0 && fStartVector.getBlockX() >= 0) ? 1 : 0)
                            + ((lGreenVector.getBlockZ() >= 0 && fStartVector.getBlockZ() >= 0) ? 1 : 0);
                    if (lRedVector.angle(fStartVector) < lGreenVector.angle(fStartVector) && !lRedEmpty && lRedSize >= 2) {
                        fSide = Side.Red;
                        sendPlayer("Let's travel the red way.");
                    } else if (lGreenVector.angle(fStartVector) < lRedVector.angle(fStartVector) && !lGreenEmpty && lGreenSize >= 2) {
                        fSide = Side.Green;
                        sendPlayer("Let's travel the green way.");
                    } else if (!lRedEmpty) {
                        fSide = Side.Red;
                        sendPlayer("I think we should travel the red way.");
                    } else if (!lGreenEmpty) {
                        fSide = Side.Green;
                        sendPlayer("I think we should travel the green way.");
                    } else {
                        fSide = Side.Red;
                        sendPlayer("There is no way. Driving to the red buoy.");
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
                    if (fLastItem != null) {
                        triggerLevers(fLastItem);
                    }
                    if (fDestination != null) {
                        triggerLevers(fDestination);
                    }
                    WaterPathItem lNextDestination = getNextDestination(true);
                    if (lNextDestination != null) {
                        fLastItem = fDestination;
                        fDestination = lNextDestination;
                        if (lNextDestination != null) {
                            if (fSide == Side.Red) {
                                if (lNextDestination.red_links.contains(fLastItem.key)
                                        //&& fLastItem.green_links.isEmpty()
                                        //&& lNextDestination.green_links.isEmpty()
                                        ) {
                                    fSide = Side.Green;
                                    sendPlayer("Now let's travel the green way.");
                                }
                            } else {
                                if (lNextDestination.green_links.contains(fLastItem.key)
                                        //&& fLastItem.red_links.isEmpty()
                                        //&& lNextDestination.red_links.isEmpty()
                                        ) {
                                    fSide = Side.Red;
                                    sendPlayer("Now let's travel the red way.");
                                }
                            }
                        }
                    } else {
                        sendPlayer("Travel stopped.");
                        deactivate();
                    }
                }
            } finally {
                fInRun = false;
            }
        }
    }
    
    protected void triggerLevers(WaterPathItem aItem) {
        if (fSide == Side.Red) {
            triggerLevers(aItem.red_position.getBlockAt(fBoat.getWorld(), 0, 1, 0));
        } else {
            triggerLevers(aItem.green_position.getBlockAt(fBoat.getWorld(), 0, 1, 0));
        }
    }

    protected void triggerLevers(Block aBlock) {
        Material lMat = aBlock.getType();
        if (lMat.equals(Material.LEVER)) {
            aBlock.setData((byte)(aBlock.getData() ^ 0x08));
            plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new TriggerLever(aBlock), plugin.configLeverTicks);
        } else if (lMat.equals(Material.STONE_BUTTON)) {
            aBlock.setData((byte)(aBlock.getData() ^ 0x08));
            plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new TriggerLever(aBlock), plugin.configLeverTicks);
        }
    }
    
    protected WaterPathItem getNextDestination(boolean lExecute) {
        WaterPathItem lNextDestination = null;
        World lWorld = fBoat.getWorld();
        //Vector lPos = fBoat.getLocation().toVector();
        
        if (fLastItem != null && fSide == Side.Red &&
                ((fDestination.red_links.isEmpty())
                || (fDestination.red_links.size() == 1 && fDestination.red_links.contains(fLastItem.key)) )) {
            if (lExecute) {
                //sendPlayer("No more destinations.");
                deactivate();
            }
        } else if (fLastItem != null && fSide == Side.Green &&
                ((fDestination.green_links.isEmpty())
                || (fDestination.green_links.size() == 1 && fDestination.green_links.contains(fLastItem.key)) )) {
            if (lExecute) {
                //sendPlayer("No more destinations.");
                deactivate();
            }
        } else {
            WaterPathDB lDB = plugin.getWaterPathDB(lWorld.getName());
            if (fLastItem != null) {
                if (   (fSide == Side.Red && fDestination.red_links.size() > 2)
                    || (fSide == Side.Green && fDestination.green_links.size() > 2)) {
                    if (lExecute) {
                        //sendPlayer("To many destinations.");
                        deactivate(); // to many destinations
                    }
                }
                WaterPathItem lRed = null;
                for(String lKey : fDestination.red_links) {
                    if (!lKey.equals(fLastItem.key)) {
                        lRed = lDB.getRecord(lKey);
                        break;
                    }
                }
                WaterPathItem lGreen = null;
                for(String lKey : fDestination.green_links) {
                    if (!lKey.equals(fLastItem.key)) {
                        lGreen = lDB.getRecord(lKey);
                        break;
                    }
                }
                if (fSide == Side.Red && lRed != null) {
                    lNextDestination = lRed;
                } else if (fSide == Side.Green && lGreen != null) {
                    lNextDestination = lGreen;
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
        }
        return lNextDestination;
    }
    
    protected void deactivate() {
        if (fLastItem != null) {
            triggerLevers(fLastItem);
        }
        plugin.deactivateBoatMovement(fBoat);
        //plugin.getLogger().info("boat stopped.");
    }

    public void setTaskId(int aTaskId) {
        fTaskId = aTaskId;
    }
    
    public int getTaskId() {
        return fTaskId;
    }
    
    public void sendPlayer(String aText, Object... aObjects) {
        Entity lPassenger = fBoat.getPassenger();
        if (lPassenger != null && lPassenger instanceof Player) {
            Player lPlayer = (Player)lPassenger;
            lPlayer.sendMessage(BuoyMain.plugin.getText(lPlayer, aText, aObjects));
        }
    }
}
