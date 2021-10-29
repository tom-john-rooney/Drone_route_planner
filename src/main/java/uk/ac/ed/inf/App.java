package uk.ac.ed.inf;

import javax.xml.crypto.Data;
import java.time.LocalDate;
import java.sql.Date;
import java.util.ArrayList;

/**
 * Hello world!
 *
 */
public class App
{
    public static void main( String[] args )
    {
        ArrayList<Order> orders = Database.readOrders("01/01/2022");
        for(Order order:orders) {
            System.out.println(order.toString());
        }
    }
}
