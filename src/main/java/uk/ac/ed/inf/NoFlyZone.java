package uk.ac.ed.inf;

import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;
import com.mapbox.turf.TurfJoins;

public class NoFlyZone {
    public final Polygon zone;

    public NoFlyZone(Polygon zone){
        this.zone = zone;
    }

    public boolean pointInPolygon(What3WordsLoc loc){
        Point p = Point.fromLngLat(loc.coordinates.lng, loc.coordinates.lat);
        return TurfJoins.inside(p, this.zone);
    }
}
