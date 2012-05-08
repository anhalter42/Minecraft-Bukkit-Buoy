/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.buoy;

import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author andre
 */
public class WaterPathDB {
    public File store;
    protected ArrayList fItems = new ArrayList();
    
    public WaterPathDB(File aFile) {
        store = aFile;
    }
    
    public void load() {
        fItems.clear();
        if (store.exists()) {
            try {
                BufferedReader lReader = new BufferedReader(new FileReader(store));
                String line;
                while ((line = lReader.readLine()) != null) {
                    WaterPathItem lItem = new WaterPathItem(line);
                    addItem(lItem);
                }
            } catch (IOException ex) {
                Logger.getLogger(WaterPathDB.class.getName()).log(Level.SEVERE, null, ex);
            }            
        }
    }
    
    public void save() {
        if (store.exists()) {
            store.delete();
        }
        try {
            BufferedWriter lWriter = new BufferedWriter(new FileWriter(store));
            for (Object lObject : fItems) {
                WaterPathItem lItem = (WaterPathItem) lObject;
                lWriter.write(lItem.toCSV());
                lWriter.newLine();
            }
            lWriter.close();
        } catch (IOException ex) {
            Logger.getLogger(WaterPathDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void addItem(WaterPathItem aItem) {
        fItems.add(aItem);
    }
    
    public WaterPathItem getItem(int aIndex) {
        return (WaterPathItem) fItems.get(aIndex);
    }
    
    public WaterPathItem getItemAt(int aX, int aY, int aZ) {
        for (Object lObject : fItems) {
            WaterPathItem lItem = (WaterPathItem) lObject;
            if (lItem.red_position.isAt(aX,aY,aZ) || lItem.green_position.isAt(aX, aY, aZ)) {
                return lItem;
            }
        }
        return null;
    }
            
    public boolean contains(int aX, int aY, int aZ) {
        return getItemAt(aX, aY, aZ) != null;
    }
}
