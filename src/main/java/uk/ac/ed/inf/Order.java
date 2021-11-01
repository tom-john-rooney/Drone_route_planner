package uk.ac.ed.inf;

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

    /**
     * Constructor to initialise a new Order instance
     *
     * @param id the ID of the order
     * @param customerId the student number of the student who placed the order
     * @param deliveryLoc the location to which the delivery is to be made in the form of a what3words address
     */
    public Order(String id, String customerId, String deliveryLoc){
        this.id = id;
        this.customerId = customerId;
        this.deliveryLoc = deliveryLoc;
    }

    /**
     * REMOVE BEFORE SUBMISSION
     */
    @Override
    public String toString() {
        return String.format(this.id + " " + this.customerId + " " + this.deliveryLoc);
    }
}
