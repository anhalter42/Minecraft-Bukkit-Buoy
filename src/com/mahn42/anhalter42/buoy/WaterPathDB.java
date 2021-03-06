/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.buoy;

import com.mahn42.framework.BlockPosition;
import com.mahn42.framework.BlockPositionWalkAround;
import com.mahn42.framework.DBSetWorld;
import com.mahn42.framework.WorldLineWalk;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.util.Vector;

/**
 *
 * @author andre
 */
public class WaterPathDB extends DBSetWorld<WaterPathItem> {
    protected double fMaxLinkDistance = 8000.0f;
    
    public WaterPathDB() {
        super(WaterPathItem.class);
    }
    
    public WaterPathDB(World aWorld, File aFile) {
        super(WaterPathItem.class, aFile, aWorld);
    }
    
    public void setMaxLinkDistance(double aValue) {
        fMaxLinkDistance = aValue;
    }
    
    public WaterPathItem getItemAt(int aX, int aY, int aZ) {
        for (WaterPathItem lItem : this) {
            if (lItem.red_position.isAt(aX,aY,aZ)
                || lItem.green_position.isAt(aX, aY, aZ)
                || lItem.mid_position.isAt(aX, aY, aZ)) {
                return lItem;
            }
        }
        return null;
    }
            
    public WaterPathItem getItemForStart(BlockPosition aPos) {
        for (WaterPathItem lItem : this) {
            if (aPos.isBetween(lItem.way_green_position, lItem.way_red_position)) {
                for(BlockPosition lPos : new WorldLineWalk(lItem.way_green_position, lItem.way_red_position)) {
                    if (lPos.equals(aPos)) {
                        return lItem;
                    }
                }
            }
        }
        return null;
    }
            
    public WaterPathItem getItemAt(Location lLoc) {
        return getItemAt(lLoc.getBlockX(), lLoc.getBlockY(), lLoc.getBlockZ());
    }

    public WaterPathItem getItemAt(BlockPosition aPos) {
        return getItemAt(aPos.x, aPos.y, aPos.z);
    }

    public boolean contains(int aX, int aY, int aZ) {
        return getItemAt(aX, aY, aZ) != null;
    }
    
    public ArrayList<WaterPathItem> getItemNearly(int aX, int aY, int aZ, double aDistance) {
        ArrayList<WaterPathItemDistance> lDistances = new ArrayList<WaterPathItemDistance>();
        ArrayList<WaterPathItem> lResult = new ArrayList<WaterPathItem>();
        for (WaterPathItem lItem : this) {
            double lDistance = lItem.distance(aX, aY, aZ);
            if (lDistance <= aDistance) {
                lDistances.add(new WaterPathItemDistance(lItem, lDistance));
            }
        }
        java.util.Collections.sort(lDistances);
        for(WaterPathItemDistance lItemDist : lDistances) {
            lResult.add(lItemDist.item);
        }
        return lResult;
    }

    public ArrayList<WaterPathItem> getItemNearlySquared(int aX, int aY, int aZ, double aDistanceSquared) {
        ArrayList<WaterPathItemDistance> lDistances = new ArrayList<WaterPathItemDistance>();
        ArrayList<WaterPathItem> lResult = new ArrayList<WaterPathItem>();
        for (WaterPathItem lItem : this) {
            double lDistance = lItem.distanceSquared(aX, aY, aZ);
            if (lDistance <= aDistanceSquared) {
                lDistances.add(new WaterPathItemDistance(lItem, lDistance));
            }
        }
        java.util.Collections.sort(lDistances);
        for(WaterPathItemDistance lItemDist : lDistances) {
            lResult.add(lItemDist.item);
        }
        return lResult;
    }

    protected class WaterPathItemDistance implements Comparable<WaterPathItemDistance> {
        WaterPathItem item;
        double distance;
        
        WaterPathItemDistance(WaterPathItem aItem, double aDistance) {
            item = aItem;
            distance = aDistance;
        }

        @Override
        public int compareTo(WaterPathItemDistance aObject) {
            if (aObject instanceof WaterPathItemDistance) {
                if (((WaterPathItemDistance)aObject).distance < distance) {
                    return +1;
                } else if (((WaterPathItemDistance)aObject).distance == distance) {
                    return 0;
                } else {
                    return -1;
                }
            } else {
                return 0;
            }
        }
    }
    
