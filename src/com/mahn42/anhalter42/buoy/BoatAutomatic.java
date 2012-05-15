/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.buoy;

import java.util.HashMap;
import org.bukkit.entity.Boat;
import org.bukkit.util.Vector;

/**
 *
 * @author andre
 */
public class BoatAutomatic implements Runnable {

    protected class BoatAutomaticMovement {
        boolean active;
        Boat boat;
        Vector velocity;
    }
    protected BuoyMain plugin;
    protected HashMap<Boat, BoatAutomaticMovement> fMovements;
    
    public BoatAutomatic(BuoyMain aPlugin) {
        fMovements = new HashMap<Boat, BoatAutomaticMovement>();
        plugin = aPlugin;
    }
    
    @Override
    public void run() {
        for(BoatAutomaticMovement lMovement : fMovements.values()) {
            if (lMovement.active) {
                lMovement.boat.setVelocity(lMovement.velocity);
            }
        }
    }
    
    public void setMovement(Boat aBoat, Vector aVelocity) {
        BoatAutomaticMovement lMovement;
        if (!fMovements.containsKey(aBoat)) {
            lMovement = new BoatAutomaticMovement();
            lMovement.boat = aBoat;
            fMovements.put(aBoat, lMovement);
        } else {
            lMovement = fMovements.get(aBoat);
        }
        lMovement.active = true;
        lMovement.velocity = aVelocity;
    }
    
    public void deactivateMovement(Boat aBoat) {
        fMovements.remove(aBoat);
    }
    
}
