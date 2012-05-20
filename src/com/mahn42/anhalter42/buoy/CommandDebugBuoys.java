/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.buoy;

import org.bukkit.Material;
import org.bukkit.World;
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
            int lId;
            Player lPlayer = (Player) aCommandSender;
            World lWorld = lPlayer.getWorld();
            WaterPathDB lDB = plugin.getWaterPathDB(lWorld.getName());
            if (aStrings.length > 0) {
                lId = Material.WATER.getId();
            } else {
                lId = Material.WOOL.getId();
            }
            for(WaterPathItem lItem : lDB) {
                for(String lKey : lItem.red_links) {
                    WaterPathItem lNextItem = lDB.getRecord(lKey);
                    for(BlockPosition lPos : new WorldLineWalk(lItem.way_red_position, lNextItem.way_red_position)) {
                        lPos.getBlock(lWorld).setTypeIdAndData(lId,(byte)14, false);
                    }
                }
                for(String lKey : lItem.green_links) {
                    WaterPathItem lNextItem = lDB.getRecord(lKey);
                    for(BlockPosition lPos : new WorldLineWalk(lItem.way_green_position, lNextItem.way_green_position)) {
                        lPos.getBlock(lWorld).setTypeIdAndData(lId,(byte)13, false);
                    }
                }
            }
        }
        return true;
    }
    
}
