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
        if (destinationMem != null) {
            recordIsDestinationReachable(agentState, destinationMem);
            //Hasmap to contain list of Coordinates (Empty list in edge cases)
            HashMap<CellPerception, Coordinate> problematicPackets = getProblematicPackets(agentState);

            for(var probPacket : problematicPackets.keySet()){
                if(probPacket.getCoordinate().equals(problematicPackets.get(probPacket))){
                    agentAction.pickPacket(probPacket.getX(), probPacket.getY());
                    return;
                }
            }

            for(var probPacket : problematicPackets.keySet()){
                //sort by lists length
                agentAction.step(problematicPackets.get(probPacket).getX(), problematicPackets.get(probPacket).getY());
                return;
            }
            super.act(agentState,agentAction);
        }
        else{
            System.out.println("findPacketButDestinationUnreachable destination error "+ agentState.getName());
        }

    }

    private void recordIsDestinationReachable(AgentState agentState, AgentMemoryFragment destinationMem) {
        var isDestReachableMem= agentState.getMemoryFragment("isDestinationReachable");
        if(isDestReachableMem == null || !isDestReachableMem.getReachable()){
            Optional<Coordinate> reachableCoord = agentState.getPerception().isReachable(new Coordinate(agentState.getX(), agentState.getY()), destinationMem.getCoordinate());
            agentState.addMemoryFragment("isDestinationReachable", new AgentMemoryFragment(reachableCoord.isPresent()));
        }
    }

    private HashMap<CellPerception, Coordinate> getProblematicPackets(AgentState agentState) {
        var packetCellsForColor= agentState.getPerception().getPacketCellsForColor(agentState.getColor().get());
        HashMap<CellPerception,Coordinate> problematicPackets = new HashMap<>();
        for(CellPerception packetCell : packetCellsForColor){
            var reachable= agentState.getPerception().isReachable(new Coordinate(agentState.getX(), agentState.getY()),packetCell.toCoordinate());

            if(agentState.getPerception().packetIsProblematic(generateAllMovesFromCoordinate(packetCell.toCoordinate()), reachable))
            {
                problematicPackets.put(packetCell, reachable.get());
                System.out.println(agentState.getName() + " found a probl packet: ("+ packetCell.getX()+", "+packetCell.getY() + ")");
            }
        }
        return problematicPackets;
    }
}
