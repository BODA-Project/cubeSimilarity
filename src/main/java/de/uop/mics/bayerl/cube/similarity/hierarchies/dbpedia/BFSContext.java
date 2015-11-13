package de.uop.mics.bayerl.cube.similarity.hierarchies.dbpedia;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Files;
import de.uop.mics.bayerl.cube.similarity.hierarchies.dbpedia.json.Graph;
import de.uop.mics.bayerl.cube.similarity.hierarchies.dbpedia.json.Link;
import de.uop.mics.bayerl.cube.similarity.hierarchies.dbpedia.json.Node;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by sebastianbayerl on 26/10/15.
 */
public class BFSContext {


    private static final int LINK_VAL_PATH = 50;
    private static final int LINK_VAL = 1;

    private static final int NODE_GRP_ITEM = 0;
    private static final int NODE_GRP_PATH = 1;
    private static final int NODE_GRP = 2;

    private static final int BFS_DEPTH = 2;


    public static String getPathGraph(List<List<String>> paths) {
        // TODO mark nodes on shortest path
        String spacer = "<######>";
        Set<String> allNodes = new HashSet<>();
        Set<String> allEdges = new HashSet<>();

        for (List<String> path : paths) {
            allNodes.addAll(path);
            for (int i = 0; i < path.size() - 1; i++) {
                allEdges.add(path.get(i) + spacer + path.get(i + 1));
            }
        }

        // get nodes on shortest paths
        int shortest = 100;
        for (List<String> path : paths) {
            shortest = Math.min(shortest, path.size());
        }

        Set<String> shortestPathNodes = new HashSet<>();
        for (List<String> path : paths) {
            if (path.size() == shortest) {
                shortestPathNodes.addAll(path);
            }
        }

        List<String> allNodesList = new ArrayList<>();
        allNodesList.addAll(allNodes);

        Graph graph = new Graph();
        Node[] nodes = new Node[allNodesList.size()];
        graph.setNodes(nodes);
        for (int i = 0; i < allNodesList.size(); i++) {
            String node = allNodesList.get(i);
            Node n = new Node();
            nodes[i] = n;

            String name = node.replace("http://dbpedia.org/resource/", "dbp:").replace("dbp:Category:", "");
            n.setName(name);
            if (name.startsWith("dbp:")) {
                n.setGroup(NODE_GRP_ITEM);
            } else if (shortestPathNodes.contains(node)) {
                n.setGroup(NODE_GRP_PATH);
            } else {
                n.setGroup(NODE_GRP);
            }
        }

        Map<String, Integer> nodePosition = new HashMap<>();

        for (int i = 0; i < allNodesList.size(); i++) {
            nodePosition.put(allNodesList.get(i), i);
        }


        List<String> allEdgesList = new ArrayList<>();
        allEdgesList.addAll(allEdges);

        Link[] links = new Link[allEdges.size()];
        graph.setLinks(links);
        for (int i = 0; i < allEdgesList.size(); i++) {
            String edge = allEdgesList.get(i);
            String[] splits = edge.split(spacer);
            String s = splits[0];
            String t = splits[1];

            Link link = new Link();
            links[i] = link;
            link.setSource(nodePosition.get(s));
            link.setTarget(nodePosition.get(t));
            link.setValue(LINK_VAL);
        }

        return toJson(graph);
    }


