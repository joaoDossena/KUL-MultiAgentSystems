package agent.behavior.impl.first;

import agent.AgentAction;
import agent.AgentMemoryFragment;
import agent.AgentState;
import agent.behavior.impl.wander.Wander;
import environment.CellPerception;
import environment.Coordinate;
import environment.Perception;
import environment.world.agent.Agent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HasPacketBehavior extends Wander {
    private final String ENERGY_STATIONS = "EnergyStations";

    @Override
    public void act(AgentState agentState, AgentAction agentAction) {
        Perception perception = agentState.getPerception();
        if(goToStation(agentAction, perception, agentState)) return;
        checkForEnergyStations(agentState, perception);
        CellPerception[] neighbours = perception.getNeighbours();

        for(CellPerception neighbor : neighbours){
            if(neighbor != null && neighbor.containsDestination(agentState.getCarry().get().getColor())){
                agentAction.putPacket(neighbor.getX(), neighbor.getY());
                return;
            }

        }
        walkTowardsClosestDestination(agentState, agentAction);
    }

    private boolean goToStation(AgentAction agentAction,Perception perception,AgentState agentState){
        int currentEnergy = agentState.getBatteryState();
        AgentMemoryFragment memoryfragment = agentState.getMemoryFragment(ENERGY_STATIONS);
        if(memoryfragment == null) return false;

        List<Coordinate> stations = memoryfragment.getCoordinates();
        stations = perception.shortWithManhattanDistance(stations,agentState.getX(),agentState.getY());
        if((currentEnergy-calculateDistanceWithEnergy(10, agentState, new Coordinate(stations.get(0).getX(), stations.get(0).getY()-1))) < 405){
            placePacketDown(agentAction, agentState);
            return true;
        }
        return false;
    }

    public boolean placePacketDown(AgentAction agentAction,AgentState agentState){
        Perception perception = agentState.getPerception();
        List<Coordinate> coordinates = generatePossibleAbsolutePositions(agentState.getX(),agentState.getY());
        coordinates = returnListToRelative(coordinates, agentState.getX(), agentState.getY());
        AgentMemoryFragment memoryFragment = agentState.getMemoryFragment(agentState.getCarry().get().getColor().toString());
        if(memoryFragment != null)
            coordinates = perception.shortWithManhattanDistance(coordinates,memoryFragment.getCoordinates().get(0).getX(),memoryFragment.getCoordinates().get(0).getY());
        for(Coordinate c : coordinates){
            if(perception.getCellPerceptionOnRelPos(c.getX(), c.getY()) != null && perception.getCellPerceptionOnRelPos(c.getX(),c.getY()).isWalkable()
                && !perception.getCellPerceptionOnRelPos(c.getX(), c.getY()).containsEnergyStation()){
                agentAction.putPacket(agentState.getX() + c.getX(),agentState.getY() + c.getY());
                return true;
            }
        }
        return false;
    }


    private void walkTowardsClosestDestination(AgentState agentState, AgentAction agentAction) {
        Perception perception = agentState.getPerception();
        List<CellPerception> destinations = perception.getDestinationCells(agentState.getCarry().get().getColor());

        // If no destinations in perception, wander randomly.
        if(destinations.isEmpty()){
            if (agentState.getMemoryFragment(agentState.getCarry().get().getColor().toString()) != null) {
                AgentMemoryFragment destinationCoordinates = agentState.getMemoryFragment(agentState.getCarry().get().getColor().toString());
                AgentMemoryFragment fragment = agentState.getMemoryFragment("lastMove");
                Coordinate undoPreviousMove = null, previousMove;
                List<Coordinate> possibleNewLocations = generatePossibleAbsolutePositions(agentState.getX(),agentState.getY());
                List<Coordinate> destinationSortedCoordinates=prioritizeWithManhattan(possibleNewLocations,agentState.getPerception(),destinationCoordinates.getCoordinates().get(0));
                List<Coordinate> relativeSortedCoordinates=returnListToRelative(destinationSortedCoordinates,agentState.getX(),agentState.getY());
                List<Coordinate> accessibleFromPreviousAndCurrent = null;

                if (fragment != null) {
                    previousMove = fragment.getCoordinates().get(0);
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

        Optional<Coordinate> minMoveOpt = perception.getShortestMoveToCell(minCell, agentState.getX(), agentState.getY());
        if(minMoveOpt.isEmpty()){
            agentAction.skip();
            return;
        }
        Coordinate minMove = minMoveOpt.get();

        agentState.addMemoryFragment("lastMove", new AgentMemoryFragment(new Coordinate(minMove.getX(), minMove.getY())));
        agentAction.step(agentState.getX() + minMove.getX(), agentState.getY() + minMove.getY());
    }
}
