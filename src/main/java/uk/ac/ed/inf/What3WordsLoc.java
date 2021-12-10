package uk.ac.ed.inf;

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

}
