package bearmaps.proj2c;

import bearmaps.hw4.streetmap.Node;
import bearmaps.hw4.streetmap.StreetMapGraph;
import bearmaps.proj2ab.Point;
import bearmaps.proj2ab.WeirdPointSet;
import java.util.ArrayList;
import edu.princeton.cs.algs4.TST;

import java.util.*;

/**
 * An augmented graph that is more powerful that a standard StreetMapGraph.
 * Specifically, it supports the following additional operations:
 *
 *
 * @author Alan Yao, Josh Hug, ________
 */
public class AugmentedStreetMapGraph extends StreetMapGraph {
    private HashMap<Point, Node> map = new HashMap<>();
    private WeirdPointSet pointSet;
    private TST<List<Node>> trie = new TST<>();
    private Map<String, List<Node>> locationGet = new HashMap<>();

    public AugmentedStreetMapGraph(String dbPath) {
        super(dbPath);
        List<Node> nodes = this.getNodes();
        List<Point> points = new ArrayList<>();
        for (Node n : nodes) {
            if (n.name() != null){
                String cleaned = cleanString(n.name());
                if (cleaned.length() >= 1) {
                    if (trie.contains(cleaned)) {
                        List<Node> nameList = trie.get(cleaned);
                        nameList.add(n);
                        trie.put(cleaned, nameList);
                    } else {
                        List<Node> value = new ArrayList<>();
                        value.add(n);
                        trie.put(cleaned, value);
                    }
                } else if (cleaned.equals("")){
                    if (locationGet.containsKey(cleaned)) {
                        List<Node> nameList = locationGet.get(cleaned);
                        locationGet.remove(cleaned);
                        nameList.add(n);
                        locationGet.put(cleaned, nameList);
                    } else {
                        List<Node> nodeList = new ArrayList<>();
                        nodeList.add(n);
                        locationGet.put(cleaned, nodeList);
                    }
                }
            }
            if (neighbors(n.id()).size() > 0) {
                Point p = new Point(n.lon(), n.lat());
                points.add(p);
                map.put(p, n);
            }
        }
        pointSet = new WeirdPointSet(points);
    }


    /**
     * For Project Part II
     * Returns the vertex closest to the given longitude and latitude.
     * @param lon The target longitude.
     * @param lat The target latitude.
     * @return The id of the node in the graph closest to the target.
     */
    public long closest(double lon, double lat) {
        return map.get(pointSet.nearest(lon, lat)).id();
    }


    /**
     * For Project Part III (gold points)
     * In linear time, collect all the names of OSM locations that prefix-match the query string.
     * @param prefix Prefix string to be searched for. Could be any case, with our without
     *               punctuation.
     * @return A <code>List</code> of the full names of locations whose cleaned name matches the
     * cleaned <code>prefix</code>.
     */
    public List<String> getLocationsByPrefix(String prefix) {
        List<String> lst = new ArrayList<>();
        String cleaned = cleanString(prefix);
        if (cleaned.length() >= 1) {
            Iterable<String> names = trie.keysWithPrefix(cleaned);
            for (String s : names) {
                List<Node> value = trie.get(s);
                for (Node n : value) {
                    lst.add(n.name());
                }
            }
        }
        return lst;
    }

    /**
     * For Project Part III (gold points)
     * Collect all locations that match a cleaned <code>locationName</code>, and return
     * information about each node that matches.
     * @param locationName A full name of a location searched for.
     * @return A list of locations whose cleaned name matches the
     * cleaned <code>locationName</code>, and each location is a map of parameters for the Json
     * response as specified: <br>
     * "lat" -> Number, The latitude of the node. <br>
     * "lon" -> Number, The longitude of the node. <br>
     * "name" -> String, The actual name of the node. <br>
     * "id" -> Number, The id of the node. <br>
     */
    public List<Map<String, Object>> getLocations(String locationName) {
        List<Map<String, Object>> lst = new ArrayList<>();
        if (locationName != null) {
            String cleaned = cleanString(locationName);
            if (cleaned.length() >= 1) {
                List<Node> nodeList = trie.get(cleaned);
                for (Node n : nodeList) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("lat", n.lat());
                    map.put("lon", n.lon());
                    map.put("name", n.name());
                    map.put("id", n.id());
                    lst.add(map);
                }
            } else if (cleaned.equals("")) {
                List<Node> loc = locationGet.get(cleaned);
                for (Node n : loc) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("lat", n.lat());
                    map.put("lon", n.lon());
                    map.put("name", n.name());
                    map.put("id", n.id());
                    lst.add(map);
                }
            }
        }
        return lst;
    }


    /**
     * Useful for Part III. Do not modify.
     * Helper to process strings into their "cleaned" form, ignoring punctuation and capitalization.
     * @param s Input string.
     * @return Cleaned string.
     */
    private static String cleanString(String s) {
        return s.replaceAll("[^a-zA-Z ]", "").toLowerCase();
    }

}
