package agent.behavior.impl.first;

import agent.AgentAction;
import agent.AgentCommunication;
import agent.AgentMemoryFragment;
import agent.AgentState;
import agent.behavior.impl.wander.Wander;
import environment.CellPerception;
import environment.Coordinate;
import environment.Mail;
import environment.Perception;
import environment.world.agent.AgentRep;
import environment.world.energystation.EnergyStation;

import java.util.ArrayList;
import java.util.List;

public class NoPacketBehavior extends Wander {

    @Override
    public void act(AgentState agentState, AgentAction agentAction) {
        Perception perception = agentState.getPerception();
        checkForEnergyStations(agentState,perception);
        checkEnergyLevel(agentState);
        var neighbours = perception.getNeighbours();

        for(CellPerception neighbor : neighbours){
            if(neighbor != null && neighbor.containsPacket()){
                agentAction.pickPacket(neighbor.getX(), neighbor.getY());
                return;
            }
        }

        walkTowardsClosestPacket(agentState, agentAction);
    }

    private void walkTowardsClosestPacket(AgentState agentState, AgentAction agentAction) {
        Perception perception = agentState.getPerception();
        List<CellPerception> packets = perception.getPacketCells();

        // If no packets in perception, wander randomly.
        if(packets.isEmpty()){
            super.act(agentState, agentAction);
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

        agentState.addMemoryFragment("lastMove", new AgentMemoryFragment(new Coordinate(minMove.getX(), minMove.getY())));
        agentAction.step(agentState.getX() + minMove.getX(), agentState.getY() + minMove.getY());
    }
}
