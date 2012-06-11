/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.buoy;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

/**
 *
 * @author andre
 */
public class BlockListener implements Listener {
    
    protected BuoyMain plugin;
    
    public BlockListener(BuoyMain aPlugin) {
        plugin = aPlugin;
    }
    
    @EventHandler
    public void breakBlock(BlockBreakEvent aEvent) {
        Block lBlock = aEvent.getBlock();
        if (lBlock.getType().equals(Material.WOOL)) {
            byte lColor = lBlock.getData();
            if (lColor == plugin.configGreenBouyColor || lColor == plugin.configRedBouyColor) { // green or red
                WaterPathDB lDB = plugin.getWaterPathDB(lBlock.getWorld().getName());
                boolean lFound = false;
                WaterPathItem lBuoy = lDB.getItemAt(lBlock.getLocation());
                while (lBuoy != null) {
                    lDB.remove(lBuoy);
                    aEvent.getPlayer().sendMessage("Buoy is destroyed.");
                    lBuoy = lDB.getItemAt(lBlock.getLocation());
                    lFound = true;
                }
                if (lFound) {
                    plugin.updateDynMapBuoy();
                }
            }
        }
    }
}
