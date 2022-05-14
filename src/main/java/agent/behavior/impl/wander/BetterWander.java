package agent.behavior.impl.wander;

import agent.AgentAction;
import agent.AgentState;
import environment.CellPerception;
import environment.Coordinate;
import environment.Perception;

import java.util.List;

public class BetterWander extends Wander {

    @Override
    public void act(AgentState agentState, AgentAction agentAction) {

        if (agentState.hasCarry()) {

            Perception perception = agentState.getPerception();

            for (CellPerception neighbor : perception.getNeighbours()) {
                if (neighbor != null && neighbor.containsDestination(agentState.getCarry().get().getColor())) {
                    agentAction.putPacket(neighbor.getX(), neighbor.getY());
                    return;
                }
            }
        } else {
            Perception perception = agentState.getPerception();

            for (CellPerception neighbor : perception.getNeighbours()) {
                if (neighbor != null && neighbor.containsPacket()) {
                    agentAction.pickPacket(neighbor.getX(), neighbor.getY());
                    return;
                }
            }
        }

        super.act(agentState, agentAction);
    }

    protected List<Coordinate> prioritizeWithManhattan(List<Coordinate> possibleCurrentMoves, Perception currPerception, Coordinate destinationCoordinates) {
        return currPerception.sortWithManhattanDistance(possibleCurrentMoves,destinationCoordinates.getX(),destinationCoordinates.getY());
    }

}
