package agent.behavior.impl.delegation;

import agent.AgentAction;
import agent.AgentMemoryFragment;
import agent.AgentState;
import agent.behavior.impl.wander.Wander;
import environment.CellPerception;
import environment.Coordinate;
import environment.Perception;

import java.util.List;

public class NoPacketBehavior extends Wander {

    @Override
    public void act(AgentState agentState, AgentAction agentAction) {

        Perception perception = agentState.getPerception();
        var neighbours = perception.getNeighbours();

        for(CellPerception neighbor : neighbours){
            if(neighbor != null && neighbor.containsPacketOfColor(agentState.getColor().get())){
                agentAction.pickPacket(neighbor.getX(), neighbor.getY());
                return;
            }
        }

        moveUsingAStar(agentState, agentAction);
    }

    private void moveUsingAStar(AgentState agentState, AgentAction agentAction) {
        Perception perception = agentState.getPerception();
        List<CellPerception> visiblePackets = perception.getPacketCellsForColor(agentState.getColor().get());
        Coordinate agentCoord = new Coordinate(agentState.getX(), agentState.getY());

        if(!visiblePackets.isEmpty()) {
            CellPerception packet = visiblePackets.get(0);
            List<Coordinate> path = perception.aStar(agentCoord, packet);
            if(path.isEmpty()) {
//                System.out.println("NoPacketBehaviour::moveUsingAStar: empty path");
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
