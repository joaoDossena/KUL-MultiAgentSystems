package agent.behavior.impl.delegation;

import agent.AgentAction;
import agent.AgentMemoryFragment;
import agent.AgentState;
import agent.behavior.impl.wander.Wander;
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
//        walkTowardsClosestDestination(agentState, agentAction);
        moveUsingAStar(agentState, agentAction);
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

    private List<Coordinate> prioritizeWithManhattan(List<Coordinate> possibleCurrentMoves, Perception perception, Coordinate destinationCoordinates) {
        return perception.sortWithManhattanDistance(possibleCurrentMoves,destinationCoordinates.getX(),destinationCoordinates.getY());
    }

    private void walkTowardsClosestDestination(AgentState agentState, AgentAction agentAction) {
        Perception perception = agentState.getPerception();
        List<CellPerception> destinations = perception.getDestinationCells(agentState.getCarry().get().getColor());

        // If no destinations in perception, wander randomly.
        if(destinations.isEmpty()){
            if (agentState.getMemoryFragment(agentState.getCarry().get().getColor().toString()) != null) {
                var destinationCoordinates = agentState.getMemoryFragment(agentState.getCarry().get().getColor().toString());
                AgentMemoryFragment fragment = agentState.getMemoryFragment("lastMove");
                Coordinate undoPreviousMove = null, previousMove;
                List<Coordinate> possibleNewLocations = generatePossibleAbsolutePositions(agentState.getX(),agentState.getY());
                List<Coordinate> destinationSortedCoordinates = prioritizeWithManhattan(possibleNewLocations,agentState.getPerception(),destinationCoordinates.getCoordinate());
                List<Coordinate> relativeSortedCoordinates = returnListToRelative(destinationSortedCoordinates,agentState.getX(),agentState.getY());
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

                var perception1 = agentState.getPerception();
                addDestinationsIfFound(perception1, agentState);
                var optimizedMoves = new ArrayList<>(avoidWorthlessMoves(moves, perception1));
                //TODO first use prioritized move else move Randomly
                performMove(agentState, agentAction, optimizedMoves);
                return;
            }
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

        agentState.addMemoryFragment("lastMove", new AgentMemoryFragment(new Coordinate(minMove.getX(), minMove.getY())));
        agentAction.step(agentState.getX() + minMove.getX(), agentState.getY() + minMove.getY());
    }

    public void moveUsingAStar(AgentState agentState, AgentAction agentAction){
        Perception perception = agentState.getPerception();

        List<CellPerception> visibleDestinations = perception.getDestinationCells(agentState.getCarry().get().getColor());
        Coordinate agentCoord = new Coordinate(agentState.getX(), agentState.getY());

        if(!visibleDestinations.isEmpty()) {
            CellPerception dest = visibleDestinations.get(0);
            List<Coordinate> path = perception.aStar(agentCoord, dest);
            if(path.isEmpty()) {
                System.out.println("HasPacketBehaviour::moveUsingAStar: empty path");
            }
            else{
                Coordinate step = path.get(1);
                Coordinate lastMoveRelativeCoord = perception.getMoveFromAbsPositions(agentCoord, step);
                agentState.addMemoryFragment("lastMove", new AgentMemoryFragment(lastMoveRelativeCoord));
                agentAction.step(step.getX(), step.getY());
                return;
            }
        }

        super.act(agentState, agentAction);
    }
}
