package uk.ac.ed.inf;

import java.util.ArrayList;

/**
 * Represents a What3Words address.
 *
 * More details of WhatThreeWords can be found here: https://what3words.com/about/
 */
public class What3WordsLoc {
    /** A LongLat object to represent the precise Longitude and Latitude that the w3w address maps to. */
    public final LongLat coordinates;

    /**
     * Constructor to initialise a new What3WordsLoc
     *
     * @param coordinates a LongLat object representing the precise location of the address
     */
    public What3WordsLoc(LongLat coordinates){
        this.coordinates = coordinates;
    }

    /**
     * Represents a coordinate pair.
     *
     * These coordinates consist of 2 components; a longitude and a latitude.
     * They are of the form (longitude, latitude).
     */
    public static class LongLat {
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
        /** The number which any drone move's direction in degrees must be a scalar multiple of */
        public static final int ANGLE_SCALE = 10;
        /** The largest bearing on which the drone can move */
        public static final int MAX_ANGLE = 350;
        /** The smallest bearing on which the drone can move */
        public static final int MIN_ANGLE = 0;
        /** The special 'junk' value to represent when the drone is hovering */
        public static final int JUNK_ANGLE = -999;
        /** The length of any move the drone makes in degrees */
        public static final double MOVE_DISTANCE = 0.00015;
        /** The location of Appleton Tower */
        public static final What3WordsLoc.LongLat AT_LOC = new What3WordsLoc.LongLat(-3.186874, 55.944494);

        /** The longitude component of the coordinate */
        public final double lng;
        /** The latitude component of the coordinate */
        public final double lat;

        /**
         * Constructor to initialise a new LongLat instance.
         *
         * @param lng the longitude component of the coordinate being initialised
         * @param lat the latitude component of the coordinate being initialised
         */
        public LongLat(double lng, double lat){
            this.lng = lng;
            this.lat = lat;
        }

        @Override
        public String toString(){
            return "longitude: " + this.lng + "\n" + "latitude: " + this.lat;
        }

        public boolean equals(What3WordsLoc.LongLat point){ return (this.lng == point.lng) && (this.lat == point.lat); }

        /**
         * Checks if a LongLat instance lies within the confinement area.
         *
         * This means is lies in the area defined by MAX_LONGITUDE, MIN_LONGITUDE,
         * MAX_LATITUDE, and MIN_LATITUDE.
         * This area strict i.e, being on the boundary equates to being outside it.
         *
         * @return true if coordinate lies within the confinement area, false otherwise
         */
        public boolean isConfined(){
            boolean lngConfined = (lng < MAX_LONGITUDE) && (lng > MIN_LONGITUDE);
            boolean latConfined = (lat < MAX_LATITUDE) && (lat > MIN_LATITUDE);

            return lngConfined && latConfined;
        }

        /**
         * Calculates the Pythagorean distance between 2 LongLat instances.
         *
         * @param point the point to which the Pythagorean distance is calculated
         * @return the Pythagorean distance between the calling instance and point in degrees
         */
        public double distanceTo(What3WordsLoc.LongLat point){
            isPointNull(point);
            return Math.hypot(this.lng - point.lng, this.lat - point.lat);
        }

        /**
         * Determines if one LongLat instance is 'close to' another.
         *
         * This means that the two instances are within DISTANCE_TOLERANCE degrees of each other.
         *
         * @param point the point which may or may not be 'close to' the calling instance
         * @return true if the instance calling the method is 'close to' point, false otherwise
         */
        public boolean closeTo(What3WordsLoc.LongLat point){
            isPointNull(point);
            return this.distanceTo(point) < DISTANCE_TOLERANCE;
        }

