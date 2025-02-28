package agent.behavior.impl.wander;

import agent.AgentAction;
import agent.AgentCommunication;
import agent.AgentMemoryFragment;
import agent.AgentState;
import agent.behavior.Behavior;
import environment.CellPerception;
import environment.Coordinate;
import environment.Mail;
import environment.Perception;
import environment.world.agent.AgentRep;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



public class Wander extends Behavior {
    private final String ENERGY_STATIONS = "EnergyStations";
    @Override
    public void communicate(AgentState agentState, AgentCommunication agentCommunication) {
        communicateStations(agentCommunication,agentState.getPerception(), agentState);
    }


    private void communicateStations(AgentCommunication agentCommunication,Perception perception, AgentState agentState) {

        var msgs=agentCommunication.getMessages();

        var energyStations=agentState.getMemoryFragment(ENERGY_STATIONS);
        for(int i=0;i< msgs.size();i++) {
            if (msgs.get(i).getCoordinates() != null) {
                if (energyStations != null) {
                    for (Coordinate cor : msgs.get(i).getCoordinates()) {
                        energyStations.addToCoordinatesList(cor);
                    }
                } else {
                    agentState.addMemoryFragment(ENERGY_STATIONS, new AgentMemoryFragment(msgs.get(i).getCoordinates()));
                    continue;
                }

                agentCommunication.removeMessage(i);
                agentState.addMemoryFragment(ENERGY_STATIONS, energyStations);
            }
        }

        var energyStates=agentState.getMemoryFragment(ENERGY_STATIONS);
        if(energyStates!=null) {
            ArrayList<AgentRep> visibleAgents = perception.getVisibleAgents();
            for(AgentRep agentRep:visibleAgents){
                agentCommunication.sendMessage(agentRep,energyStates.getCoordinates());
            }
        }
    }

