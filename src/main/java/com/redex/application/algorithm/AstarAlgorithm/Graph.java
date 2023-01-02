package com.redex.application.algorithm.AstarAlgorithm;
import java.util.*;

public class Graph {

    private final List<Node> nodeList;
    private final Map<Long, List<Node>> connections;

    public Graph(List<Node> nodeSet, Map<Long, List<Node>> connections) {
        this.nodeList = Collections.synchronizedList(nodeSet);
        this.connections = Collections.synchronizedMap(connections);
    }

    public Node getNode(Long id){

        Node node = null;
        for(Node onenode : nodeList){
            if(onenode.getId() == id) node = onenode;;
        }
        return node;
    }

    public List<Node> getConnections(Node node){
        return connections.get(node.getId());
    }
}