        /**
         * Calculates the next position of the drone after a move.
         *
         * This method assumes that the two points lie in a 2D plane, not on a sphere.
         * It also adopts the convention that East = 0 degrees, North = 90 etc.
         *
         * @param angle the direction in which the move is to be made in degrees
         * @return a LongLat object representing the longitude and latitude of the drone
         *         after the move, this object is null if the angle is not a multiple of ANGLE_SCALE
         *         or lies outside the range [MIN_ANGLE,MAX_ANGLE]
         */
        public What3WordsLoc.LongLat nextPosition(int angle){
            if(angle == JUNK_ANGLE){
                return new What3WordsLoc.LongLat(this.lng, this.lat);
            }
            else if(!(angle % ANGLE_SCALE == 0) || angle > MAX_ANGLE || angle < MIN_ANGLE){
                System.err.println(String.format("Fatal error in What3WordsLoc.LongLat.nextPosition: angle %d is invalid.",angle));
                System.exit(1);
                return null;
            }
            else{
                double newlng = this.lng + MOVE_DISTANCE * Math.cos(Math.toRadians(angle));
                double newlat = this.lat + MOVE_DISTANCE * Math.sin(Math.toRadians(angle));
                return new What3WordsLoc.LongLat(newlng, newlat);
            }
        }

        /**
         * Handles null coordinates.
         *
         * Does so by exiting the application and displaying an error message in the case that
         * the input coordinate is null.
         * This method is to be used as a helper by other methods within this class to help with
         * input validation.
         *
         * @param point the point being checked for null status
         */
        private void isPointNull(What3WordsLoc.LongLat point){
            if(point == null){
                System.err.println("Fatal error: null coordinate.");
                System.exit(1);
            }
        }

        /**
         * Gets the bearing to a point, with the bearing returned being a multiple of ANGLE_SCALE and in the range
         * [MIN_ANGLE, MAX_ANGLE].
         *
         * @param point the point to which the bearing is to be calculated
         * @return the bearing to point
         */
        public int getBearingTo(What3WordsLoc.LongLat point){
            if(this.equals(point)){
                return JUNK_ANGLE;
            }

            double theta = Math.atan2(point.lat - this.lat, point.lng - this.lng);
            float angle = (float) Math.toDegrees(theta);
            angle = Math.round(angle/ANGLE_SCALE) * ANGLE_SCALE;
            if(angle < 0) {
                angle += 360;
            }else if(angle == 360){
                angle = 0;
            }
            return (int) angle;
        }

        /**
         * Gets the path to a specified point as an ArrayList of What3WordsLoc.LongLat objects, with
         * each object representing a point to which a move is to be made as the path is traversed from
         * the calling instance to point.
         *
         * @param point the point to which the path is to be calculated
         * @param zones a NoFlyZones object whose zones field contains details of all no-fly-zones
         *              stored on the web server
         * @return an ArrayList of What3Words.LongLat instances representing the points along the path between
         *         the calling instance and point, if a legal, straight path between them exists. Otherwise, an#
         *         empty ArrayList is returned.
         */
        public ArrayList<What3WordsLoc.LongLat> getPathTo(What3WordsLoc.LongLat point, NoFlyZones zones){
            isPointNull(point);
            // Checks on both to and from points; both most be confined and outside nfz's.
            if(!(point.isConfined()) || !(this.isConfined()) || zones.pointInZones(point)) {
                System.err.println(String.format("Fatal error in What3WordsLoc.LongLat.getPathTo.\n\nStart point:" +
                        "\nLongitude %d\nLatitude %d" +
                        "\n\nEnd point:\nLongitude: %d\nLatitude: %d" +
                        "\n\nCheck that both points are in confinement area and outside of no-fly-zones.", this.lng, this.lat, point.lng, point.lat));
                System.exit(1);
                return null;
            }

            ArrayList<What3WordsLoc.LongLat> pointsOnPath = new ArrayList<>();
            What3WordsLoc.LongLat currPos = new What3WordsLoc.LongLat(this.lng, this.lat);;
            pointsOnPath.add(currPos);
            while(!(currPos.closeTo(point))){
                int bearing = currPos.getBearingTo(point);
                What3WordsLoc.LongLat nextPos = currPos.nextPosition(bearing);
                boolean confined = nextPos.isConfined();
                boolean intersectsZones = zones.lineIntersectsZones(currPos, nextPos);
                if(confined && !(intersectsZones)){
                    pointsOnPath.add(nextPos);
                    currPos = nextPos;
                }
                // No legal, straight path exists between the points.
                else{
                    return new ArrayList<>();
                }
            }
            return pointsOnPath;
        }
    }
}
