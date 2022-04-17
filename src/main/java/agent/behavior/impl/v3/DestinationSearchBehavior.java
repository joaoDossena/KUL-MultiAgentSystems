package agent.behavior.impl.v3;

import agent.AgentAction;
import agent.AgentState;
import environment.Coordinate;
import environment.Perception;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DestinationSearchBehavior extends SearchBehavior {

    @Override
    protected boolean doAction(AgentState agentState, AgentAction agentAction) {
        return false;
    }

    @Override
    protected List<Coordinate> getMovesInOrderRel(AgentState agentState) {

        var destinationMem = agentState.getMemoryFragment(agentState.getCarry().get().getColor().toString());

        if (destinationMem == null || destinationMem.getCoordinates() == null || destinationMem.getCoordinates().isEmpty()) {
            return getAllPermittedMovesInRandomOrderRel(agentState);
        }

        var neighboursAbs = Coordinate.of(agentState.getX(), agentState.getY()).getNeighboursAbsolute();
        var permittedMovesRel = agentState.getPerception().getPermittedMovesRel();

        return neighboursAbs.stream()
                .sorted(Comparator.comparing(coordinate -> euclideanDistance(coordinate, destinationMem.getCoordinates().get(0))))
                .map(coordinate -> Coordinate.of(coordinate.getX() - agentState.getX(), coordinate.getY() - agentState.getY()))
                .filter(permittedMovesRel::contains)
                .collect(Collectors.toCollection(ArrayList::new));
    }

}
