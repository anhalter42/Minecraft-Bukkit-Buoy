/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.buoy;

import org.bukkit.Location;

/**
 *
 * @author andre
 */
public class WaterPathItem {
    
    public BlockPosition red_position;
    public BlockPosition green_position;
    public BlockPosition mid_position;
    public BlockPosition way_green_position;
    public BlockPosition way_red_position;
    
    public WaterPathItem() {
        red_position = new BlockPosition();
        green_position = new BlockPosition();
        mid_position = new BlockPosition();
        way_green_position = new BlockPosition();
        way_red_position = new BlockPosition();
    }
            
    public WaterPathItem(BlockPosition aRedPosition, BlockPosition aGreenPosition) {
        red_position = aRedPosition;
        green_position = aGreenPosition;
        calcPositions();
    }

    public WaterPathItem(String aLine) {
        red_position = new BlockPosition();
        green_position = new BlockPosition();
        mid_position = new BlockPosition();
        way_green_position = new BlockPosition();
        way_red_position = new BlockPosition();
        fromString(aLine);
    }
            
    public WaterPathItem(int aRedX, int aRedY, int aRedZ, int aGreenX, int aGreenY, int aGreenZ) {
        red_position = new BlockPosition(aRedX, aRedY, aRedZ);
        green_position = new BlockPosition(aGreenX, aGreenY, aGreenZ);
        calcPositions();
    }
    
    public WaterPathItem(Location aRed, Location aGreen) {
        red_position = new BlockPosition(aRed);
        green_position = new BlockPosition(aGreen);
        calcPositions();
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
    
    public String toCSV() {
        return red_position.toCSV() + ";" + green_position.toCSV() + ";" + mid_position.toCSV()+ ";" + way_red_position.toCSV()+ ";" + way_green_position.toCSV();
    }
    
    public void fromString(String aLine) {
        String[] lCols = aLine.split(";");
        if (lCols.length >= 15) {
            red_position.x = new Integer(lCols[0]).intValue();
            red_position.y = new Integer(lCols[1]).intValue();
            red_position.z = new Integer(lCols[2]).intValue();
            green_position.x = new Integer(lCols[3]).intValue();
            green_position.y = new Integer(lCols[4]).intValue();
            green_position.z = new Integer(lCols[5]).intValue();
            mid_position.x = new Integer(lCols[6]).intValue();
            mid_position.y = new Integer(lCols[7]).intValue();
            mid_position.z = new Integer(lCols[8]).intValue();
            way_red_position.x = new Integer(lCols[9]).intValue();
            way_red_position.y = new Integer(lCols[10]).intValue();
            way_red_position.z = new Integer(lCols[11]).intValue();
            way_green_position.x = new Integer(lCols[12]).intValue();
            way_green_position.y = new Integer(lCols[13]).intValue();
            way_green_position.z = new Integer(lCols[14]).intValue();
        }
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
    
}
