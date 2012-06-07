/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.buoy;

import org.bukkit.block.Block;

/**
 *
 * @author andre
 */
class TriggerLever implements Runnable {

    protected Block fBlock;
    
    public TriggerLever(Block aBlock) {
        fBlock = aBlock;
    }

    @Override
    public void run() {
        fBlock.setData((byte)(fBlock.getData() ^ 0x08));
    }
    
}
