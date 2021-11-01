package uk.ac.ed.inf;

import java.util.ArrayList;

/**
 * Hello world!
 *
 */
public class App
{
    public static void main( String[] args )
    {
        ArrayList<Order> orders = Database.readOrders("01/10/2022");
        for(Order order:orders) {
            System.out.println(order.toString());
        }
    }
}
