package uk.ac.ed.inf;

/**
 * Represents a coordinate pair.
 *
 * These coordinates consist of 2 components; a longitude and a latitude. They
 * are of the form (longitude, latitude).
 */
public class LongLat {
    /** The value which all longitudes must be strictly less than */
    public static final double MAX_LONGITUDE = -3.184319;
    /** The value which all longitudes must be strictly greater than */
    public static final double MIN_LONGITUDE = -3.192473;
    /** The value which all latitudes must be strictly less than */
    public static final double MAX_LATITUDE = 55.946233;
    /** The value which all longitudes must be strictly greater than */
    public static final double MIN_LATITUDE = 55.942617;
    /** The maximum distance allowed in degrees between two points for them to be 'close to' one another */
    public static final double DISTANCE_TOLERANCE = 0.00015;
    /** The number of which any drone move's direction in degrees must be a scalar multiple of */
    public static final int ANGLE_SCALE = 10;
    /** The largest bearing on which the drone can move */
    public static final int MAX_ANGLE = 350;
    /** The smallest bearing on which the drone can move */
    public static final int MIN_ANGLE = 0;
    /** The special 'junk' value to represent when the drone is hovering */
    public static final int JUNK_ANGLE = -999;
    /** The length of any move the drone makes in degrees */
    public static final double MOVE_DISTANCE = 0.00015;

    /** The longitude component of the coordinate */
    public final double longitude;
    /** The latitude component of the coordinate */
    public final double latitude;

    /**
     * Initialises a new LongLat instance.
     *
     * @param longitude the longitude component of the coordinate being initialised
     * @param latitude  the latitude component of the coordinate being initialised
     */
    public LongLat(double longitude, double latitude){
        this.longitude = longitude;
        this.latitude = latitude;
    }

    /**
     * Checks if a LongLat instance lies within the confinement area defined by
     * MAX_LONGITUDE, MIN_LONGITUDE, MAX_LATITUDE, MIN_LATITUDE.
     *
     * @return true if coordinate lies within the confinement area, false otherwise
     */
    public boolean isConfined(){
        boolean longitudeConfined = (longitude < MAX_LONGITUDE) && (longitude > MIN_LONGITUDE);
        boolean latitudeConfined = (latitude < MAX_LATITUDE) && (latitude > MIN_LATITUDE);

        return longitudeConfined && latitudeConfined;
    }

    /**
     * Calculates the Pythagorean distance between 2 LongLat instances.
     *
     * @param point to which the Pythagorean distance from the instance calling
     *              the method is calculated
     * @return the Pythagorean distance between the calling instance and point
     */
    public double distanceTo(LongLat point){
        isPointNull(point);
        return Math.hypot(this.longitude - point.longitude, this.latitude - point.latitude);
    }

    /**
     * Determines if one LongLat instance is 'close to' another i.e. within DISTANCE_TOLERANCE degrees
     *
     * @param point which is being checked to see if it is 'close to' the instance
     *              calling the method
     * @return true if the instance calling the method is close to point, false otherwise
     */
    public boolean closeTo(LongLat point){
        isPointNull(point);
        return this.distanceTo(point) < DISTANCE_TOLERANCE;
    }

    /**
     * Calculates the next position of the drone after a move
     *
     * @param angle the direction in which the move is to be made in degrees
     * @return a LongLat object representing the longitude and latitude of the drone after the move,
     *         this object is null if the angle is not a multiple of ANGLE_SCALE or lies outside the range
     *         [MIN_ANGLE,MAX_ANGLE]
     */
    public LongLat nextPosition(int angle){
        if(angle == JUNK_ANGLE){
            return new LongLat(this.longitude, this.latitude);
        }
        else if(!(angle % ANGLE_SCALE == 0) || angle > MAX_ANGLE || angle < MIN_ANGLE){
            return null;
        }
        else{
            double newLongitude = this.longitude + MOVE_DISTANCE * Math.cos(Math.toRadians(angle));
            double newLatitude = this.latitude + MOVE_DISTANCE * Math.sin(Math.toRadians(angle));
            return new LongLat(newLongitude, newLatitude);
        }
    }

    /**
     * Handles null points by exiting the application and displaying an error message
     *
     * @param point being checked for null status
     */
    public void isPointNull(LongLat point){
        if(point == null){
            System.err.println("Fatal error: null coordinate");
            System.exit(1);
        }
    }
}
