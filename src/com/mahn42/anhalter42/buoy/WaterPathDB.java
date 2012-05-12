/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.buoy;

import java.io.File;
import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.util.Vector;

/**
 *
 * @author andre
 */
public class WaterPathDB extends DBSet<WaterPathItem> {
    public WaterPathDB(File aFile) {
        super(WaterPathItem.class, aFile);
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
            
    public boolean contains(int aX, int aY, int aZ) {
        return getItemAt(aX, aY, aZ) != null;
    }
    
    public ArrayList<WaterPathItem> getItemNearly(int aX, int aY, int aZ, double aDistance) {
        ArrayList<WaterPathItem> lResult = new ArrayList<WaterPathItem>();
        for (WaterPathItem lItem : this) {
            double lDistance = lItem.distance(aX, aY, aZ);
            if (lDistance <= aDistance) {
                lResult.add(lItem);
            }
        }
        return lResult;
    }

    public ArrayList<WaterPathItem> getItemNearlySquared(int aX, int aY, int aZ, double aDistanceSquared) {
        ArrayList<WaterPathItem> lResult = new ArrayList<WaterPathItem>();
        for (WaterPathItem lItem : this) {
            double lDistance = lItem.distanceSquared(aX, aY, aZ);
            if (lDistance <= aDistanceSquared) {
                lResult.add(lItem);
            }
        }
        return lResult;
    }

    public ArrayList<WaterPathItem> getItemNearlyDirection(int aX, int aY, int aZ, double aDistance, Vector aDirection, float aMinAngle, float aMaxAngle) {
        ArrayList<WaterPathItem> lResult = new ArrayList<WaterPathItem>();
        for (WaterPathItem lItem : this) {
            double lDistance = lItem.distance(aX, aY, aZ);
            if (lDistance <= aDistance) {
                Vector lVector = new Vector(aX - lItem.mid_position.x, aY - lItem.mid_position.y, aZ - lItem.mid_position.z);
                float lAngle = aDirection.angle(lVector);
                if (aMinAngle <= lAngle && lAngle <= aMaxAngle) {
                    lResult.add(lItem);
                }
            }
        }
        return lResult;
    }

    public ArrayList<WaterPathItem> getItemNearlyDirection(Location aLocation, double aDistance, Vector aDirection, float aMinAngle, float aMaxAngle) {
        return getItemNearlyDirection(aLocation.getBlockX(), aLocation.getBlockY(), aLocation.getBlockZ(), aDistance, aDirection, aMinAngle, aMaxAngle);
    }

    public ArrayList<WaterPathItem> getItemNearlyDirection(BlockPosition aPos, double aDistance, Vector aDirection, float aMinAngle, float aMaxAngle) {
        return getItemNearlyDirection(aPos.x, aPos.y, aPos.z, aDistance, aDirection, aMinAngle, aMaxAngle);
    }
}
