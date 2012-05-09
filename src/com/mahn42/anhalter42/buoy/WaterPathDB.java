/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.buoy;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author andre
 */
public class WaterPathDB implements Iterable<WaterPathItem> {
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
                String lLine;
                String lHeader = null;
                while ((lLine = lReader.readLine()) != null) {
                    if (lHeader == null) {
                        lHeader = lLine;
                    }
                    WaterPathItem lItem = new WaterPathItem(lHeader, lLine);
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
        int lIndex = 0;
        int lFoundIndex = -1;
        double lDistance = Double.MAX_VALUE;
        double lTempDistance;
        double lLastTempDistance = Double.MAX_VALUE;
        for (Object lObject : fItems) {
            WaterPathItem lItem = (WaterPathItem) lObject;
            lTempDistance = lItem.distanceSquared(aItem);
            if (lTempDistance < lDistance) {
                lDistance = lTempDistance;
                lFoundIndex = lIndex;
            } else {
                if ((lFoundIndex >= 0) && (lTempDistance > lLastTempDistance)) {
                    break;
                }
            }
            lLastTempDistance = lTempDistance;
            lIndex++;
        }
        if (lFoundIndex < 0) {
            lFoundIndex = 0;
        }
        fItems.add(lFoundIndex, aItem);
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

    /*
    public ArrayList getItemsNearlyAt(WaterPathItem aItem) {
        return getItemsNearlyAt(fItems.indexOf(aItem));
    }
    
    public ArrayList getItemsNearlyAt(int aIndex) {
        ArrayList lResults = new ArrayList();
        
        return lResults;
    }
    * 
    */

    protected class WaterPathItemIterator implements Iterator<WaterPathItem> {

        protected WaterPathDB fDB;
        protected int fIndex = 0;
        
        public WaterPathItemIterator(WaterPathDB aDB) {
            fDB = aDB;
        }
        
        @Override
        public boolean hasNext() {
            return fDB.fItems.size() > 0 && fIndex < fDB.fItems.size();
        }

        @Override
        public WaterPathItem next() {
            return fDB.getItem(fIndex++);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
    
    @Override
    public Iterator<WaterPathItem> iterator() {
        return new WaterPathItemIterator(this);
    }
}
