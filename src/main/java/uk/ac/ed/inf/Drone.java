package uk.ac.ed.inf;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the Drone and its movements.
 */
public class Drone {
    /** The what3words address of Appleton tower */
    public static final String AT_W3W_ADDR = "nests.takes.print";

    /** The w3w address of the last location the drone visited */
    private String w3wAddress;
    /** The real position of the drone */
    private What3WordsLoc.LongLat position;
    /** The graph of the real world locations between which the drone is moving */
    private LocationGraph lg;
    /** Words instance whose HashMaps have been populated with details of the what3words addresses the drone can visit */
    private Words w3w;
    /** The no-fly-zones the drone must not enter */
    private NoFlyZones zones;

    /**
     * Constructor to instantiate a new Drone instance.
     *
     * @param lg a LocationGraph instance whose graph contains all the real world locations the drone can visit and the
     *           paths that exist between them
     * @param w3w a Words instance whose HashMaps have been populated with the details of the w3w locations the drone can
     *            visit
     * @param zones the no-fly-zones the drone must not enter
     */
    public Drone(LocationGraph lg, Words w3w, NoFlyZones zones){
        // Starts at Appleton Tower
        this.w3wAddress = this.AT_W3W_ADDR;
        this.position = What3WordsLoc.LongLat.AT_LOC;
        this.lg = lg;
        this.w3w = w3w;
        this.zones = zones;
    }

    // NEED TO FINISH AND DOCUMENT
    public void makeDelivery(ArrayList<String> pickUpLocs, String deliveryLoc){
        List<List<String>> w3wPath = lg.getW3wPathFromGraph(this.w3wAddress, pickUpLocs, deliveryLoc);
        for(List<String> subPath : w3wPath){
            moveAlongSubPath(subPath);
        }
    }

    private void moveAlongSubPath(List<String> subPath){
        // the drone is already here
        subPath.remove(0);
        for(String w3wAddr : subPath){
            What3WordsLoc.LongLat locOfAddr = w3w.getLocOfAddr(w3wAddr).coordinates;
            ArrayList<What3WordsLoc.LongLat> locsOnRoute = this.position.getPathTo(locOfAddr, this.zones);
            for(What3WordsLoc.LongLat loc: locsOnRoute){
                this.position = loc;
            }
            this.w3wAddress = w3wAddr;
        }
    }
}
