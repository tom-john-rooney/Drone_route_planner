package uk.ac.ed.inf;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;


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

    public void getDeliveryCost(String... items){
        ArrayList<ArrayList<Shop.Item>> menus = getMenus();
        alphabeticalSortMenus(menus);
    }

    public ArrayList<ArrayList<Shop.Item>> getMenus(){
        WebServerIO server = new WebServerIO();
        String url = server.buildURL(this.machine, this.port, MENUS_URL);
        ArrayList<Shop> shops = parseShops(server.getFrom(url));
        return compileMenus(shops);
    }

    public ArrayList<ArrayList<Shop.Item>> compileMenus(ArrayList<Shop> shops){
        ArrayList<ArrayList<Shop.Item>> menus = new ArrayList<ArrayList<Shop.Item>>();
        for(Shop shop : shops){
            menus.add(shop.menu);
        }
        return menus;
    }

    public ArrayList<Shop> parseShops(String jsonListString){
        Type listType = new TypeToken<ArrayList<Shop>>() {}.getType();
        ArrayList<Shop> shopList = new Gson().fromJson(jsonListString, listType);
        return shopList;
    }

    public void alphabeticalSortMenus(ArrayList<ArrayList<Shop.Item>> menus){
        for(ArrayList<Shop.Item> menu : menus) {
            Collections.sort(menu, (itemOne, itemTwo) -> itemOne.item.compareTo(itemTwo.item));
        }
    }
}
