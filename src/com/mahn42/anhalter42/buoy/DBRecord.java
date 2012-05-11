/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.buoy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

/**
 *
 * @author andre
 */
public class DBRecord {

    public String key;
    
    public DBRecord() {
        init();
    }
    
    protected void init() {
        key = UUID.randomUUID().toString();
    }
    
    public String toCSV() {
        ArrayList lCols = new ArrayList();
        String lLine = null;
        toCSVInternal(lCols);
        for(Object lObject : lCols) {
            String lCol = (String) lObject.toString();
            if (lLine == null) {
                lLine = lCol;
            } else {
                lLine = lLine + ";" + lCol;
            }
        }
        return lLine;
    }
    
    protected void toCSVInternal(ArrayList aCols) {
        aCols.add(key);
    }
    
    public void fromCSV(String aHeader, String aLine) {
        String[] lArray = aLine.split(aLine);
        ArrayList lCols = new ArrayList();
        lCols.addAll(Arrays.asList(lArray));
        fromCSVInternal(lCols);
    }
    
    protected void fromCSVInternal(ArrayList aCols) {
        key = (String) aCols.get(0);
        aCols.remove(0);
    }

    public boolean isSameRecord(DBRecord aRecord) {
        return (aRecord.key == null ? key == null : aRecord.key.equals(key));
    }
    
    @Override
    public boolean equals(Object aObject) {
        if (aObject instanceof DBRecord) {
            DBRecord lItem = (DBRecord) aObject;
            return lItem.key.equals(key);
        } else {
            return false;
        }
    }
    
    @Override
    public String toString() {
        return getClass().getName() + ":" + key;
    }
    
}
