package uk.ac.ed.inf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Represents an order placed by a customer
 */
public class Order {
    /** The ID of the order */
    public final String id;
    /** The student number of the student who placed the order of format s1234567 */
    public final String customerId;
    /** A what3words to which the order must be delivered. For more details of what3words: https://what3words.com/about/ */
    public final String deliveryLoc;
    /** The items ordered by the customer */
    public final ArrayList<String> contents;
    /** The total value of the order including the delivery fee */
    public final int value;

    /**
     * Constructor to initialise a new Order instance
     *
     * @param id the ID of the order
     * @param customerId the student number of the student who placed the order
     * @param deliveryLoc the location to which the delivery is to be made in the form of a what3words address
     */
    public Order(String id, String customerId, String deliveryLoc, ArrayList<String> contents, int value){
        this.id = id;
        this.customerId = customerId;
        this.deliveryLoc = deliveryLoc;
        this.contents = contents;
        this.value = value;
    }

    /**
     * REMOVE BEFORE SUBMISSION
     */
    @Override
    public String toString() {
        String contentsString = String.join(", ", this.contents);
        return String.format(this.id + " " + this.customerId + " " + this.deliveryLoc + " " + contentsString + " " + this.value);
    }

    /**
     * Sorts an ArrayList of order objects in descending order by their value
     *
     * @param orders the ArrayList of orders to be sorted
     */
    public static void sortByValue(ArrayList<Order> orders){
        Collections.sort(orders, Comparator.comparingInt((Order o) -> o.value).reversed());
    }
}
