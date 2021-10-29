package uk.ac.ed.inf;

public class Order {
    public final String id;
    public final String customerId;
    public final String deliveryLoc;

    public Order(String id, String customerId, String deliveryLoc){
        this.id = id;
        this.customerId = customerId;
        this.deliveryLoc = deliveryLoc;
    }

    @Override
    public String toString() {
        return String.format(this.id + " " + this.customerId + " " + this.deliveryLoc);
    }
}
