package agent.behavior.impl.v3;

import agent.AgentAction;
import agent.AgentState;
import environment.Coordinate;
import environment.Perception;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DestinationSearchBehavior extends SearchBehavior {

    @Override
    protected boolean initAct(AgentState agentState, AgentAction agentAction) {
        return false;
    }

    @Override
    protected List<Coordinate> getMovesInOrder(AgentState agentState) {

        var destinationMem = agentState.getMemoryFragment(agentState.getCarry().get().getColor().toString());
        if (destinationMem == null || destinationMem.getCoordinates() == null || destinationMem.getCoordinates().isEmpty()) {
            var permittedMovesRel = agentState.getPerception().getPermittedMovesRel();
            Collections.shuffle(permittedMovesRel);
            return permittedMovesRel;
        }

        var possibleNewLocations = Coordinate.of(agentState.getX(), agentState.getY()).getNeighboursAbsolute();
        var destinationSortedCoordinates = prioritizeWithManhattan(possibleNewLocations, agentState.getPerception(), destinationMem.getCoordinates().get(0));
        var relativeSortedCoordinates = returnListToRelative(destinationSortedCoordinates, agentState.getX(), agentState.getY());

        relativeSortedCoordinates.removeIf(c -> !agentState.getPerception().getPermittedMovesRel().contains(c));

        return relativeSortedCoordinates;
    }

    protected List<Coordinate> prioritizeWithManhattan(List<Coordinate> possibleCurrentMoves, Perception currPerception, Coordinate destinationCoordinates) {
        return currPerception.shortWithManhattanDistance(possibleCurrentMoves, destinationCoordinates.getX(), destinationCoordinates.getY());
    }

    protected List<Coordinate> returnListToRelative(List<Coordinate> destinationSortedCoordinates, int x, int y) {
        List<Coordinate> relativeCoordinates = new ArrayList<>();
        for (Coordinate cor : destinationSortedCoordinates) {
            relativeCoordinates.add(new Coordinate(cor.getX() - x, cor.getY() - y));
        }
        return relativeCoordinates;
    }
}
