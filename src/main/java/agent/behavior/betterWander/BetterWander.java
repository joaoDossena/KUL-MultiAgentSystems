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
        }

        wander(agentState, agentAction);
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

    private void betterWander(AgentState agentState, AgentAction agentAction) {
        Perception perception = agentState.getPerception();
        List<CellPerception> packets = perception.getPacketCells();

        if(packets.isEmpty()){
            wander(agentState, agentAction);
            return;
        }

        CellPerception minCell = packets.get(0);
        int minDistance = perception.manhattanDistance(minCell.getX(), minCell.getY(), agentState.getX(), agentState.getY());
        for(int i = 1; i < packets.size(); i++) {
            int distance = perception.manhattanDistance(packets.get(i).getX(), packets.get(i).getY(), agentState.getX(), agentState.getY());
            if(distance < minDistance){
                minDistance = distance;
                minCell = packets.get(i);
            }
        }

        // Find the closest walkable move in the direction of minCell

    }
}
