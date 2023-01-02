package com.redex.application.algorithm.AstarAlgorithm;

import com.redex.application.core.Utils.DateAndTime;
import com.redex.application.core.model.business.Shipment;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.*;

@Slf4j
public class RouteFinder {
    private final Graph graph;
    private final ConnectScorer nextNodeScorer;
    private final ConnectScorer targetScorer;

    public RouteFinder(Graph graph, ConnectScorer nextNodeScorer, ConnectScorer targetScorer) {
        this.graph = graph;
        this.nextNodeScorer = nextNodeScorer;
        this.targetScorer = targetScorer;
    }

    /*
    * This method uses A* algorithm to find a route between two nodes.
    * A* algorithm uses a priority queue to select which node is the next node, where weights are set in ConnectScorer class.
    */

    List<Node> findRoute(Node from, Node to,
                         Shipment shipment){

        Map<Node, RouteNode> allNodes = new HashMap<>(); // map of every node that we've visited so far and what we know about it
        Queue<RouteNode> openSet = new PriorityQueue<>(); //nodes that we can consider as the next step

        RouteNode start = new RouteNode(from, null,
                0d,  targetScorer.computeCost(from, to, shipment));

        allNodes.put(from, start);
        openSet.add(start);

        RouteNode actual = null;

        while(!openSet.isEmpty()){

            actual = openSet.poll();

            if(actual.getCurrent().getId() == to.getId()){

                //Found our destination!
                List<Node> route = new ArrayList<>();
                RouteNode current = actual;
                do{
                    route.add(0, current.getCurrent());
                    current = allNodes.get(current.getPrevious());
                } while (current!=null);

                //*** below code section is not necessary, only for console prints ***

                String line = shipment.getCode()+";"+shipment.getRegistrationDatetime()+";"+shipment.getTotalQuantity()+';';
                for (int i = 0; i < route.size(); i++) {
                    line+=route.get(i).getAirport().getOACI();
                    if(i< route.size()-1)
                        line+="->";
                    if(i == route.size()-1){
                        shipment.setDeliveryDateTime(route.get(i).getFlightCargoOrigin().getEndDateTime().plusHours(1));
                    }
                }
                line+=";";
                Duration duration = DateAndTime.getDurationLocalTime(route.get(1).getFlightCargoOrigin().getStartDateTime().toLocalDateTime(),
                        route.get(route.size()-1).getFlightCargoOrigin().getEndDateTime().toLocalDateTime());
                String formattedElapsedTime = String.format("%02d:%02d:%02d", duration.toHours(),
                        duration.toMinutes() % 60, duration.toSeconds() % 60);

                line+= formattedElapsedTime+";" + shipment.getDeliveryDateTime();

                System.out.println(line);
                //*** end of print code ***

                return route;
            }

            //get connections for actual node
            List<Node> connections = graph.getConnections(actual.getCurrent());

            //for each connection from actual node
            for (Node connection: connections) {
                RouteNode nextNode = allNodes.getOrDefault(connection, new RouteNode(connection));

                //save actual node and his routeNode in list of checked nodes
                allNodes.put(connection, nextNode);

                //check one hour restriction between flights
                if(actual.getCurrent().getFlightCargoOrigin() != null){
                    if(actual.getCurrent().getFlightCargoOrigin().getEndDateTime().plusHours(1).compareTo(nextNode.getCurrent().getFlightCargoOrigin().getStartDateTime()) > 0) {
                        continue;
                    }
                }

                //calc cost for new route
                double newScore = actual.getRouteScore() + nextNodeScorer.computeCost(actual.getCurrent(), connection, shipment);

                //get cost for new route (originally infinite)
                double nextScore = nextNode.getRouteScore();
                if (nextScore > newScore) {
                    nextNode.setPrevious(actual.getCurrent());
                    nextNode.setRouteScore(newScore);
                    nextNode.setEstimatedScore(newScore + targetScorer.computeCost(connection, to, shipment));
                    openSet.add(nextNode);
                }
            }
        }

        return null;

    }

}
