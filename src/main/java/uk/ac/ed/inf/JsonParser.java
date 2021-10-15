package uk.ac.ed.inf;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * The methods in this class all handle different kinds of JSON parsing.
 */
public class JsonParser {

    /** Default constructor to prevent instantiation */
    private JsonParser() {}

    /**
     * Takes an input JSON list string and parses this to an ArrayList of objects
     * of an unspecified type.
     *
     * @param jsonListString the string containing the content to be parsed
     * @return an ArrayList of objects of an unspecified type
     */
    public static ArrayList<?> parseJsonList(String jsonListString) {
        Type listType = new TypeToken<ArrayList<Shop>>() {}.getType();
        ArrayList<?> parsedList = new Gson().fromJson(jsonListString, listType);
        return parsedList;
    }
}
