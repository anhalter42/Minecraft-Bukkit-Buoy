/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.buoy;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author andre
 */
public class DBSet<T extends DBRecord> implements Iterable<T> {
    protected File fStore;
    protected ArrayList<T> fRecords = new ArrayList<T>();
    protected HashMap<String, T> fKeyIndex = new HashMap();
    protected Class<T> fRecordClass;
    
    public DBSet(Class<T> aRecordClass) {
        fRecordClass = aRecordClass;
    }
    
    public DBSet(Class<T> aRecordClass, File aStore) {
        fRecordClass = aRecordClass;
        fStore = aStore;
    }
    
    public void load() {
        fRecords.clear();
        fKeyIndex.clear();
        if (fStore.exists()) {
            try {
                BufferedReader lReader = new BufferedReader(new FileReader(fStore));
                String lLine;
                String lHeader = lReader.readLine();
                while ((lLine = lReader.readLine()) != null) {
                    T lRecord = null;
                    try {
                        lRecord = fRecordClass.newInstance();
                    } catch (InstantiationException ex) {
                        Logger.getLogger(DBSet.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IllegalAccessException ex) {
                        Logger.getLogger(DBSet.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    if (lRecord != null) {
                        lRecord.fromCSV(lHeader, lLine);
                        addRecordInternal(lRecord);
                    }
                }
            } catch (IOException ex) {
                getLogger().log(Level.SEVERE, null, ex);
            }            
        }
    }
    
    public void save() {
        if (fStore.exists()) {
            fStore.delete();
        }
        try {
            BufferedWriter lWriter = new BufferedWriter(new FileWriter(fStore));
            lWriter.write("CSV:1.0");
            lWriter.newLine();
            for (T lRecord : fRecords) {
                lWriter.write(lRecord.toCSV());
                lWriter.newLine();
            }
            lWriter.close();
        } catch (IOException ex) {
            getLogger().log(Level.SEVERE, null, ex);
        }
    }
    
    public T getRecord(int aIndex) {
        return (T)fRecords.get(aIndex);
    }
    
    public T getRecord(String aKey) {
        return (T)fKeyIndex.get(aKey);
    }
    
    public void addRecord(T aRecord) {
        addRecordInternal(aRecord);
    }
    
    protected void addRecordInternal(T aRecord) {
        if (aRecord != null) {
            fRecords.add(aRecord);
            fKeyIndex.put(aRecord.key, aRecord);
        }
    }
    
    protected void removedRecord(T aRecord) {
    }
    
    public void remove(T aRecord) {
        if (!fRecords.remove(aRecord))
            getLogger().info("remove: not found");
        if (fKeyIndex.remove(aRecord.key) == null)
            getLogger().info("remove: index not found");
        removedRecord(aRecord);
    }
    
    public void remove(int aIndex) {
        remove(fRecords.get(aIndex));
    }

    public void remove(String aKey) {
        remove(fKeyIndex.get(aKey));
    }

    public int size() {
        return fRecords.size();
    }
    
    protected Logger getLogger() {
        return Logger.getLogger(getClass().getSimpleName());
    }
    
    @Override
    public Iterator<T> iterator() {
        return fRecords.iterator();
    }
}
