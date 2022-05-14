package agent.behavior.impl.delegation;

import agent.AgentAction;
import agent.AgentState;
import agent.behavior.impl.wander.Wander;
import environment.CellPerception;
import environment.Coordinate;


import java.util.*;

public class FindPacketButDestinationUnreachable extends Wander {
    @Override
    public void act(AgentState agentState, AgentAction agentAction) {
        var destinationMem = agentState.getMemoryFragment(agentState.getColor().get().toString());
        if (destinationMem != null) {
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