    protected List<Coordinate> generatePossibleAbsolutePositions(int x, int y) {
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

    protected List<Coordinate> prioritizeWithManhattan(List<Coordinate> possibleCurrentMoves, Perception currPerception, Coordinate destinationCoordinates) {
        return currPerception.shortWithManhattanDistance(possibleCurrentMoves,destinationCoordinates.getX(),destinationCoordinates.getY());
    }
    protected List<Coordinate> returnListToRelative(List<Coordinate> destinationSortedCoordinates, int x, int y) {
        List<Coordinate> relativeCoordinates=new ArrayList<>();
        for(Coordinate cor : destinationSortedCoordinates){
            relativeCoordinates.add(new Coordinate(cor.getX()-x,cor.getY()-y));
        }
        return relativeCoordinates;
    }


    @Override
    public void act(AgentState agentState, AgentAction agentAction) {
        AgentMemoryFragment fragment = agentState.getMemoryFragment("lastMove");
        Coordinate undoPreviousMove = null, previousMove;
        List<Coordinate> possibleCurrentMoves = generateAllMovesFromCoordinate(new Coordinate(0, 0));
        List<Coordinate> accessibleFromPreviousAndCurrent = null;

        if (fragment != null) {
            previousMove = fragment.getCoordinates().get(0);
            undoPreviousMove = previousMove.invertedSign();
            accessibleFromPreviousAndCurrent = commonElements(possibleCurrentMoves, generateAllMovesFromCoordinate(undoPreviousMove));
            accessibleFromPreviousAndCurrent.add(undoPreviousMove);
        }

        // Potential moves an agent can make (radius of 1 around the agent)
        List<Coordinate> moves = possibleCurrentMoves;
        if (accessibleFromPreviousAndCurrent != null) {
            moves = removeIntersection(possibleCurrentMoves, accessibleFromPreviousAndCurrent); // priority #1
            Collections.shuffle(moves);
            moves.addAll(accessibleFromPreviousAndCurrent);
        }

        var perception = agentState.getPerception();
        addDestinationsIfFound(perception,agentState);
        var optimizedMoves = new ArrayList<Coordinate>(avoidWorthlessMoves(moves, perception));
        //TODO first use prioritized move else move Randomly

        performMove(agentState, agentAction, optimizedMoves);
    }



    protected void checkForEnergyStations(AgentState agentState, Perception perception){
        List<CellPerception> chargers = perception.getEnergyStations();
        for(CellPerception station : chargers) {
            var stations = agentState.getMemoryFragment(ENERGY_STATIONS);
            if (stations == null) {
                agentState.addMemoryFragment(ENERGY_STATIONS, new AgentMemoryFragment(new Coordinate(station.getX(), station.getY())));
            }
            else{
                stations.addToCoordinatesList(station.toCoordinate());
                agentState.addMemoryFragment(ENERGY_STATIONS,stations);
            }
        }
    }


    protected void addDestinationsIfFound(Perception perception, AgentState agentState) {
        var destinations=perception.getDestinationCells();
        destinations.forEach((k, v) -> agentState.addMemoryFragment(k.toString(),new AgentMemoryFragment(new Coordinate(v.getX(),v.getY()))));
    }

    public List<Coordinate> avoidWorthlessMoves(List<Coordinate> moves, Perception perception) {
        var optimizedMoves = new ArrayList<Coordinate>(moves);
        var vision = perception.getAllVision();

        if (perception.getSelfX() == 1 || perception.getSelfX() == 2) {
            optimizedMoves.remove(new Coordinate(-1, 0));
            optimizedMoves.add(new Coordinate(-1, 0));
        } else if (perception.getSelfX() == vision.length - 2 || perception.getSelfX() == vision.length - 3) {
            optimizedMoves.remove(new Coordinate(1, 0));
            optimizedMoves.add(new Coordinate(1, 0));
        }

        if (perception.getSelfY() == 1 || perception.getSelfY() == 2) {
            optimizedMoves.remove(new Coordinate(0, -1));
            optimizedMoves.add(new Coordinate(0, -1));
        } else if (perception.getSelfY() == vision.length - 2 || perception.getSelfY() == vision.length - 3) {
            optimizedMoves.remove(new Coordinate(0, 1));
            optimizedMoves.add(new Coordinate(0, 1));
        }

        return optimizedMoves;
    }

    public void performMove(AgentState agentState, AgentAction agentAction, List<Coordinate> moves) {

        // Check for viable moves
        for (var move : moves) {
            var perception = agentState.getPerception();
            int x = move.getX();
            int y = move.getY();

            // If the area is null, it is outside the bounds of the environment
            //  (when the agent is at any edge for example some moves are not possible)
            if (perception.getCellPerceptionOnRelPos(x, y) != null && perception.getCellPerceptionOnRelPos(x, y).isWalkable() && !perception.getCellPerceptionOnRelPos(x, y).containsAgent()) {
                agentState.addMemoryFragment("lastMove", new AgentMemoryFragment(new Coordinate(x, y)));
               // System.out.println("Step Agent ID:"+ agentState.getName());
                agentAction.step(agentState.getX() + x, agentState.getY() + y);
                var cor=agentState.getMemoryFragment(ENERGY_STATIONS);
                if(cor==null)
                    System.out.println(agentState.getName() + " No energy Stations known");
                else
                    System.out.println(agentState.getName()+ " stations: "+cor.getCoordinates().size());
                return;
            }
        }
        //System.out.println("Skip Agent ID:"+ agentState.getName());
        agentAction.skip();
    }

    public List<Coordinate> removeIntersection(List<Coordinate> moves, List<Coordinate> forbiddenMoves) {
        List<Coordinate> res = new ArrayList<>();
        for (Coordinate move : moves) {
            if (!forbiddenMoves.contains(move))
                res.add(move);
        }
        return res;
    }

    public List<Coordinate> generateAllMovesFromCoordinate(Coordinate coordinate) {
        List<Coordinate> res = new ArrayList<>();

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                if (x == 0 && y == 0)
                    continue;
                res.add(new Coordinate(coordinate.getX() + x, coordinate.getY() + y));
            }
        }
        return res;
    }

    public List<Coordinate> commonElements(List<Coordinate> l1, List<Coordinate> l2) {
        List<Coordinate> res = new ArrayList<>();
        for (Coordinate c1 : l1) {
            if (l2.contains(c1))
                res.add(c1);
        }
        return res;
    }

    protected void walkTowardsCoordinate(AgentAction agentAction,AgentState agentState,Coordinate coordinate){
        //AgentMemoryFragment fragment = agentState.getMemoryFragment("lastMove");
        //Coordinate undoPreviousMove = null, previousMove;
        List<Coordinate> possibleNewLocations = generatePossibleAbsolutePositions(agentState.getX(),agentState.getY());
        List<Coordinate> destinationSortedCoordinates=prioritizeWithManhattan(possibleNewLocations,agentState.getPerception(),coordinate);
        List<Coordinate> relativeSortedCoordinates=returnListToRelative(destinationSortedCoordinates,agentState.getX(),agentState.getY());
        List<Coordinate> accessibleFromPreviousAndCurrent = null;

        //if (fragment != null) {
        //    previousMove = fragment.getCoordinates().get(0);
        //    undoPreviousMove = previousMove.invertedSign();
        //    accessibleFromPreviousAndCurrent = commonElements(relativeSortedCoordinates, generateAllMovesFromCoordinate(undoPreviousMove));
        //    accessibleFromPreviousAndCurrent.add(undoPreviousMove);
        //}

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
    }

    protected int calculateDistanceWithEnergy(int energyOfMoves, AgentState agentState, Coordinate coordinate){
        Perception perception=agentState.getPerception();
        return energyOfMoves*perception.manhattanDistance(agentState.getX(), agentState.getY(),coordinate.getX(),coordinate.getY());
    }

}
