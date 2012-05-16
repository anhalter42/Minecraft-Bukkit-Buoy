/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.buoy;

import java.util.ArrayList;
import org.bukkit.Effect;
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
        //Logger.getLogger("buoy").info("begin");
        Location lLoc = startBlock.getLocation();
        World lWorld = startBlock.getWorld();
        WaterPathDB lDB = plugin.getWaterPathDB(lWorld.getName());
        byte aColor;
        if (startBlock.getData() == 14) {
            aColor = 13;
        } else {
            aColor = 14;
        }
        Block[] lBlocks = findNearestBuoy(lWorld, lLoc, 60, null, aColor);
        //Logger.getLogger("buoy").info("found " + new Integer(lBlocks.length).toString());
        if (lBlocks.length > 0) {
            for(Block lBlock : lBlocks) {
                Location lLoc2;
                if (aColor == 14) {
                    lLoc2 = lLoc;
                    lLoc = lBlock.getLocation();
                } else {
                    lLoc2 = lBlock.getLocation();
                }
                boolean lWaterLine = true;
                for(BlockPosition lPos : new WorldLineWalk(lLoc, lLoc2)) {
                    int lId = lPos.getBlockTypeId(lWorld);
                    if (!((lId == 8) || (lId == 9) || (lId == 35))) {
                        lWaterLine = false;
                        break;
                    }
                }
                if (lWaterLine) {
                    WaterPathItem lItem = lDB.getItemAt(lLoc);
                    if (lItem == null) {
                        lItem = new WaterPathItem(lLoc, lLoc2);
                        lDB.addRecord(lItem);
                        player.sendMessage("Buoy activated.");
                        player.playEffect(lLoc, Effect.CLICK2, (byte)0);
                        //updateLinks(lDB, lItem);
                    } else {
                        player.sendMessage("Buoy is already active.");
                        //updateLinks(lDB, lItem);
                    }
                    lDB.save(); // TODO save it later (perhaps every minute)
                    break;
                } else {
                    player.sendMessage("Buoys must have a direct link with water only!");
                }
            }
        } else {
            player.sendMessage("No corresponding buoy found. You need red and green buoy.");
        }
        //Logger.getLogger("buoy").info("end");
    }
    
    // 1 3  2 5  3 7  4 9
    protected Block[] findNearestBuoy(World aWorld, Location aStart, int aMaxRadius, Location[] aExcludeLocations, byte aColor) {
        ArrayList lList = new ArrayList();
        Location lLoc = aStart;
        World lWorld = aWorld;
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
                            lList.add(lBlock);
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
        Block[] lBlocks = new Block[lList.size()];
        int lIndex = 0;
        for(Object lObject : lList) {
            Block lBlock = (Block) lObject;
            lBlocks[lIndex] = lBlock;
            lIndex++;
        }
        return lBlocks;
    }

    protected void updateLinks(WaterPathDB aDB, WaterPathItem aItem) {
        aDB.updateLinks(aItem);
    }
}
