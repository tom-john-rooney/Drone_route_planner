package uk.ac.ed.inf;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the Drone and its movements.
 */
public class Drone {
    /** The what3words address of Appleton tower */
    public static final String AT_W3W_ADDR = "nests.takes.print";
    /** The total number of moves the drone can make when fully charged */
    private final int STARTING_MOVES_TOTAL = 1500;

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
    /** The number of moves the drone has the battery power left to make */
    private int numMoves;

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
        this.numMoves = STARTING_MOVES_TOTAL;
    }

    // NEED TO FINISH AND DOCUMENT
    public ArrayList<What3WordsLoc.LongLat> makeDelivery(ArrayList<String> pickUpLocs, String deliveryLoc){
        System.out.println(String.format("Moves before delivery: %d", this.numMoves));

        // Keeps record of original location of drone in case there is not enough battery
        String originalAddr = this.w3wAddress;
        What3WordsLoc.LongLat originalPos = this.position;

        ArrayList<What3WordsLoc.LongLat> deliveryPathLocs = new ArrayList<>();
        System.out.println("DELIVERY:");
        System.out.println("  Shops:");
        for(String loc : pickUpLocs){
            System.out.println("    " + loc);
        }
        System.out.println(String.format("  Delivery: %s", deliveryLoc));
        List<List<String>> w3wPath = lg.getW3wPathFromGraph(this.w3wAddress, pickUpLocs, deliveryLoc);
        What3WordsLoc.LongLat locOfDeliv = w3w.getLocOfAddr(deliveryLoc).coordinates;

        int movesToReturn = locOfDeliv.getPathTo(What3WordsLoc.LongLat.AT_LOC, this.zones).size();

        for(List<String> subPath : w3wPath){
            ArrayList<What3WordsLoc.LongLat> subPathLocs = getPoints(subPath);
            deliveryPathLocs.addAll(subPathLocs);
        }

        if(!(enoughBattery(movesToReturn, deliveryPathLocs.size()))){
            System.out.println("Not enough battery for this order!\n");
            this.w3wAddress = originalAddr;
            this.position = originalPos;
            return new ArrayList<>();
        }

        System.out.println("Delivery complete!\n");
        this.numMoves = this.numMoves - (movesToReturn + deliveryPathLocs.size());
        System.out.println(String.format("Moves remaining: %s", this.numMoves));
        return deliveryPathLocs;
    }

    private boolean enoughBattery(int movesToAT, int deliveryMoves){
        if(movesToAT + deliveryMoves > this.numMoves){
            return false;
        }
        return true;
    }

    public ArrayList<What3WordsLoc.LongLat> returnToBase(){
        ArrayList<What3WordsLoc.LongLat> movesToBase = this.position.getPathTo(What3WordsLoc.LongLat.AT_LOC, this.zones);
        for(What3WordsLoc.LongLat loc: movesToBase){
            this.position = loc;
        }
        this.numMoves = this.numMoves - movesToBase.size();
        this.w3wAddress = AT_W3W_ADDR;
        System.out.println(String.format("Moves remaining at completion: %d", this.numMoves));
        return movesToBase;
    }

    private ArrayList<What3WordsLoc.LongLat> getPoints(List<String> subPath){
        ArrayList<What3WordsLoc.LongLat> pointsToVisit = new ArrayList<>();
        System.out.println(String.format("Start loc: %s", subPath.get(0)));
        // the drone is already here
        subPath.remove(0);
        for(String w3wAddr : subPath){
            What3WordsLoc.LongLat addrPoint= w3w.getLocOfAddr(w3wAddr).coordinates;
            ArrayList<What3WordsLoc.LongLat> pointsBetweenAddrs = this.position.getPathTo(addrPoint, this.zones);
            System.out.println("Subpath locations:");
            for(What3WordsLoc.LongLat loc: pointsBetweenAddrs){
                System.out.println(loc.toString());
            }
            pointsToVisit.addAll(pointsBetweenAddrs);
            for(What3WordsLoc.LongLat loc: pointsBetweenAddrs){
                this.position = loc;
            }
            this.w3wAddress = w3wAddr;
            System.out.println(String.format("Drone now at: %s", this.w3wAddress));
        }
        System.out.println("Sub path traversed!");
        return pointsToVisit;
    }
}
