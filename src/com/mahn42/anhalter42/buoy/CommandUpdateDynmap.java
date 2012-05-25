/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.buoy;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author andre
 */
public class CommandUpdateDynmap implements CommandExecutor {

    protected BuoyMain plugin;
    
    public CommandUpdateDynmap(BuoyMain aPlugin) {
        plugin = aPlugin;
    }

    @Override
    public boolean onCommand(CommandSender aCommandSender, Command aCommand, String aString, String[] aStrings) {
        if (aStrings.length > 0) {
            if (aStrings[0].equalsIgnoreCase("update")) {
                plugin.updateDynMapBuoy();
            }
        }
        return true;
    }
}
