package uk.ac.ed.inf;

import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;
import com.mapbox.turf.TurfJoins;

import javax.sound.sampled.Line;
import java.awt.geom.Line2D;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the no-fly-zones stored on the web server.
 */
public class NoFlyZones {
    /** The directory on the web server at which the details of the no-fly-zones are stored */
    private final String ZONES_DIR = "/buildings/no-fly-zones.geojson";
    /** Factor used to approximate the line in lineIntersectsZones */
    private final int LNG_IND = 0;
    private final int LAT_IND = 1;

    /** One geojson.Polygon object to represent each no-fly-zone */
    private ArrayList<Polygon> zones = new ArrayList<>();
    /** The machine on which the web server is hosted */
    public final String machine;
    /** The on the web server to which a connection needs to be made */
    public final String port;

    /**
     * Constructor to initialise a new NoFlyZones instance.
     *
     * @param machine the machine on which the web server is hosted
     * @param port the port on the server to which a connection needs to be made
     */
    public NoFlyZones(String machine, String port){
        this.machine = machine;
        this.port = port;
    }

    /**
     * Gets the no-fly-zones from the web server and stores them in the zones field.
     */
    public void getZones(){
        if(this.zones.isEmpty()) {
            String url = WebServer.buildURL(this.machine, this.port, this.ZONES_DIR);
            String geoJsonStr = WebServer.getFrom(url);
            this.zones = GeoJsonParsing.parseNoFlyZones(geoJsonStr);
        }
    }

    /**
     * Checks if a line between 2 What3WordsLoc.LongLat points goes through any of the no-
     * fly-zones.
     *
     * @param from the start point of the line
     * @param to the end point of the line
     * @return true if the line intersects any of the no-fly-zones, false otherwise.
     */
    public boolean lineIntersectsZones(What3WordsLoc.LongLat from, What3WordsLoc.LongLat to){
        Line2D line = new Line2D.Double(from.lng, from.lat, to.lng, to.lat);
        for(Polygon zone : zones){
            if(lineIntersectsPolygon(line, zone)){
                return true;
            }
        }return false;
    }

    /**
     * Checks if a line intersects a Polygon.
     *
     * @param line the line which may or may not intersect the Polygon
     * @param p the Polygon with which intersection between the line is being checked.
     * @return true if the line intersects the Polygon, false otherwise.
     */
    public boolean lineIntersectsPolygon(Line2D line, Polygon p){
        List<List<Point>> edges = p.coordinates();
        for(List<Point> edge : edges){
            if(lineIntersectsEdge(line, edge)){
                return true;
            }
        }return false;
    }

    /**
     * Checks if a line intersects an edge of a Polygon.
     *
     * @param line the line which may or may not intersect the edge of the Polygon
     * @param edge the edge of a Polygon with which intersection between the line is being checked.
     * @return true if the line intersects the edge, false otherwise.
     */
    public boolean lineIntersectsEdge(Line2D line, List<Point> edge){
        ArrayList<Line2D> edgeLines = getEdgeLines(edge);
        for(Line2D edgeLine : edgeLines){
            if(lineIntersectsEdgeLine(line, edgeLine)){
                return true;
            }
        }return false;

    }

    /**
     * Checks if a line intersects a line making up an edge of a Polygon.
     *
     * @param line the line which may or may not intersect the edge line
     * @param edgeLine the line of an edge of a Polygon with which intersection between the line is being checked.
     * @return true if the line intersects the edge line, false otherwise.
     */
    public boolean lineIntersectsEdgeLine(Line2D line, Line2D edgeLine){
        return line.intersectsLine(edgeLine);
    }

    /**
     * Gets all the lines making up an edge of a Polygon.
     *
     * @param edge the edge whose lines are to be returned
     * @return the lines making up the edge
     */
    public ArrayList<Line2D> getEdgeLines(List<Point> edge){
        ArrayList<Line2D> edgeLines = new ArrayList<Line2D>();
        for(int i = 0; i < edge.size()-1; i++){
            List<Double> startCoords = edge.get(i).coordinates();
            List<Double> endCoords = edge.get(i+1).coordinates();
            Line2D edgeLine = new Line2D.Double(
                    startCoords.get(LNG_IND), startCoords.get(LAT_IND),
                    endCoords.get(LNG_IND), endCoords.get(LAT_IND));
            edgeLines.add(edgeLine);
        }
        return edgeLines;
    }

    /**
     * Checks if a What3Words.LongLat object lies in any of the no-fly-zones.
     *
     * @param point the point to be checked
     * @return true if loc lies in any of the no-fly-zones stored, false otherwise
     */
    public boolean pointInZones(What3WordsLoc.LongLat point){
        for(Polygon zone : this.zones){
            if(pointInPolygon(point, zone)){
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a What3Words.LongLat point lies in a particular no-fly-zone
     * @param point the point to be checked
     * @param zone the no-fly-zone in which loc may or may not lie
     * @return true if loc is inside zone, false otherwise
     */
    private static boolean pointInPolygon(What3WordsLoc.LongLat point, Polygon zone){
        Point p = Point.fromLngLat(point.lng, point.lat);
        return TurfJoins.inside(p, zone);
    }
}
