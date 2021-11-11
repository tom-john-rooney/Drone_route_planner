package uk.ac.ed.inf;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mapbox.geojson.*;
import java.util.ArrayList;

/**
 * The methods in the class handle different kinds of GeoJSON parsing using Mapbox's
 * GeoJSON library.
 */
public class GeoJsonParsing {

    private final static String LOCATION_PROPERTY = "location";

    /** Default constructor to prevent instantiation */
    private GeoJsonParsing(){}

    /**
     * Takes an input GeoJSON string whose FeatureCollection should only contain Features whose
     * Geometry type is Polygon. This string is parsed to an ArrayList of Polygon objects.
     *
     * @param geoJsonPolygonStr the string to be parsed
     * @return an ArrayList of Polygon objects
     */
    public static ArrayList<Polygon> parsePolygons(String geoJsonPolygonStr){
        FeatureCollection fc = FeatureCollection.fromJson(geoJsonPolygonStr);
        ArrayList<Polygon> polygonLst = new ArrayList<>();
        for(Feature f : fc.features()){
            Geometry g = f.geometry();
            if(g instanceof Polygon){
                polygonLst.add((Polygon)g);
            }else{
                System.err.println("Fatal error in parsePolygons: File contained a non-polygon Feature.");
                System.exit(1);
            }
        }
        return polygonLst;
    }

    /**
     * Takes an input GeoJSON string whose FeatureCollection should only contain Features whose
     * properties include a field called 'location'. This string is parsed to an ArrayList of
     * what3words address strings.
     *
     * @param geoJsonPointStr the string to be parsed
     * @return an ArrayList of what3words address strings
     */
    public static ArrayList<String> parsePointsToW3w(String geoJsonPointStr){
        FeatureCollection fc = FeatureCollection.fromJson(geoJsonPointStr);
        ArrayList<String> w3wAddressLst = new ArrayList<>();
        for(Feature f : fc.features()){
            if(f.properties().has(LOCATION_PROPERTY)){
                w3wAddressLst.add(f.getStringProperty(LOCATION_PROPERTY));
            }
        }
        return w3wAddressLst;
    }
}
