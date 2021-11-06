package uk.ac.ed.inf;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Hello world!
 *
 */
public class App
{
    public static void main( String[] args )
    {
        System.out.println("Please enter the date in format dd/mm/yyyy: ");
        Scanner in = new Scanner(System.in);
        String input_str = in.nextLine();
        Menus menus = new Menus("localhost", "80");
        WhatThreeWords w3w = new WhatThreeWords("localhost","80");

        ArrayList<Order> orders = Database.readOrders(input_str);
        Order.sortByValue(orders);

        ArrayList<Shop> shops = menus.getShopsWithMenus();

        System.out.println(w3w.getDetailsFromServer("army.monks.grapes").toString());
    }
}
