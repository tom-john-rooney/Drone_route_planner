package uk.ac.ed.inf;

import java.util.ArrayList;

/**
 * Represents a delivery made by the drone.
 */
public class Delivery {
    /** The points visited by the drone in making the delivery */
    public final ArrayList<LongLat> pointsVisited;
    /** The order being delivered */
    public final Order orderDelivered;

    /**
     * Public constructor to instantiate a new Delivery instance.
     *
     * @param pointsVisited the points to be visited by the drone when making the delivery
     * @param orderDelivered the order being delivered
     */
    public Delivery(ArrayList<LongLat> pointsVisited, Order orderDelivered){
        this.pointsVisited = pointsVisited;
        this.orderDelivered = orderDelivered;
    }

    /**
     * Public constructor to instantiate a new 'dud' delivery.
     */
    public Delivery(){
        this.pointsVisited = new ArrayList<>();
        this.orderDelivered = new Order();
    }
}
