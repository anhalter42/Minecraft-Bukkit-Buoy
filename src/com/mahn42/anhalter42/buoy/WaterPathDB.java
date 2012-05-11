/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.buoy;

import java.io.File;

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
            if (lItem.red_position.isAt(aX,aY,aZ) || lItem.green_position.isAt(aX, aY, aZ)) {
                return lItem;
            }
        }
        return null;
    }
            
    public boolean contains(int aX, int aY, int aZ) {
        return getItemAt(aX, aY, aZ) != null;
    }
}
