/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.buoy;

import java.util.ArrayList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author andre
 */
public class CommandRemoveBuoys implements CommandExecutor {

    protected BuoyMain plugin;
    
    public CommandRemoveBuoys(BuoyMain aPlugin) {
        plugin = aPlugin;
    }
    
    @Override
    public boolean onCommand(CommandSender aCommandSender, Command aCommand, String aString, String[] aStrings) {
        if (aCommandSender instanceof Player) {
          Player lPlayer = (Player) aCommandSender;
          String lPlayerName = lPlayer.getName();
          if (aStrings.length > 0) {
              lPlayerName = aStrings[0];
          }
          WaterPathDB lDB = plugin.getWaterPathDB(lPlayer.getWorld().getName());
          int lCount = 0;
          ArrayList<WaterPathItem> lToRemove = new ArrayList<WaterPathItem>();
          for(WaterPathItem lItem : lDB) {
              if (lItem.player.equals(lPlayerName)) {
                  lToRemove.add(lItem);
                  lCount++;
              }
          }
          for(WaterPathItem lItem : lToRemove) {
              lDB.remove(lItem);
          }
          lPlayer.sendMessage(new Integer(lCount) + " are removed.");
        }
        return true;
    }
    
}
