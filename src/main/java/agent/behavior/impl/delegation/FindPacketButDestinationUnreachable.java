package agent.behavior.impl.delegation;

import agent.AgentAction;
import agent.AgentMemoryFragment;
import agent.AgentState;
import agent.behavior.impl.wander.Wander;
import environment.CellPerception;
import environment.Coordinate;


import java.util.*;

public class FindPacketButDestinationUnreachable extends Wander {
    @Override
    public void act(AgentState agentState, AgentAction agentAction) {
        var coloredDestinationCells= agentState.getPerception().getDestinationCells(agentState.getColor().get());
        if(coloredDestinationCells != null && !coloredDestinationCells.isEmpty()) {
            agentState.addMemoryFragment(agentState.getColor().get().toString(), new AgentMemoryFragment(coloredDestinationCells.get(0).getCoordinate()));
        }

        var destinationMem = agentState.getMemoryFragment(agentState.getColor().get().toString());
        if(destinationMem != null) {
            recordIsDestinationReachable(agentState, destinationMem);
            HashMap<CellPerception, List<Coordinate> > pathsToProblPackets = getPathsToProblPackets(agentState);

            for(var probPacket : pathsToProblPackets.keySet()){
                if(pathsToProblPackets.get(probPacket).size() == 1){
                    agentAction.pickPacket(probPacket.getX(), probPacket.getY());
                    return;
                }
            }

            CellPerception nearestProblematicPacket = getNearestProblematicPacket(pathsToProblPackets);

            if (nearestProblematicPacket != null && pathsToProblPackets.size()>=2){
                try{
                    Coordinate step = pathsToProblPackets.get(nearestProblematicPacket).get(1);
                    agentAction.step(step.getX(), step.getY());
                    return;
                }
                catch (Exception e){
                    System.out.println(" Array Index out of Range----------------- ");
                }
            }

            super.act(agentState,agentAction);
        }
        else{
            System.out.println("findPacketButDestinationUnreachable destination error "+ agentState.getName());
        }

    }

    private CellPerception getNearestProblematicPacket(HashMap<CellPerception, List<Coordinate>> pathsToProblPackets) {
        CellPerception nearestProblematicPacket = null;
        int shortestPathLength = Integer.MAX_VALUE;
        for(var probPacket : pathsToProblPackets.keySet()){
            if(shortestPathLength > pathsToProblPackets.get(probPacket).size()) {
                shortestPathLength = pathsToProblPackets.get(probPacket).size();
                nearestProblematicPacket = probPacket;
            }
        }
        return nearestProblematicPacket;
    }

    private void recordIsDestinationReachable(AgentState agentState, AgentMemoryFragment destinationMem) {
        var isDestReachableMem= agentState.getMemoryFragment("isDestinationReachable");
        if(isDestReachableMem == null || !isDestReachableMem.getReachable()){
            var reachableCoord = agentState.getPerception().calculateRoute(Coordinate.of(agentState.getX(), agentState.getY()), destinationMem.getCoordinate());
            agentState.addMemoryFragment("isDestinationReachable", new AgentMemoryFragment(!reachableCoord.isEmpty()));
        }
    }

    private HashMap<CellPerception, List<Coordinate>> getPathsToProblPackets(AgentState agentState) {
        var packetCellsForColor= agentState.getPerception().getPacketCellsForColor(agentState.getColor().get());
        HashMap<CellPerception,List<Coordinate>> problematicPackets = new HashMap<>();
        for(CellPerception packetCell : packetCellsForColor){
            List<Coordinate> reachable = agentState.getPerception().calculateRoute(Coordinate.of(agentState.getX(), agentState.getY()), packetCell.toCoordinate());

            if(agentState.getPerception().packetIsProblematic(generateTouchingCoordinates(packetCell.toCoordinate()), reachable,agentState.getMemoryFragment(agentState.getColor().get().toString()).getCoordinate(),packetCell.getCoordinate()))
            {
                problematicPackets.put(packetCell, reachable);
                System.out.println(agentState.getName() + " found a probl packet: ("+ packetCell.getX()+", "+packetCell.getY() + ")");
            }
        }
        return problematicPackets;
    }
}
