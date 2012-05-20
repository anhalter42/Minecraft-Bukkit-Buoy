/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.buoy;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

/**
 *
 * @author andre
 */
public class BlockPosition {
    
    public int x;
    public int y;
    public int z;
    
    public BlockPosition() {
    }
    
    public BlockPosition(int aX, int aY, int aZ) {
        x = aX;
        y = aY;
        z = aZ;
    }
    
    public BlockPosition(Location aLocation) {
        x = aLocation.getBlockX();
        y = aLocation.getBlockY();
        z = aLocation.getBlockZ();
    }
    
    public BlockPosition(BlockPosition aPos) {
        x = aPos.x;
        y = aPos.y;
        z = aPos.z;
    }
    
    public Location getLocation(World aWorld) {
        return new Location(aWorld, x, y, z);
    }
    
    public int getBlockTypeId(World aWorld) {
        return aWorld.getBlockTypeIdAt(x, y, z);
    }
    
    @Override
    public boolean equals(Object aObject) {
        if (aObject instanceof BlockPosition) {
            BlockPosition lPos = (BlockPosition) aObject;
            return (lPos.x == x) && (lPos.y == y) && (lPos.z == z);
        } else if (aObject instanceof Location) {
            Location lLoc = (Location) aObject;
            return (lLoc.getBlockX() == x) && (lLoc.getBlockY() == y) && (lLoc.getBlockZ() == z);
        } else {
            return false;
        }
    }
    
    @Override
    public String toString() {
        return "(" + new Integer(x).toString() + "," + new Integer(y).toString() + "," + new Integer(z).toString() + ")";
    }
    
    public String toCSV() {
        return new Integer(x).toString() + ";" + new Integer(y).toString() + ";" + new Integer(z).toString();
    }
    
    public boolean isAt(int aX, int aY, int aZ) {
        return (x == aX) && (y == aY) && (z == aZ);
    }
    
    public Vector getVector() {
        return new Vector(x,y,z);
    }

    public Block getBlock(World aWorld) {
        return aWorld.getBlockAt(x, y, z);
    }
}
