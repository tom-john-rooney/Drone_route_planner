package uk.ac.ed.inf;

import java.util.ArrayList;

/**
 *
 */
public class Menus {
    public static final String MENUS_URL = "/menus/menus.json";
    public static final int STANDARD_DELIVERY_PENCE = 50;

    public final String machine;
    public final String port;

    public Menus(String machine, String port){
        this.machine = machine;
        this.port = port;
    }

    public int getDeliveryCost(String... orderItems){
        ArrayList<ArrayList<Shop.Item>> menus = getMenus();
        for(ArrayList<Shop.Item> menu : menus){
            Shop.Item.alphabeticalSort(menu);
        }
        int deliveryCost = 0;

        for(String orderItem : orderItems){
            Shop.Item searchItem = new Shop.Item(orderItem, 0);
            deliveryCost += searchItem.findItemCost(menus);
        }

        return deliveryCost + STANDARD_DELIVERY_PENCE;
    }

    private ArrayList<ArrayList<Shop.Item>> getMenus(){
        WebServer server = WebServer.getInstance();
        String url = server.buildURL(this.machine, this.port, MENUS_URL);
        ShopParser parser = ShopParser.getInstance();
        ArrayList<Shop> shops = (ArrayList<Shop>) parser.parseJson(server.getFrom(url));
        return Shop.compileMenus(shops);
    }


}