    public static String getPathContext(List<String> p, boolean includeLeafs) {
        List<String> path = new ArrayList<>();

        for (String s : p) {
            if (s.contains("Category:")) {
                path.add(s);
            }
        }

        String spacer = "<#####>";
        // check to start and end with category
        Set<String> allNodes = new HashSet<>();
        Set<String> allEdges = new HashSet<>();

        for (String pathNode : path) {
            Set<String> visited = new HashSet<>();
            Set<String> temp = new HashSet<>();
            temp.add(pathNode);
            for (int i = 0; i < BFS_DEPTH; i++) {
                Set<String> tempTarget = new HashSet<>();
                for (String node : temp) {
                    List<String> nodes = BfsSearch.getInstance().getNextNodes(node, EdgeMode.BOTH, BfsSearch.SKOS_BROADER);

                    for (String n : nodes) {
                        // add edges
                        String key = node + spacer + n;
                        //System.out.println(key);

                        allEdges.add(key);

                        // check for new found nodes
                        if (!visited.contains(n)) {
                            tempTarget.add(n);
                        }
                    }
                    visited.addAll(nodes);
                    allNodes.addAll(nodes);
                }

                temp = tempTarget;
            }
        }

        // path nodes
        Set<String> pathNodes = new HashSet<>();
        pathNodes.addAll(path);

        List<String> allNodesList = new ArrayList<>();
        allNodesList.addAll(allNodes);

        List<String> allEdgesList = new ArrayList<>();
        allEdgesList.addAll(allEdges);

        Graph graph = new Graph();
        Node[] nodes = new Node[allNodesList.size() + 2];
        graph.setNodes(nodes);
        for (int i = 0; i < allNodesList.size(); i++) {
            String node = allNodesList.get(i);
            Node n = new Node();
            nodes[i] = n;
            n.setName(node.replace("http://dbpedia.org/resource/Category:", ""));

            if (pathNodes.contains(node)) {
                n.setGroup(NODE_GRP_PATH);
            } else {
                n.setGroup(NODE_GRP);
            }
        }

        Map<String, Integer> nodePosition = new HashMap<>();

        for (int i = 0; i < allNodesList.size(); i++) {
            nodePosition.put(allNodesList.get(i), i);
        }

        Link[] links = new Link[allEdges.size() + 2];
        graph.setLinks(links);
        for (int i = 0; i < allEdgesList.size(); i++) {
            String edge = allEdgesList.get(i);
            //System.out.println(edge);
            String[] splits = edge.split(spacer);
            String s = splits[0];
            String t = splits[1];

            Link link = new Link();
            links[i] = link;
            link.setSource(nodePosition.get(s));
            link.setTarget(nodePosition.get(t));

            // mark path edges
            if (pathNodes.contains(s) && pathNodes.contains(t)) {
                link.setValue(LINK_VAL_PATH);
            } else {
                link.setValue(LINK_VAL);
            }
        }

        // add start and end of the path
        Node startNode = new Node();
        startNode.setName(p.get(0).replace("http://dbpedia.org/resource/", "dbp:"));
        startNode.setGroup(NODE_GRP_ITEM);
        nodes[nodes.length - 2] = startNode;

        Node endNode = new Node();
        endNode.setName(p.get(p.size() - 1).replace("http://dbpedia.org/resource/", "dbp:"));
        endNode.setGroup(NODE_GRP_ITEM);
        nodes[nodes.length - 1] = endNode;

        Link startLink = new Link();
        startLink.setSource(nodes.length - 2);
        startLink.setTarget(nodePosition.get(p.get(1)));
        startLink.setValue(LINK_VAL_PATH);
        links[links.length - 2] = startLink;

        Link endLink = new Link();
        endLink.setSource(nodePosition.get(p.get(p.size() - 2)));
        endLink.setTarget(nodes.length - 1);
        endLink.setValue(LINK_VAL_PATH);
        links[links.length - 1] = endLink;

        if (!includeLeafs) {
            graph = reduceGraph(graph);
        }

        return toJson(graph);
    }

    private static String toJson(Graph graph) {
        ObjectMapper mapper = new ObjectMapper();
        String output = "error";
        try {
            output = mapper.writeValueAsString(graph);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        System.out.println(output);
        try {
            Files.write(output, new File("test.json"), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return output;
    }

    private static Graph reduceGraph(Graph graph) {
        List<Node> nodes = new ArrayList<>(Arrays.asList(graph.getNodes()));
        List<Link> links = new ArrayList<>(Arrays.asList(graph.getLinks()));

        Map<String, Integer> nodePosition = new HashMap<>();

        for (int i = 0; i < nodes.size(); i++) {
            System.out.println(nodes.get(i).getName() + " " +  i);
            nodePosition.put(nodes.get(i).getName(), i);
        }

        boolean work = true;

        while (work) {
            List<Node> deleteNodes = new ArrayList<>();
            for (Node node : nodes) {
                // check for links

                if (node.getGroup() != NODE_GRP_ITEM) {
                    int nrOfLinks = 0;

                    for (Link link : links) {
                        int nodePos = nodePosition.get(node.getName());
                        if (link.getSource() == nodePos || link.getTarget() == nodePos) {
                            nrOfLinks++;

                            if (nrOfLinks > 1) {
                                break;
                            }
                        }
                    }

                    if (nrOfLinks == 1) {
                        deleteNodes.add(node);
                    }
                }
            }

            if (deleteNodes.size() == 0) {
                work = false;
            } else {
                System.out.println("delete: " + deleteNodes.size());
                for (Node deleteNode : deleteNodes) {
                    nodes.remove(deleteNode);
                    int nodePos = nodePosition.get(deleteNode.getName());

                    Set<Link> deleteLinks = new HashSet<>();
                    for (Link link : links) {
                        if (link.getSource() == nodePos || link.getTarget() == nodePos) {
                            deleteLinks.add(link);
                        }
                    }

                    for (Link deleteLink : deleteLinks) {
                        links.remove(deleteLink);
                    }
                }
            }
        }


        Map<Integer, Integer> newPositions = new HashMap<>();


        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            int oldPos = nodePosition.get(node.getName());

            newPositions.put(oldPos, i);
        }

        // generate new links
        List<Link> newLinks = new ArrayList<>();

        for (Link link : links) {
            Link newLink = new Link();
            newLink.setValue(link.getValue());
            newLink.setSource(newPositions.get(link.getSource()));
            newLink.setTarget(newPositions.get(link.getTarget()));
            newLinks.add(newLink);
        }

        graph.setNodes(nodes.toArray(new Node[nodes.size()]));
        graph.setLinks(newLinks.toArray(new Link[newLinks.size()]));

        return graph;
    }
}
