/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.buoy;

import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.util.Vector;

/**
 *
 * @author andre
 */
public class WaterPathItem extends DBRecord {
    
    public BlockPosition red_position;
    public BlockPosition green_position;
    public BlockPosition mid_position;
    public BlockPosition way_green_position;
    public BlockPosition way_red_position;
    
    protected void init() {
        super.init();
        if (red_position == null) red_position = new BlockPosition();
        if (green_position == null) green_position = new BlockPosition();
        if (mid_position == null) mid_position = new BlockPosition();
        if (way_green_position == null) way_green_position = new BlockPosition();
        if (way_red_position == null) way_red_position = new BlockPosition();
    }
    
    public WaterPathItem() {
        init();
    }
            
    public WaterPathItem(BlockPosition aRedPosition, BlockPosition aGreenPosition) {
        red_position = aRedPosition;
        green_position = aGreenPosition;
        calcPositions();
        init();
    }

    public WaterPathItem(String aHeader, String aLine) {
        init();
        fromCSV(aHeader, aLine);
    }
            
    public WaterPathItem(int aRedX, int aRedY, int aRedZ, int aGreenX, int aGreenY, int aGreenZ) {
        red_position = new BlockPosition(aRedX, aRedY, aRedZ);
        green_position = new BlockPosition(aGreenX, aGreenY, aGreenZ);
        calcPositions();
        init();
    }
    
    public WaterPathItem(Location aRed, Location aGreen) {
        red_position = new BlockPosition(aRed);
        green_position = new BlockPosition(aGreen);
        calcPositions();
        init();
    }
    
    @Override
    public boolean equals(Object aObject) {
        if (aObject instanceof WaterPathItem) {
            WaterPathItem lItem = (WaterPathItem) aObject;
            return lItem.green_position.equals(green_position) && lItem.red_position.equals(green_position);
        } else {
            return false;
        }
    }
    
    @Override
    public String toString() {
        return red_position.toString() + "-" + green_position.toString();
    }
    
    @Override
    protected void toCSVInternal(ArrayList aCols) {
        super.toCSVInternal(aCols);
        aCols.add(red_position.x);
        aCols.add(red_position.y);
        aCols.add(red_position.z);
        aCols.add(green_position.x);
        aCols.add(green_position.y);
        aCols.add(green_position.z);
        aCols.add(mid_position.x);
        aCols.add(mid_position.y);
        aCols.add(mid_position.z);
        aCols.add(way_red_position.x);
        aCols.add(way_red_position.y);
        aCols.add(way_red_position.z);
        aCols.add(way_green_position.x);
        aCols.add(way_green_position.y);
        aCols.add(way_green_position.z);
    }
    
    @Override
    protected void fromCSVInternal(ArrayList aCols) {
        red_position.x = new Integer((String)aCols.get(0)).intValue(); aCols.remove(0);
        red_position.y = new Integer((String)aCols.get(0)).intValue(); aCols.remove(0);
        red_position.z = new Integer((String)aCols.get(0)).intValue(); aCols.remove(0);
        green_position.x = new Integer((String)aCols.get(0)).intValue(); aCols.remove(0);
        green_position.y = new Integer((String)aCols.get(0)).intValue(); aCols.remove(0);
        green_position.z = new Integer((String)aCols.get(0)).intValue(); aCols.remove(0);
        mid_position.x = new Integer((String)aCols.get(0)).intValue(); aCols.remove(0);
        mid_position.y = new Integer((String)aCols.get(0)).intValue(); aCols.remove(0);
        mid_position.z = new Integer((String)aCols.get(0)).intValue(); aCols.remove(0);
        way_red_position.x = new Integer((String)aCols.get(0)).intValue(); aCols.remove(0);
        way_red_position.y = new Integer((String)aCols.get(0)).intValue(); aCols.remove(0);
        way_red_position.z = new Integer((String)aCols.get(0)).intValue(); aCols.remove(0);
        way_green_position.x = new Integer((String)aCols.get(0)).intValue(); aCols.remove(0);
        way_green_position.y = new Integer((String)aCols.get(0)).intValue(); aCols.remove(0);
        way_green_position.z = new Integer((String)aCols.get(0)).intValue(); aCols.remove(0);
    }
    
    public void calcPositions() {
        mid_position = new BlockPosition(
                    (red_position.x + green_position.x) / 2,
                    (red_position.y + green_position.y) / 2,
                    (red_position.z + green_position.z) / 2
                );
        way_red_position = new BlockPosition(
                    (red_position.x + mid_position.x) / 2,
                    (red_position.y + mid_position.y) / 2,
                    (red_position.z + mid_position.z) / 2
                );
        way_green_position = new BlockPosition(
                    (green_position.x + mid_position.x) / 2,
                    (green_position.y + mid_position.y) / 2,
                    (green_position.z + mid_position.z) / 2
                );
    }
    
    public Vector getVector() {
        return mid_position.getVector();
    }
    
    public double distanceSquared(WaterPathItem lItem) {
        return getVector().distanceSquared(lItem.getVector());
    }

    public double distance(WaterPathItem lItem) {
        return getVector().distance(lItem.getVector());
    }
}
