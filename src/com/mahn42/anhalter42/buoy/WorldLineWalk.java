/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.buoy;

import java.util.Iterator;
import org.bukkit.Location;

/**
 *
 * @author andre
 */
public class WorldLineWalk implements Iterable<BlockPosition> {
    protected BlockPosition fStart; 
    protected BlockPosition fEnd;

    public WorldLineWalk(BlockPosition aStart, BlockPosition aEnd) {
        fStart = aStart;
        fEnd = aEnd;
    }
    
    public WorldLineWalk(Location aStart, Location aEnd) {
        fStart = new BlockPosition(aStart);
        fEnd = new BlockPosition(aEnd);
    }

    @Override
    public Iterator<BlockPosition> iterator() {
        return new WorldLineWalkIter(fStart, fEnd);
    }

    
    protected class WorldLineWalkIter implements Iterator<BlockPosition> {
        protected BlockPosition fStart; 
        protected BlockPosition fEnd;
        protected BlockPosition fCurrent;


        protected int dx;
        protected int dy;
        protected int dz;

        protected int ax;
        protected int ay;
        protected int az;

        protected int sx;
        protected int sy;
        protected int sz;

        protected int idx = 1;
        
        protected int xd;
        protected int yd;
        protected int zd;
        
        protected int dominant; // 0=x, 1=y, 2=z
        protected boolean end;
        
        public WorldLineWalkIter(BlockPosition aStart, BlockPosition aEnd) {
            fStart = aStart;
            fEnd = aEnd;
            fCurrent = new BlockPosition(aStart);
            init();
        }

        private int sign(int x) {
            return (x > 0) ? 1 : (x < 0) ? -1 : 0;
        }
        
        private void init() {
            dx = fEnd.x - fStart.x;
            dy = fEnd.y - fStart.y;
            dz = fEnd.z - fStart.z;

            ax = Math.abs(dx)*2;
            ay = Math.abs(dy)*2;
            az = Math.abs(dz)*2;

            sx = sign(dx);
            sy = sign(dy);
            sz = sign(dz);

            idx = 1;
            if(ax >= Math.max(ay,az)) {	// x dominant
                yd = ay - ax/2;
                zd = az - ax/2;
                dominant = 0;
            } else if (ay>= Math.max(ax,az)) { // y dominant
                xd = ax - ay/2;
                zd = az - ay/2;
                dominant = 1;
            } else if (az>= Math.max(ax,ay)) { // z dominant
                xd = ax - az/2;
                yd = ay - az/2;
                dominant = 2;
            }
            end = false;
        }
                
        @Override
        public boolean hasNext() {
            return !end;
        }

        @Override
        public BlockPosition next() {
            BlockPosition lResult = new BlockPosition(fCurrent);
            switch (dominant) {
                case 0: // x
                    idx = idx + 1;
                    if (fCurrent.x == fEnd.x) { // end
                        end = true;
                    } else {
                        if (yd >= 0) { // move along y
                            fCurrent.y = fCurrent.y + sy;
                            yd = yd - ax;
                        }
                        if (zd >= 0) { // move along z
                            fCurrent.z = fCurrent.z + sz;
                            zd = zd - ax;
                        }
                        fCurrent.x  = fCurrent.x  + sx; // move along x
                        yd = yd + ay;
                        zd = zd + az;
                    }
                    break;
                case 1: // y
                    idx = idx + 1;
                    if (fCurrent.y == fEnd.y) { // end
                        end = true;
                    } else {
                        if (xd >= 0) { // move along x
                            fCurrent.x = fCurrent.x + sx;
                            xd = xd - ay;
                        }
                        if (zd >= 0) { // move along z
                            fCurrent.z = fCurrent.z + sz;
                            zd = zd - ay;
                        }
                        fCurrent.y  = fCurrent.y  + sy; // move along y
                        xd = xd + ax;
                        zd = zd + az;
                    }
                    break;
                case 2: // z
                    idx = idx + 1;
                    if (fCurrent.z == fEnd.z) { // end
                        end = true;
                    } else {
                        if (xd >= 0) { // move along x
                            fCurrent.x = fCurrent.x + sx;
                            xd = xd - az;
                        }
                        if (yd >= 0) { // move along y
                            fCurrent.y = fCurrent.y + sy;
                            yd = yd - az;
                        }
                        fCurrent.z  = fCurrent.z  + sz; // move along z
                        xd = xd + ax;
                        yd = yd + ay;
                    }
                    break;
            }
            return lResult;        
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }
}
