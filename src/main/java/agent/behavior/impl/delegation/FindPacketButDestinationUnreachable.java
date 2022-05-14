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
        var destination=agentState.getPerception().getDestinationCells(agentState.getColor().get());
        if(destination!=null) {
            agentState.addMemoryFragment(agentState.getColor().get().toString(), new AgentMemoryFragment(destination.get(0).getCoordinate()));
        }
        var destinationMem = agentState.getMemoryFragment(agentState.getColor().get().toString());
        if (destinationMem != null) {
            var reachableDestMem= agentState.getMemoryFragment("isDestinationReachable");
            if(reachableDestMem == null || !reachableDestMem.getReachable()){
                Optional<Coordinate> reachableCoord = agentState.getPerception().isReachable(new Coordinate(agentState.getX(), agentState.getY()), destinationMem.getCoordinate());
                if(reachableCoord.isPresent())
                    agentState.addMemoryFragment("isDestinationReachable",new AgentMemoryFragment(reachableCoord.get()));
                else throw new RuntimeException("FoundPacketButDestinationUnreachable: Trying to add destination to memory when it is unreachable");
            }
            var destinationCoordinate = destinationMem.getCoordinate();
            var targetCells=agentState.getPerception().getPacketCellsForColor(agentState.getColor().get());
            for (CellPerception cellPerception:targetCells){
                if(agentState.getPerception().packetIsProblematic(generateAllMovesFromCoordinate(cellPerception.toCoordinate()),cellPerception.toCoordinate(),new Coordinate(agentState.getX(),agentState.getY()),destinationCoordinate))
                {
                    System.out.println("a problematic packet found on:"+ cellPerception.getX()+","+cellPerception.getY());
                }
            }
        }
        else{
            System.out.println("findPacketButDestinationUnreachable destination error "+ agentState.getName());
        }

    }
}
