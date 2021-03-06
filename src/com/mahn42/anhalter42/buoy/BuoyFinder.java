/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.buoy;

import com.mahn42.framework.BlockPosition;
import com.mahn42.framework.WorldLineWalk;
import java.util.ArrayList;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
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
        WaterPathDB lDB = plugin.getWaterPathDB(lWorld.getName());
        byte aColor;
        byte aBlockColor = startBlock.getData();
        if (aBlockColor == plugin.configRedBouyColor) {
            aColor = plugin.configGreenBouyColor;
        } else {
            aColor = plugin.configRedBouyColor;
        }
        WaterPathItem lItem = lDB.getItemAt(lLoc);
        if (lItem != null) {
            lItem.calcPositions();
            //lItem.updateSwapRedGreen(lWorld);
            plugin.updateDynMapBuoy();
            //player.sendMessage((lItem.swapGreenToRed ? "GR" : "") + " " + (lItem.swapRedToGreen ? "RG" : "")) ;
            player.sendMessage(BuoyMain.plugin.getText(player, "Buoy is already active."));
            PlayerBuoyConnection aCon = BuoyMain.plugin.getBuoyConnection(player.getName());
            if (aCon == null) {
                aCon = new PlayerBuoyConnection();
                aCon.player = player.getName();
                aCon.firstKey = lItem.key;
                aCon.firstColor = aBlockColor;
                BuoyMain.plugin.setBuoyConnection(aCon);
                player.sendMessage(BuoyMain.plugin.getText(player, "This buoy is marked as startpoint. Go to the next buoy and activate again to connect."));
            } else {
                BuoyMain.plugin.removeBuoyConnection(player.getName());
                if (aCon.firstKey.equals(lItem.key)) {
                    player.sendMessage(BuoyMain.plugin.getText(player, "Unmarking this buoy as startpoint."));
                } else {
                    WaterPathItem lFirst = lDB.getRecord(aCon.firstKey);
                    if (lFirst != null) {
                        double lDistance = lFirst.mid_position.distance(lItem.mid_position);
                        if (lDistance > plugin.configMaxDistanceSetDestination) {
                            player.sendMessage(BuoyMain.plugin.getText(player, "Buoys are to far away (%dm).", plugin.configMaxDistanceSetDestination));
                        } else {
                            if (aBlockColor == aCon.firstColor) {
                                if (aBlockColor == plugin.configGreenBouyColor) {
                                    if (!lFirst.green_links.contains(lItem.key)) {
                                        lFirst.green_links.add(lItem.key);
                                    }
                                    if (!lItem.red_links.contains(lFirst.key)) {
                                        lItem.red_links.add(lFirst.key);
                                    }
                                } else {
                                    if (!lFirst.red_links.contains(lItem.key)) {
                                        lFirst.red_links.add(lItem.key);
                                    }
                                    if (!lItem.green_links.contains(lFirst.key)) {
                                        lItem.green_links.add(lFirst.key);
                                    }
                                }
                                player.sendMessage(BuoyMain.plugin.getText(player, "Buoys are now connected with red and green ways."));
                            } else {
                                lFirst.red_links.clear();
                                lFirst.green_links.clear();
                                lItem.red_links.clear();
                                lItem.green_links.clear();
                                if (aBlockColor == plugin.configGreenBouyColor) {
                                    lFirst.green_links.add(lItem.key);
                                } else {
                                    lFirst.red_links.add(lItem.key);
                                }
                                player.sendMessage(BuoyMain.plugin.getText(player, "Buoys are now cross connected."));
                            }
                        }
                    } else {
                        player.sendMessage(BuoyMain.plugin.getText(player, "Starting buoy does not exists any more."));
                    }
                }
            }
            
        } else {
            Block[] lBlocks = findNearestBuoy(lWorld, lLoc, plugin.configMaxBuoyDistance, null, aColor);
            if (lBlocks.length > 0) {
                for(Block lBlock : lBlocks) {
                    Location lLoc2 = lBlock.getLocation();
                    lItem = lDB.getItemAt(lLoc2);
                    if (lItem != null) {
                        player.sendMessage(BuoyMain.plugin.getText(player, "Corresponding buoy is already bundled."));
                    } else {
                        boolean lWaterLine = true;
                        for(BlockPosition lPos : new WorldLineWalk(lLoc, lLoc2)) {
                            int lId = lPos.getBlockTypeId(lWorld);
                            if (!((lId == Material.WATER.getId())
                                    || (lId == Material.STATIONARY_WATER.getId())
                                    || (lId == Material.WOOL.getId()))) {
                                lWaterLine = false;
                                break;
                            }
                        }
                        if (lWaterLine) {
                            if (aColor == plugin.configGreenBouyColor) {
                                lItem = new WaterPathItem(lLoc, lLoc2);
                            } else {
                                lItem = new WaterPathItem(lLoc2, lLoc);
                            }
                            lItem.player = player.getName();
                            //lItem.updateSwapRedGreen(lWorld);
                            lDB.addRecord(lItem);
                            player.sendMessage(BuoyMain.plugin.getText(player, "Buoy activated."));
                            player.playEffect(lLoc, Effect.CLICK2, (byte)0);
                            plugin.updateDynMapBuoy();
                            break;
                        } else {
                            player.sendMessage(BuoyMain.plugin.getText(player, "Buoys must have a direct link with water only!"));
                        }
                    }
                }
            } else {
                player.sendMessage(BuoyMain.plugin.getText(player, "No corresponding buoy found. You need red and green buoy."));
            }
        }
    }
    
    protected class BlockDistance implements Comparable<BlockDistance> {
        Block item;
        double distance;
        
        BlockDistance(Block aItem, double aDistance) {
            item = aItem;
            distance = aDistance;
        }

        @Override
        public int compareTo(BlockDistance aObject) {
            if (aObject instanceof BlockDistance) {
                if (((BlockDistance)aObject).distance < distance) {
                    return +1;
                } else if (((BlockDistance)aObject).distance == distance) {
                    return 0;
                } else {
                    return -1;
                }
            } else {
                return 0;
            }
        }
    }
    
    protected Block[] findNearestBuoy(World aWorld, Location aStart, int aMaxRadius, Location[] aExcludeLocations, byte aColor) {
        ArrayList<BlockDistance> lList = new ArrayList<BlockDistance>();
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
                            BlockDistance lBD = new BlockDistance(lBlock, aStart.distance(lBlock.getLocation()));
                            lList.add(lBD);
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
        java.util.Collections.sort(lList);
        Block[] lBlocks = new Block[lList.size()];
        int lIndex = 0;
        for(BlockDistance lBD : lList) {
            lBlocks[lIndex] = lBD.item;
            lIndex++;
        }
        return lBlocks;
    }

    protected void updateLinks(WaterPathDB aDB, WaterPathItem aItem) {
        aDB.updateLinks(aItem);
    }
}
