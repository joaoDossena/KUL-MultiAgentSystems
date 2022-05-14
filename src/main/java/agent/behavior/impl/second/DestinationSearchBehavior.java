package agent.behavior.impl.second;

import agent.AgentAction;
import agent.AgentMemoryFragment;
import agent.AgentState;
import environment.Coordinate;
import environment.Perception;

import java.util.ArrayList;
import java.util.List;

public class DestinationSearchBehavior extends SearchBehavior {

    @Override
    public void act(AgentState agentState, AgentAction agentAction) {
        if (agentState.getMemoryFragment(agentState.getCarry().get().getColor().toString()) != null) {
            var destinationCoordinates = agentState.getMemoryFragment(agentState.getCarry().get().getColor().toString());
            AgentMemoryFragment fragment = agentState.getMemoryFragment("lastMove");
            Coordinate undoPreviousMove = null, previousMove;
            List<Coordinate> possibleNewLocations = generatePossibleAbsolutePositions(agentState.getX(),agentState.getY());
            List<Coordinate> destinationSortedCoordinates=prioritizeWithManhattan(possibleNewLocations,agentState.getPerception(),destinationCoordinates.getCoordinate());
            List<Coordinate> relativeSortedCoordinates=returnListToRelative(destinationSortedCoordinates,agentState.getX(),agentState.getY());
            List<Coordinate> accessibleFromPreviousAndCurrent = null;

            if (fragment != null) {
                previousMove = fragment.getCoordinate();
                undoPreviousMove = previousMove.invertedSign();
                accessibleFromPreviousAndCurrent = commonElements(relativeSortedCoordinates, generateAllMovesFromCoordinate(undoPreviousMove));
                accessibleFromPreviousAndCurrent.add(undoPreviousMove);
            }

            // Potential moves an agent can make (radius of 1 around the agent)
            List<Coordinate> moves = relativeSortedCoordinates;
            if (accessibleFromPreviousAndCurrent != null) {
                moves = removeIntersection(relativeSortedCoordinates, accessibleFromPreviousAndCurrent); // priority #1
                moves.addAll(accessibleFromPreviousAndCurrent);
            }

            var perception = agentState.getPerception();
            addDestinationsIfFound(perception, agentState);
            var optimizedMoves = new ArrayList<>(avoidWorthlessMoves(moves, perception));
            //TODO first use prioritized move else move Randomly
            performMove(agentState, agentAction, optimizedMoves);
            return;
        }
        super.act(agentState, agentAction);
    }

    private List<Coordinate> returnListToRelative(List<Coordinate> destinationSortedCoordinates, int x, int y) {
        List<Coordinate> relativeCoordinates=new ArrayList<>();
        for(Coordinate cor : destinationSortedCoordinates){
            relativeCoordinates.add(new Coordinate(cor.getX()-x,cor.getY()-y));
        }
        return relativeCoordinates;
    }

    private List<Coordinate> generatePossibleAbsolutePositions(int x, int y) {
        List<Coordinate> possiblePositions = new ArrayList<>();
        possiblePositions.add(new Coordinate(x-1,y-1));
        possiblePositions.add(new Coordinate(x,y-1));
        possiblePositions.add(new Coordinate(x+1,y-1));
        possiblePositions.add(new Coordinate(x,y+1));
        possiblePositions.add(new Coordinate(x+1,y+1));
        possiblePositions.add(new Coordinate(x-1,y+1));
        possiblePositions.add(new Coordinate(x-1,y));
        possiblePositions.add(new Coordinate(x+1,y));
        return possiblePositions;
    }

    private List<Coordinate> prioritizeWithManhattan(List<Coordinate> possibleCurrentMoves, Perception currPerception, Coordinate destinationCoordinates) {
        return currPerception.sortWithManhattanDistance(possibleCurrentMoves,destinationCoordinates.getX(),destinationCoordinates.getY());
    }


}
