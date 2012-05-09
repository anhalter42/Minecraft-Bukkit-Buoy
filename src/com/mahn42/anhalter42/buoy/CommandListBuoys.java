/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.buoy;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author andre
 */
public class CommandListBuoys implements CommandExecutor {

    protected BuoyMain plugin;
    
    public CommandListBuoys(BuoyMain aPlugin) {
        plugin = aPlugin;
    }
    
    @Override
    public boolean onCommand(CommandSender aCommandSender, Command aCommand, String aString, String[] aStrings) {
        if (aCommandSender instanceof Player) {
          Player lPlayer = (Player) aCommandSender;
          WaterPathDB lDB = plugin.getWaterPathDB(lPlayer.getWorld().getName());
          lPlayer.sendMessage("count:" + new Integer(lDB.fItems.size()).toString());
          for(WaterPathItem lItem : lDB) {
              lPlayer.sendMessage(lItem.toString());
          }
        }
        return true;
    }
    
}
