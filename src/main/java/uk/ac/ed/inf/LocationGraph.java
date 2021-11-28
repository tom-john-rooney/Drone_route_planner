package uk.ac.ed.inf;

import org.jgrapht.graph.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.jgrapht.GraphTests.isEmpty;

public class LocationGraph {
    private final SimpleDirectedWeightedGraph<String, DefaultWeightedEdge> g = new SimpleDirectedWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);

    public LocationGraph(){}

    public void buildGraph(Words w3w){
        if(!(isEmpty(g))) {
            addVertices(w3w);
            addEdges(w3w);
        }
    }

    private void addVertices(Words w3w){
        Set<String> keys = w3w.getWordsMap().keySet();
        for(String key: keys){
            g.addVertex(key);
        }
    }

    private void addEdges(Words w3w){
        HashMap<String, Integer> wordsMap = w3w.getEdgeMap();
        for (Map.Entry<String, Integer> entry : wordsMap.entrySet()) {
            ArrayList<String> constituentKeys = w3w.splitCombinedKey(entry.getKey());
            String fromKey = constituentKeys.get(0);
            String toKey = constituentKeys.get(1);
            DefaultWeightedEdge edge = g.addEdge(fromKey, toKey);
            g.setEdgeWeight(edge, entry.getValue());
        }
    }
}
