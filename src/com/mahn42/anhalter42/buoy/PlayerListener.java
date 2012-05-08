/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.buoy;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author andre
 */


/* colors
colors.put("white",0);
colors.put("orange",1);
colors.put("magenta",2);
colors.put("light_blue",3);
colors.put("yellow",4);
colors.put("lime",5);
colors.put("pink",6);
colors.put("gray",7);
colors.put("light_gray",8);
colors.put("cyan",9);
colors.put("purple",10);
colors.put("blue",11);
colors.put("brown",12);
colors.put("green",13);
colors.put("red",14);
colors.put("black",15);
 */
public class PlayerListener implements Listener {

    BuoyMain plugin;
    
    public PlayerListener(BuoyMain aPlugin) {
        plugin = aPlugin;
    }
    
    @EventHandler
    public void playerInteract(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Block lBlock = event.getClickedBlock();
            if (lBlock != null) {
                event.getPlayer().sendMessage("Interact with " + new Integer(lBlock.getTypeId()) + " at " + lBlock.getLocation().toString());
                if (lBlock.getType().equals(Material.WOOL)) {
                    int lColor = lBlock.getData();
                    event.getPlayer().sendMessage("Color " + new Integer(lColor));
                    Location lLoc = lBlock.getLocation();
                    if ((lColor == 14) || (lColor == 13)) { // red or green
                        int lTypeId = lBlock.getWorld().getBlockTypeIdAt(lLoc.getBlockX(), lLoc.getBlockY() - 1, lLoc.getBlockZ());
                        if ((lTypeId == 9) || (lTypeId == 8)) {
                            event.getPlayer().sendMessage("searching buoy " + lLoc.toString());
                            BuoyFinder lTask = new BuoyFinder(plugin, lBlock, event.getPlayer());
                            plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, lTask);
                        }
                    }
                }
            } else {
                event.getPlayer().sendMessage("Interact with no block");
            }
        }
    }
}
