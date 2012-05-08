/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.buoy;

import java.io.File;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 *
 * @author andre
 */
public class BuoyFinder implements Runnable {
    public BuoyMain plugin;
    public Block startBlock;
    public Player player;
    
    public BuoyFinder(BuoyMain aPlugin, Block aBlock, Player aPlayer) {
        plugin = aPlugin;
        startBlock = aBlock;
        player = aPlayer;
    }
            
    @Override
    public void run() {
        Location lLoc = startBlock.getLocation();
        World lWorld = startBlock.getWorld();
        File lFolder = lWorld.getWorldFolder();
        String lPath = lFolder.getPath();
        lPath = lPath + File.separatorChar + "buoy.csv";
        File lFile = new File(lPath);
        WaterPathDB lDB = new WaterPathDB(lFile);
        lDB.load();
        byte aColor;
        if (startBlock.getData() == 14) {
            aColor = 13;
        } else {
            aColor = 14;
        }
        Block lBlock = findNearestBuoy(lWorld, lLoc, 40, null, aColor);
        if (lBlock != null) {
            Location lLoc2;
            if (aColor == 14) {
                lLoc2 = lLoc;
                lLoc = lBlock.getLocation();
            } else {
                lLoc2 = lBlock.getLocation();
            }
            if (!lDB.contains(lLoc.getBlockX(), lLoc.getBlockY(), lLoc.getBlockZ())) {
                WaterPathItem lItem = new WaterPathItem(lLoc, lLoc2);
                lWorld.getBlockAt(lItem.mid_position.x, lItem.mid_position.y, lItem.mid_position.z).setTypeIdAndData(35, (byte) 2, true);
                lWorld.getBlockAt(lItem.way_red_position.x, lItem.way_red_position.y, lItem.way_red_position.z).setTypeIdAndData(35, (byte) 14, true);
                lWorld.getBlockAt(lItem.way_green_position.x, lItem.way_green_position.y, lItem.way_green_position.z).setTypeIdAndData(35, (byte) 13, true);
                lDB.addItem(lItem);
                lDB.save();
                player.sendMessage("Buoy activated.");
            } else {
                player.sendMessage("Buoy is already active.");
            }
        } else {
            player.sendMessage("no corresponding buoy found. you need red and green buoy.");
        }
    }
    
    // 1 3  2 5  3 7  4 9
    protected Block findNearestBuoy(World aWorld, Location aStart, int aMaxRadius, Location[] aExcludeLocations, byte aColor) {
        Location lLoc = aStart;
        World lWorld = aWorld;
        //List lBlocks = new ArrayList();
        int maxlen = 1 + (aMaxRadius * 2);
        int x = lLoc.getBlockX();
        int y = lLoc.getBlockY();
        int z = lLoc.getBlockZ();
        x--; z--;
        int dx = 1;
        int dz = 0;
        int len = 3;
        int pos;
        boolean lExclude;
        while (len < maxlen) {
            pos = 0;
            while (pos < len) {
                lExclude = false;
                if (aExcludeLocations != null) {
                    for (Location lExLoc : aExcludeLocations) {
                        if ((lExLoc.getBlockX() == x) && (lExLoc.getBlockY() == y) && (lExLoc.getBlockZ() == z)) {
                            lExclude = true;
                            break;
                        }
                    }
                }
                if (!lExclude) {
                    int lID = lWorld.getBlockTypeIdAt(x, y, z);
                    if (lID == 35) { // Wolle
                        Block lBlock = lWorld.getBlockAt(x, y, z);
                        if ((lBlock.getData() == aColor)
                            && (lWorld.getBlockTypeIdAt(x, y - 1, z) == 9)) { // color 14 red 
                            return lBlock;
                        }
                    }
                }
                x+=dx;
                z+=dz;
                pos++;
            }
            x-=dx;
            z-=dz;
            if (dx > 0) {
                dx = 0;
                dz = 1;
            } else if (dx < 0) {
                dx = 0;
                dz = -1;
            } else if (dz > 0) {
                dx = -1;
                dz = 0;
            } else {
                dx = 1; 
                dz = 0;
                x--; z--; len+=2;
            }
        }
        return null;
    }
}
