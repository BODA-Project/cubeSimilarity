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

    private static final int BFS_DEPTH = 1;

    public static String getPathContext(List<String> p) {
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
                    List<String> nodes = BfsSearch.getNextNodes(node, EdgeMode.BOTH, BfsSearch.SKOS_BROADER);

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
        startNode.setName(p.get(0).replace("http://dbpedia.org/resource/", ""));
        startNode.setGroup(NODE_GRP_ITEM);
        nodes[nodes.length - 2] = startNode;

        Node endNode = new Node();
        endNode.setName(p.get(p.size() - 1).replace("http://dbpedia.org/resource/", ""));
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
}
