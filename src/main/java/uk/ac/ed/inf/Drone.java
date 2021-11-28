package uk.ac.ed.inf;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Drone {
    public static final String AT_W3W_ADDR = "nests.takes.print";

    private String w3wAddress;
    private What3WordsLoc.LongLat position;
    private LocationGraph lg = new LocationGraph();
    private Words w3w = new Words();
    private NoFlyZones zones = new NoFlyZones();

    public Drone(What3WordsLoc.LongLat position){
        this.w3wAddress = this.AT_W3W_ADDR;
        this.position = position;
    }

    public void updateAddr(String newAddr){
        this.w3wAddress = newAddr;
    }

    public void updatePosn(What3WordsLoc.LongLat newPosn){
        this.position = newPosn;
    }

    public void setGraph(LocationGraph lg){
        if(this.lg.emptyGraph()){
            this.lg = lg;
        }
    }

    public void setWords(Words w3w){
        if(WebServer.serverLocUnspeccified(this.w3w.machine, this.w3w.port)){
            this.w3w = w3w;
        }
    }

    public void setZones(NoFlyZones zones){
        if(WebServer.serverLocUnspeccified(this.zones.machine, this.zones.port)){
            this.zones = zones;
        }
    }

    public void makeDelivery(ArrayList<String> pickUpLocs, String deliveryLoc){
        if(lg.emptyGraph()
                || WebServer.serverLocUnspeccified(this.w3w.machine, this.w3w.port)
                || WebServer.serverLocUnspeccified(this.zones.machine, this.zones.port)){
            System.err.println("Fatal error in Drone.make delivery: Make sure graph, w3w and zones field are set.");
            System.exit(1);
        }

        List<List<String>> w3wPath = lg.getW3wPathFromGraph(this.w3wAddress, pickUpLocs, deliveryLoc);
    }
}
