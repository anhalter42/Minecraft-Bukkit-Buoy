/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.buoy;

import com.mahn42.anhalter42.buoy.BlockPosition;
import org.bukkit.Location;

/**
 *
 * @author andre
 */
public class WaterPathItem {
    public BlockPosition red_position;
    public BlockPosition green_position;
    
    public WaterPathItem() {
        red_position = new BlockPosition();
        green_position = new BlockPosition();
    }
            
    public WaterPathItem(BlockPosition aRedPosition, BlockPosition aGreenPosition) {
        red_position = aRedPosition;
        green_position = aGreenPosition;
    }

    public WaterPathItem(String aLine) {
        this.fromString(aLine);
    }
            
    public WaterPathItem(int aRedX, int aRedY, int aRedZ, int aGreenX, int aGreenY, int aGreenZ) {
        red_position = new BlockPosition(aRedX, aRedY, aRedZ);
        green_position = new BlockPosition(aGreenX, aGreenY, aGreenZ);
    }
    
    public WaterPathItem(Location aRed, Location aGreen) {
        red_position = new BlockPosition(aRed);
        green_position = new BlockPosition(aGreen);
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
        return red_position.toCSV() + ";" + green_position.toCSV();
    }
    
    public void fromString(String aLine) {
        String[] lCols = aLine.split(";");
        red_position.x = new Integer(lCols[0]).intValue();
        red_position.y = new Integer(lCols[1]).intValue();
        red_position.z = new Integer(lCols[2]).intValue();
        green_position.x = new Integer(lCols[3]).intValue();
        green_position.y = new Integer(lCols[4]).intValue();
        green_position.z = new Integer(lCols[5]).intValue();
    }
    
}
