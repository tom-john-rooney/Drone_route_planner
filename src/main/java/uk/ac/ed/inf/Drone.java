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

    /**
     * Instructs the drone to make a delivery
     *
     * Takes the locations from which items are to be collected and the delivery location selected by the user,
     * finds the shortest path from the drone's current location to the delivery point, with stops at each shop,
     * and moves the drone accordingly. The drone is 'moved' by updating its w3wAddress and position fields. If the
     * drone doesn't have enough battery to complete the delivery and return to base, its position will not be updated
     * and the delivery not made.
     *
     * @param pickUpLocs the w3w addresses of shops at which item in the order being delivered are kept
     * @param deliveryLoc the w3w address of the customer's selected delivery location
     * @return an ArrayList of What3Words.LongLat objects; may be empty if the drone doesn't have enough
     *         battery to complete the order and return to base
     */
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
        // Shortest path for drone to follow from its w3wAddress to deliveryLoc, in w3w addresses.
        List<List<String>> w3wPath = lg.getW3wPathFromGraph(this.w3wAddress, pickUpLocs, deliveryLoc);
        What3WordsLoc.LongLat delivPoint = w3w.getLocOfAddr(deliveryLoc).coordinates;

        // A sub-path is from one parameter location to the next e.g. from shop to delivery.
        // Direct path may not exist; may need to visit addresses along the way (hence use of List).
        for(List<String> subPath : w3wPath){
            ArrayList<What3WordsLoc.LongLat> subPathPoints= getPoints(subPath);
            deliveryPathLocs.addAll(subPathPoints);
        }

        int movesToBase = delivPoint.getPathTo(What3WordsLoc.LongLat.AT_LOC, this.zones).size();
        if(!(enoughBattery(movesToBase, deliveryPathLocs.size()))){
            System.out.println("Not enough battery for this order!\n");
            this.w3wAddress = originalAddr;
            this.position = originalPos;
            return new ArrayList<>();
        }

        System.out.println("Delivery complete!\n");
        this.numMoves = this.numMoves - (movesToBase + deliveryPathLocs.size());
        System.out.println(String.format("Moves remaining: %s", this.numMoves));
        return deliveryPathLocs;
    }

    /**
     * Checks if the drone has enough battery to complete the delivery of an order and return to base after.
     *
     * @param movesToBase the number of moves required to return the drone to being 'close to' its base after
     *                    completing the delivery in question
     * @param deliveryMoves the number of moves required to complete the delivery
     * @return true if the drone has sufficient moves to complete the delivery and return to base, false otherwise
     */
    private boolean enoughBattery(int movesToBase, int deliveryMoves){
        if(movesToBase + deliveryMoves > this.numMoves){
            return false;
        }
        return true;
    }

    /**
     * Returns the drone to its base of operations from its current location.
     *
     * @return the points the drone must visit along the path back to its base
     */
    public ArrayList<What3WordsLoc.LongLat> returnToBase(){
        List<String> pathToBase = lg.getShortestPath(this.w3wAddress, AT_W3W_ADDR);
        ArrayList<What3WordsLoc.LongLat> movesToBase = getPoints(pathToBase);
        this.numMoves = this.numMoves - movesToBase.size();
        this.w3wAddress = AT_W3W_ADDR;
        System.out.println(String.format("Moves remaining at completion: %d", this.numMoves));
        return movesToBase;
    }

    /**
     * Gets the points for the drone to visit in order to traverse a 'sub-path'.
     *
     * Note: a 'sub-path' is a path between two of the w3w addresses a drone must visit to
     *       complete a delivery e.g. between two shops or between a shop and the delivery location
     *       etc. The full path the drone must follow to deliver an order will consist of several of these
     *       sub-paths.
     *
     *       A 'sub-path' also just be a simple path i.e., a path with only a start and an end; no stops
     *       along the way. An example could be the drone's path as it returns to base from its current
     *       w3w address.
     *
     * @param subPath the 'sub-path' the drone is to follow, in the format of a List of w3w addresses
     * @return an ArrayList of What3WordsLoc.LongLat instances, representing the points in space the drone
     *         must visit, in order from start to end, to traverse the sub-path
     */
    private ArrayList<What3WordsLoc.LongLat> getPoints(List<String> subPath){
        ArrayList<What3WordsLoc.LongLat> pointsToVisit = new ArrayList<>();
        System.out.println(String.format("Start loc: %s", subPath.get(0)));
        // The drone is already here.
        subPath.remove(0);
        for(String w3wAddr : subPath){
            What3WordsLoc.LongLat addrPoint= w3w.getLocOfAddr(w3wAddr).coordinates;
            ArrayList<What3WordsLoc.LongLat> pointsBetweenAddrs = moveBetweenPoints(this.position, addrPoint);
            pointsToVisit.addAll(pointsBetweenAddrs);
            System.out.println("Subpath locations:");
            for(What3WordsLoc.LongLat loc: pointsBetweenAddrs){
                System.out.println(loc.toString());
            }
            this.w3wAddress = w3wAddr;
            System.out.println(String.format("Drone now at: %s", this.w3wAddress));
        }
        System.out.println("Sub path traversed!");
        return pointsToVisit;
    }

    /**
     * Moves the drone between two points in space, updating its position as it moves.
     *
     * @param start the point at which the movement of the drone is to start
     * @param end the destination of the drone
     * @return an ArrayList of What3WordsLoc.LongLat instances, each representing a point in space the
     *         drone must visit as it moves from start to end
     */
    private ArrayList<What3WordsLoc.LongLat> moveBetweenPoints(What3WordsLoc.LongLat start, What3WordsLoc.LongLat end){
        ArrayList<What3WordsLoc.LongLat> pointsToVisit = start.getPathTo(end, this.zones);
        for(What3WordsLoc.LongLat point: pointsToVisit){
            this.position = point;
        }
        return pointsToVisit;
    }
}