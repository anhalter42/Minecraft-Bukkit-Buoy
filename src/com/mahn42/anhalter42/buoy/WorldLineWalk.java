/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.buoy;

import java.util.Iterator;
import java.util.logging.Logger;
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

        //protected int x;
        //protected int y;
        //protected int z;
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

            //x = fStart.x;
            //y = fStart.y;
            //z = fStart.z;
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
                
    /*
    public static void line3(int x1, int y1, int z1, int x2, int y2, int z2) {
        int dx = x2 - x1;
        int dy = y2 - y1;
        int dz = z2 - z1;

        int ax = Math.abs(dx)*2;
        int ay = Math.abs(dy)*2;
        int az = Math.abs(dz)*2;

        int sx = sign(dx);
        int sy = sign(dy);
        int sz = sign(dz);

        int x = x1;
        int y = y1;
        int z = z1;
        int idx = 1;

        if(ax >= Math.max(ay,az)) {	//		% x dominant
            int yd = ay - ax/2;
            int zd = az - ax/2;

            while(true) {
                Logger.getLogger("xyz").info(new Integer(x).toString()+","+new Integer(y).toString()+","+new Integer(z).toString());
                //X(idx) = x;
                //Y(idx) = y;
                //Z(idx) = z;
                idx = idx + 1;

                if (x == x2) //% end
                    break;

                if (yd >= 0) { //% move along y
                    y = y + sy;
                    yd = yd - ax;
                }

                if (zd >= 0) { // move along z
                    z = z + sz;
                    zd = zd - ax;
                }

                x  = x  + sx; // move along x
                yd = yd + ay;
                zd = zd + az;
            }
        } else if (ay>= Math.max(ax,az)) { // y dominant
            int xd = ax - ay/2;
            int zd = az - ay/2;

            while(true) {
                Logger.getLogger("xyz").info(new Integer(x).toString()+","+new Integer(y).toString()+","+new Integer(z).toString());
                //X(idx) = x;
                //Y(idx) = y;
                //Z(idx) = z;
                idx = idx + 1;

                if (y == y2) // end
                    break;

                if (xd >= 0) { // move along x
                    x = x + sx;
                    xd = xd - ay;
                }

                if (zd >= 0) { // move along z
                    z = z + sz;
                    zd = zd - ay;
                }

                y  = y  + sy; // move along y
                xd = xd + ax;
                zd = zd + az;
            }
        } else if (az>= Math.max(ax,ay)) { // z dominant
            int xd = ax - az/2;
            int yd = ay - az/2;

            while(true) {
                Logger.getLogger("xyz").info(new Integer(x).toString()+","+new Integer(y).toString()+","+new Integer(z).toString());
                //X(idx) = x;
                //Y(idx) = y;
                //Z(idx) = z;
                idx = idx + 1;

                if (z == z2) // end
                    break;

                if (xd >= 0) { // move along x
                    x = x + sx;
                    xd = xd - az;
                }

                if (yd >= 0) { // move along y
                    y = y + sy;
                    yd = yd - az;
                }

                z  = z  + sz; // move along z
                xd = xd + ax;
                yd = yd + ay;
            }
        }
        return; // bresenh    
    }
    * 
    */

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
