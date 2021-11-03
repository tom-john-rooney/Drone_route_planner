package uk.ac.ed.inf;

import com.mapbox.geojson.*;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * The methods in the class handle different kinds of GeoJSON parsing using Mapbox's
 * GeoJSON library.
 */
public class GeoJsonParsing {

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
     * Geometry type is Point. This string is parsed to an ArrayList of Point objects.
     *
     * @param geoJsonPointStr the string to be parsed
     * @return an ArrayList of Point objects
     */
    public static ArrayList<Point> parsePoints(String geoJsonPointStr){
        FeatureCollection fc = FeatureCollection.fromJson(geoJsonPointStr);
        ArrayList<Point> pointLst = new ArrayList<>();
        for(Feature f : fc.features()){
            Geometry g = f.geometry();
            if(g instanceof Point){
                pointLst.add((Point)g);
            }else{
                System.err.println("Fatal error in parsePoints: File contained a non-point Feature.");
                System.exit(1);
            }
        }
        return pointLst;
    }
}
