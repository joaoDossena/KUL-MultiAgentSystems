package agent.behavior.graph;

import agent.AgentAction;
import agent.AgentState;
import agent.behavior.wander.Wander;
import environment.CellPerception;
import environment.Coordinate;
import environment.Perception;

import java.util.ArrayList;
import java.util.List;

public class HasPacketBehavior extends Wander {

    @Override
    public void act(AgentState agentState, AgentAction agentAction) {

        Perception perception = agentState.getPerception();
        var neighbours = perception.getNeighbours();

        for(CellPerception neighbor : neighbours){
            if(neighbor != null && neighbor.containsDestination(agentState.getCarry().get().getColor())){
                agentAction.putPacket(neighbor.getX(), neighbor.getY());
                return;
            }
        }
        walkTowardsClosestDestination(agentState, agentAction);
    }

    private void walkTowardsClosestDestination(AgentState agentState, AgentAction agentAction) {
        Perception perception = agentState.getPerception();
        List<CellPerception> destinations = perception.getDestinationCells(agentState.getCarry().get().getColor());

        // If no destinations in perception, wander randomly.
        if(destinations.isEmpty()){
            super.act(agentState, agentAction);
            return;
        }

        // Otherwise, look for closest destination.
        CellPerception minCell = perception.getClosestCell(destinations, agentState.getX(), agentState.getY());

        // Find the closest walkable move in the direction of minCell
        List<Coordinate> moves = new ArrayList<>(List.of(
                new Coordinate(1, 1), new Coordinate(-1, -1),
                new Coordinate(1, -1), new Coordinate(-1, 1),
                new Coordinate(1, 0), new Coordinate(-1, 0),
                new Coordinate(0, 1), new Coordinate(0, -1)
        ));

        Coordinate minMove = perception.getShortestMoveToCell(minCell, moves, agentState.getX(), agentState.getY());

        agentAction.step(agentState.getX() + minMove.getX(), agentState.getY() + minMove.getY());
    }
}