    public ArrayList<WaterPathItem> getItemNearlyDirection(int aX, int aY, int aZ, double aDistance, Vector aDirection, float aMinAngle, float aMaxAngle) {
        //Logger.getLogger("xxx").info("v" + aDirection.toString() + " xyz" + new Integer(aX).toString() + ","+ new Integer(aY).toString()+","+new Integer(aZ).toString());
        ArrayList<WaterPathItemDistance> lDistances = new ArrayList<WaterPathItemDistance>();
        ArrayList<WaterPathItem> lResult = new ArrayList<WaterPathItem>();
        for (WaterPathItem lItem : this) {
            double lDistance = lItem.distance(aX, aY, aZ);
            if (lDistance <= aDistance) {
                Vector lVector = new Vector(lItem.mid_position.x - aX, aDirection.getBlockY(), lItem.mid_position.z - aZ);
                boolean lx1p = lVector.getBlockX() >= 0 ? true : false;
                boolean lz1p = lVector.getBlockZ() >= 0 ? true : false;
                boolean lx2p = aDirection.getBlockX() >= 0 ? true : false;
                boolean lz2p = aDirection.getBlockZ() >= 0 ? true : false;
                int lsame = 0;
                if ((lx1p && lx2p) || (!lx1p && !lx2p)) lsame++;
                if ((lz1p && lz2p) || (!lz1p && !lz2p)) lsame++;
                if (lsame >= 2) {
                    float lAngle = aDirection.angle(lVector);
                    Logger.getLogger("xxx").info(lItem.toString() + " v " + lVector.toString() + " Angle " + lAngle + "(" + aMinAngle + "," + aMaxAngle + ")" + " d " + aDirection.toString() + " dis " + lDistance);
                    if (aMinAngle <= lAngle && lAngle <= aMaxAngle) {
                        lDistances.add(new WaterPathItemDistance(lItem, lDistance));
                    }
                }
            }
        }
        java.util.Collections.sort(lDistances);
        for(WaterPathItemDistance lItemDist : lDistances) {
            lResult.add(lItemDist.item);
        }
        return lResult;
    }

    public ArrayList<WaterPathItem> getItemNearlyDirection(Location aLocation, double aDistance, Vector aDirection, float aMinAngle, float aMaxAngle) {
        return getItemNearlyDirection(aLocation.getBlockX(), aLocation.getBlockY(), aLocation.getBlockZ(), aDistance, aDirection, aMinAngle, aMaxAngle);
    }

    public ArrayList<WaterPathItem> getItemNearlyDirection(BlockPosition aPos, double aDistance, Vector aDirection, float aMinAngle, float aMaxAngle) {
        return getItemNearlyDirection(aPos.x, aPos.y, aPos.z, aDistance, aDirection, aMinAngle, aMaxAngle);
    }
    
    @Override
    protected void removedRecord(WaterPathItem aRecord) {
        for(WaterPathItem lItem : this) {
            lItem.red_links.remove(aRecord.key);
            lItem.green_links.remove(aRecord.key);
        }
    }

    public void updateLinks(WaterPathItem aItem) {
        //Logger.getLogger("updateLinks").info(aItem.toString());
        for(WaterPathItem lItem : this) {
            if (lItem != aItem) {
                double lDistance = aItem.distanceSquared(lItem);
                //Logger.getLogger("updateLinks").info("" + lDistance);
                if (lDistance < fMaxLinkDistance) {
                    boolean lRedWaterline = true;
                    boolean lFirst = true;
                    //Logger.getLogger("updateLinks").info(lItem.toString());
                    for(BlockPosition lPos : new WorldLineWalk(aItem.way_red_position, lItem.way_red_position)) {
                        //Logger.getLogger("updateLinks").info(lPos.toString());
                        if (lFirst) lFirst = false; else {
                            Material lMat = lPos.getBlockType(world);
                            if (!(lMat.equals(Material.WATER) || lMat.equals(Material.STATIONARY_WATER))) {
                                Logger.getLogger("updateLinks").info("red blocked by " + lMat);
                                lRedWaterline = false;
                                break;
                            }
                        }
                    }
                    if (lRedWaterline) {
                        if (!aItem.red_links.contains(lItem.key)) aItem.red_links.add(lItem.key);
                        if (!lItem.red_links.contains(aItem.key)) lItem.red_links.add(aItem.key);
                        //Logger.getLogger("updateLinks").info("red " + lItem.key);
                    }
                    boolean lGreenWaterline = true;
                    lFirst = true;
                    for(BlockPosition lPos : new WorldLineWalk(aItem.way_green_position, lItem.way_green_position)) {
                        if (lFirst) lFirst = false; else {
                            Material lMat = lPos.getBlockType(world);
                            if (!(lMat.equals(Material.WATER) || lMat.equals(Material.STATIONARY_WATER))) {
                                Logger.getLogger("updateLinks").info("green blocked by " + lMat);
                                //fWorld.getBlockAt(lPos.x, lPos.y + 1, lPos.z).setTypeId(Material.TORCH.getId());
                                lGreenWaterline = false;
                                break;
                            }
                        }
                    }
                    if (lGreenWaterline) {
                        if (!aItem.green_links.contains(lItem.key)) aItem.green_links.add(lItem.key);
                        if (!lItem.green_links.contains(aItem.key)) lItem.green_links.add(aItem.key);
                        //Logger.getLogger("updateLinks").info("green " + lItem.key);
                    }
                }
            }
        }
    }
}
