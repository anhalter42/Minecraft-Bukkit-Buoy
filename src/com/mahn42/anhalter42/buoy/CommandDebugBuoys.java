/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.buoy;

import com.mahn42.framework.BlockPosition;
import com.mahn42.framework.WorldLineWalk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author andre
 */
public class CommandDebugBuoys implements CommandExecutor {

    protected BuoyMain plugin;
    
    public CommandDebugBuoys(BuoyMain aPlugin) {
        plugin = aPlugin;
    }

    @Override
    public boolean onCommand(CommandSender aCommandSender, Command aCommand, String aString, String[] aStrings) {
        if (aCommandSender instanceof Player) {
            Material lSetMaterial;
            boolean lRemove = aStrings.length > 0;
            Player lPlayer = (Player) aCommandSender;
            World lWorld = lPlayer.getWorld();
            WaterPathDB lDB = plugin.getWaterPathDB(lWorld.getName());
            if (lRemove) {
                lSetMaterial = Material.WATER;
            } else {
                lSetMaterial = Material.WOOL;
            }
            for(WaterPathItem lItem : lDB) {
                //TODO only if in near from player
                if (lItem.player.equalsIgnoreCase(lPlayer.getName())) {
                    for(String lKey : lItem.red_links) {
                        WaterPathItem lNextItem = lDB.getRecord(lKey);
                        for(BlockPosition lPos : new WorldLineWalk(lItem.way_red_position, lNextItem.way_red_position)) {
                            if (lRemove) {
                                for(int dx = -1; dx <= 1; dx++) {
                                    for (int dz = -1; dz <= 1; dz++) {
                                        Block lBlock = lPos.getBlockAt(lWorld, dx, 0, dz);
                                        Material lMat = lBlock.getType();
                                        if (lMat.equals(Material.WATER)
                                                || lMat.equals(Material.STATIONARY_WATER)
                                                || lMat.equals(Material.WOOL)) {
                                            lBlock.setTypeIdAndData(lSetMaterial.getId(), plugin.configRedBouyColor, false);
                                        }
                                    }
                                }
                            } else {
                                lPos.getBlock(lWorld).setTypeIdAndData(lSetMaterial.getId(), plugin.configRedBouyColor, false);
                            }
                        }
                    }
                    for(String lKey : lItem.green_links) {
                        WaterPathItem lNextItem = lDB.getRecord(lKey);
                        for(BlockPosition lPos : new WorldLineWalk(lItem.way_green_position, lNextItem.way_green_position)) {
                            if (lRemove) {
                                for(int dx = -1; dx <= 1; dx++) {
                                    for (int dz = -1; dz <= 1; dz++) {
                                        Block lBlock = lPos.getBlockAt(lWorld, dx, 0, dz);
                                        Material lMat = lBlock.getType();
                                        if (lMat.equals(Material.WATER)
                                                || lMat.equals(Material.STATIONARY_WATER)
                                                || lMat.equals(Material.WOOL)) {
                                            lBlock.setTypeIdAndData(lSetMaterial.getId(), plugin.configGreenBouyColor, false);
                                        }
                                    }
                                }
                            } else {
                                lPos.getBlock(lWorld).setTypeIdAndData(lSetMaterial.getId(), plugin.configGreenBouyColor, false);
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
    
}
