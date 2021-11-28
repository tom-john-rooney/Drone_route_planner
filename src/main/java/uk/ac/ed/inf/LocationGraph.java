package uk.ac.ed.inf;

import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.*;

import java.util.*;

import static org.jgrapht.GraphTests.isEmpty;

public class LocationGraph {
    private final SimpleDirectedWeightedGraph<String, DefaultWeightedEdge> g = new SimpleDirectedWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);

    public LocationGraph(){}

    public void buildGraph(Words w3w){
        if(isEmpty(g)){
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

    public List<List<String>> getW3wPathFromGraph(String startLoc, ArrayList<String> pickUpLocs, String delivLoc){
        switch(pickUpLocs.size()){
            case 1:{
                String shopLoc = pickUpLocs.get(0);
                ArrayList<String> locsToVisit = new ArrayList<>(Arrays.asList(startLoc, shopLoc, delivLoc));
                return getGraphPath(locsToVisit);
            }
            case 2:{
                String shopOneLoc = pickUpLocs.get(0);
                String shopTwoLoc = pickUpLocs.get(1);

                ArrayList<String> locsToVisit = new ArrayList<>(Arrays.asList(startLoc, shopOneLoc, shopTwoLoc, delivLoc));
                List<List<String>> pathOne = getGraphPath(locsToVisit);

                locsToVisit = new ArrayList<>(Arrays.asList(startLoc, shopTwoLoc, shopOneLoc, delivLoc));
                List<List<String>> pathTwo = getGraphPath(locsToVisit);

                if(getPathWeight(pathOne) > getPathWeight(pathTwo)){
                    return pathTwo;
                }else{
                    return pathOne;
                }
            }
            default:{
                System.err.println("Fatal error in LocationGraph.getW3wPath: Number of pickup locations must be 1 or 2.");
                System.exit(1);
                return null;
            }
        }
    }

    private int getPathWeight(List<List<String>> path){
        int totalWeight = 0;
        for(List<String> subpath: path){
            totalWeight += getSubPathWeight(subpath);
        }
        return totalWeight;
    }

    private int getSubPathWeight(List<String> path){
        int subPathWeight = 0;
        for(int i = 0; i < path.size()-1; i++){
            String startVertex = path.get(i);
            String endVertex = path.get(i+1);
            DefaultWeightedEdge edge = g.getEdge(startVertex, endVertex);
            subPathWeight += g.getEdgeWeight(edge);
        }
        return subPathWeight;
    }

    private List<List<String>> getGraphPath(ArrayList<String> locsToVisit){
        List<List<String>> path = new ArrayList<>();
        for(int i=0; i < locsToVisit.size()-1; i++){
            String currLoc = locsToVisit.get(i);
            String nxtLoc = locsToVisit.get(i+1);
            path.add(getShortestPath(currLoc, nxtLoc));
        }
        return path;
    }

    private List<String> getShortestPath(String start, String end){
        System.out.println(start + " " + end);
         return DijkstraShortestPath.findPathBetween(g, start, end).getVertexList();
    }

    public boolean emptyGraph(){
        return isEmpty(g);
    }
}
