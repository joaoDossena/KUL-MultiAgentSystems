package agent.behavior.betterWander;

import agent.AgentAction;
import agent.AgentCommunication;
import agent.AgentState;
import agent.behavior.Behavior;
import environment.CellPerception;
import environment.Coordinate;
import environment.Perception;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BetterWander extends Behavior  {
    @Override
    public void communicate(AgentState agentState, AgentCommunication agentCommunication) {
        // No communication
    }


    @Override
    public void act(AgentState agentState, AgentAction agentAction) {
        if(agentState.hasCarry()){
            Perception perception = agentState.getPerception();
            var neighbours = perception.getNeighbours();

            for(CellPerception neighbor : neighbours){
                if(neighbor != null && neighbor.containsDestination(agentState.getCarry().get().getColor())){
                    agentAction.putPacket(neighbor.getX(), neighbor.getY());
                    return;
                }
            }
            walkTowardsClosestDestination(agentState, agentAction);
            return;
        }

        else{
            Perception perception = agentState.getPerception();
            var neighbours = perception.getNeighbours();

            for(CellPerception neighbor : neighbours){
                if(neighbor != null && neighbor.containsPacket()){
                    agentAction.pickPacket(neighbor.getX(), neighbor.getY());
                    return;
                }
            }

            walkTowardsClosestPacket(agentState, agentAction);
            return;
        }

//        wander(agentState, agentAction);
    }

    private void wander(AgentState agentState, AgentAction agentAction){
        // Potential moves an agent can make (radius of 1 around the agent)
        List<Coordinate> moves = new ArrayList<>(List.of(
                new Coordinate(1, 1), new Coordinate(-1, -1),
                new Coordinate(1, 0), new Coordinate(-1, 0),
                new Coordinate(0, 1), new Coordinate(0, -1),
                new Coordinate(1, -1), new Coordinate(-1, 1)
        ));

        // Shuffle moves randomly
        Collections.shuffle(moves);

        // Check for viable moves
        for (var move : moves) {
            var perception = agentState.getPerception();
            int x = move.getX();
            int y = move.getY();

            // If the area is null, it is outside the bounds of the environment
            //  (when the agent is at any edge for example some moves are not possible)
            if (perception.getCellPerceptionOnRelPos(x, y) != null && perception.getCellPerceptionOnRelPos(x, y).isWalkable()) {
                agentAction.step(agentState.getX() + x, agentState.getY() + y);
                return;
            }
        }

        // No viable moves, skip turn
        agentAction.skip();
    }

    //TODO: refactor this horrible code later
    private void walkTowardsClosestPacket(AgentState agentState, AgentAction agentAction) {
        Perception perception = agentState.getPerception();
        List<CellPerception> packets = perception.getPacketCells();

        // If no packets in perception, wander randomly.
        if(packets.isEmpty()){
            wander(agentState, agentAction);
            return;
        }

        // Otherwise, look for closest packet.
        CellPerception minCell = perception.getClosestCell(packets, agentState.getX(), agentState.getY());

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

    //TODO: refactor this horrible code later
    private void walkTowardsClosestDestination(AgentState agentState, AgentAction agentAction) {
        Perception perception = agentState.getPerception();
        List<CellPerception> destinations = perception.getDestinationCells(agentState.getCarry().get().getColor());

        // If no destinations in perception, wander randomly.
        if(destinations.isEmpty()){
            wander(agentState, agentAction);
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
