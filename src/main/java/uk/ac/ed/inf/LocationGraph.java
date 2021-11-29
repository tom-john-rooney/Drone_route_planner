package uk.ac.ed.inf;

import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.*;

import java.util.*;

import static org.jgrapht.GraphTests.isEmpty;

/**
 * Represents the locations the drone can visit as a graph.
 *
 * The vertices of the graph are locations stored on the web server in the 'words' directory i.e.,
 * landmarks, shops, delivery locations etc. An edge exists between 2 vertices if there exists a
 * 'straight' path between them in real life, which does not go through any no-fly-zones. The graph
 * is also weighted; the weight being the approximate number of moves it takes for the drone to move
 * from the location represented by the vertex at one end of the edge, to the other in real life.
 */
public class LocationGraph {
    /** The actual graph */
    private final SimpleDirectedWeightedGraph<String, DefaultWeightedEdge> g = new SimpleDirectedWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
    /** Words instance whose HashMaps contain details of all words stored on the web server and the edges between then */
    private final Words w3w;

    /**
     * Constructor to instantiate a new LocationGraph instance.
     *
     * @param w3w a Words instance whose edgeMap contains details of all edges that exist and
     *            their weights
     */
    public LocationGraph(Words w3w){
        this.w3w = w3w;
        buildGraph();
    }

    /** Populates the vertices and edges of the graph using the edgeMap of the w3w field */
    public void buildGraph(){
        if(isEmpty(g)){
            addVertices();
            addEdges();
        }
    }

    /** Adds a vertex to the graph for each key (address) stored in w3w's wordsMap */
    private void addVertices(){
        Set<String> keys = w3w.getWordsMap().keySet();
        for(String key: keys){
            g.addVertex(key);
            System.out.println(key);
        }
        System.out.println("vertices done\n");
    }

    /** Adds the edges to the graph that exist in w3w's edgeMap*/
    private void addEdges(){
        HashMap<String, Integer> wordsMap = w3w.getEdgeMap();
        for (Map.Entry<String, Integer> entry : wordsMap.entrySet()) {
            // Keys in Words.edgeMap consist of the start location's w3w address and the end location's address
            // joined by a "."
            ArrayList<String> constituentKeys = w3w.splitCombinedKey(entry.getKey());
            String fromKey = constituentKeys.get(0);
            String toKey = constituentKeys.get(1);
            DefaultWeightedEdge edge = g.addEdge(fromKey, toKey);
            g.setEdgeWeight(edge, entry.getValue());
            System.out.println(fromKey + " " + toKey);
        }
        System.out.println("edges done \n");
    }

    /**
     * Finds the shortest path for the drone to follow to visit all the locations it needs to in order to make a delivery.
     *
     * All inputs are in w3w address form, with the output being a List of Lists of w3wAddress. Each List represents the
     * addresses the drone needs to visit from one of the specified locations to the next, in order to follow the shortest
     * overall path.
     *
     * @param startLoc the w3w address that the drone is currently close to
     * @param pickUpLocs the shop(s) that the drone must visit to pick up the items in the order
     * @param delivLoc the delivery location selected by the user
     * @return A List<List<String>> where each nested List is a List of w3w addresses for the drone to visit to get from
     *         one parameter location to the next
     */
    public List<List<String>> getW3wPathFromGraph(String startLoc, ArrayList<String> pickUpLocs, String delivLoc){
        switch(pickUpLocs.size()){
            case 1:{
                // Only one shop so simply retrieve it.
                String shopLoc = pickUpLocs.get(0);
                ArrayList<String> locsToVisit = new ArrayList<>(Arrays.asList(startLoc, shopLoc, delivLoc));
                return getGraphPath(locsToVisit);
            }
            case 2:{
                String shopOneLoc = pickUpLocs.get(0);
                String shopTwoLoc = pickUpLocs.get(1);

                // 2 shops; need to try both possible orderings of them to see which is fastest.
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

    /**
     * Finds the number of moves that a path between nodes in the graph takes and returns its total
     * weight as the sum of the weights of each edge visited.
     *
     * Note: 'path' means a route from one node to another, with specified stops along the way.
     *
     * @param path The path whose weight is to be calculated
     * @return the total weight of each edge traversed along the path
     */
    private int getPathWeight(List<List<String>> path){
        int totalWeight = 0;
        for(List<String> subpath: path){
            totalWeight += getSubPathWeight(subpath);
        }
        return totalWeight;
    }

    /**
     * Calculates the weight of a sub-path in a path. This is the number of moves taken to get from the
     * vertex at the start of the sub-path to the one at the end.
     *
     * See documentation of 'LocationGraph.getPathWeight' for definition of 'path'. A 'sub-path' is a path between 2
     * vertices the drone must visit in its 'path'; there are no specified vertices that must be visited along the way.
     *
     * @param subPath the subpath whose weight is to be calculated
     * @return the total weight of each edge traversed along the sub path
     */
    private int getSubPathWeight(List<String> subPath){
        int subPathWeight = 0;
        for(int i = 0; i < subPath.size()-1; i++){
            String startVertex = subPath.get(i);
            String endVertex = subPath.get(i+1);
            DefaultWeightedEdge edge = g.getEdge(startVertex, endVertex);
            subPathWeight += g.getEdgeWeight(edge);
        }
        return subPathWeight;
    }

    /**
     * Gets the shortest path that visits every vertex specified in locsToVisit in order.
     *
     * @param locsToVisit the vertices that must be visited along the path, in order
     * @return the shortest path that visits each of these vertices in order
     */
    private List<List<String>> getGraphPath(ArrayList<String> locsToVisit){
        List<List<String>> path = new ArrayList<>();
        for(int i=0; i < locsToVisit.size()-1; i++){
            String currLoc = locsToVisit.get(i);
            String nxtLoc = locsToVisit.get(i+1);
            path.add(getShortestPath(currLoc, nxtLoc));
        }
        return path;
    }

    /**
     * Gets the shortest path through the graph between 2 specified vertices.
     *
     * There are no specified stops along the way.
     *
     * @param start the start of the path
     * @param end the vertex at the end of the path
     * @return the shortest path between the 2 as a List of w3w addresses (vertices)
     */
    private List<String> getShortestPath(String start, String end){
        System.out.println(start + " " + end);
         return DijkstraShortestPath.findPathBetween(g, start, end).getVertexList();
    }
}
