/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.buoy;

import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
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
    public ArrayList<String> red_links;
    public ArrayList<String> green_links;
    public String player;
    public boolean swapRedToGreen = false;
    public boolean swapGreenToRed = false;
    
    @Override
    protected void init() {
        super.init();
        if (red_position == null) red_position = new BlockPosition();
        if (green_position == null) green_position = new BlockPosition();
        if (mid_position == null) mid_position = new BlockPosition();
        if (way_green_position == null) way_green_position = new BlockPosition();
        if (way_red_position == null) way_red_position = new BlockPosition();
        if (red_links == null) red_links = new ArrayList<String>();
        if (green_links == null) green_links = new ArrayList<String>();
        if (player == null) player = "";
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
            return lItem.green_position.equals(green_position) && lItem.red_position.equals(red_position);
        } else {
            return false;
        }
    }
    
    @Override
    public String toString() {
        return red_position.toString() + "[" + new Integer(red_links.size()) + "]"
                + (swapRedToGreen ? "RG" : "")
                + "-"
                + green_position.toString() + "[" + new Integer(green_links.size()) + "]"
                + (swapGreenToRed ? "GR" : "");
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
        aCols.add(arrayToKeys(red_links));
        aCols.add(arrayToKeys(green_links));
        aCols.add(player);
        aCols.add(swapRedToGreen);
        aCols.add(swapGreenToRed);
    }
    
    @Override
    protected void fromCSVInternal(DBRecordCSVArray aCols) {
        super.fromCSVInternal(aCols);
        red_position.x = aCols.popInt();
        red_position.y = aCols.popInt();
        red_position.z = aCols.popInt();
        green_position.x = aCols.popInt();
        green_position.y = aCols.popInt();
        green_position.z = aCols.popInt();
        mid_position.x = aCols.popInt();
        mid_position.y = aCols.popInt();
        mid_position.z = aCols.popInt();
        way_red_position.x = aCols.popInt();
        way_red_position.y = aCols.popInt();
        way_red_position.z = aCols.popInt();
        way_green_position.x = aCols.popInt();
        way_green_position.y = aCols.popInt();
        way_green_position.z = aCols.popInt();
        aCols.popKeys(red_links);
        aCols.popKeys(green_links);
        player = aCols.pop();
        swapRedToGreen = Boolean.parseBoolean(aCols.pop());
        swapGreenToRed = Boolean.parseBoolean(aCols.pop());
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
        //little bit more to the middle
        way_red_position = new BlockPosition(
                    (way_red_position.x + mid_position.x) / 2,
                    (way_red_position.y + mid_position.y) / 2,
                    (way_red_position.z + mid_position.z) / 2
                );
        way_green_position = new BlockPosition(
                    (green_position.x + mid_position.x) / 2,
                    (green_position.y + mid_position.y) / 2,
                    (green_position.z + mid_position.z) / 2
                );
        //little bit more to the middle
        way_green_position = new BlockPosition(
                    (way_green_position.x + mid_position.x) / 2,
                    (way_green_position.y + mid_position.y) / 2,
                    (way_green_position.z + mid_position.z) / 2
                );
    }
    
    public void updateSwapRedGreen(World aWorld) {
        swapRedToGreen = isSwapBlock(aWorld, red_position,  1,  0)
                      || isSwapBlock(aWorld, red_position, -1,  0)
                      || isSwapBlock(aWorld, red_position,  0,  1)
                      || isSwapBlock(aWorld, red_position,  0, -1);
        swapGreenToRed = isSwapBlock(aWorld, green_position,  1,  0)
                      || isSwapBlock(aWorld, green_position, -1,  0)
                      || isSwapBlock(aWorld, green_position,  0,  1)
                      || isSwapBlock(aWorld, green_position,  0, -1);
    }
    
    protected boolean isSwapBlock(World aWorld, BlockPosition aPos, int aDX, int aDZ) {
        Block lBlock = red_position.getLocation(aWorld).add(aDX, 0, aDZ).getBlock();
        return lBlock.getType().equals(Material.WOOL) && lBlock.getData() == 4; // yellow
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

    public double distance(int aX, int aY, int aZ) {
        return getVector().distance(new Vector(aX, aY, aZ));
    }

    public double distanceSquared(int aX, int aY, int aZ) {
        return getVector().distanceSquared(new Vector(aX, aY, aZ));
    }
}
