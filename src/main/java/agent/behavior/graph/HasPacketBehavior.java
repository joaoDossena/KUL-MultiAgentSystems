package agent.behavior.graph;

import agent.AgentAction;
import agent.AgentState;
import agent.behavior.wander.Wander;
import environment.CellPerception;
import environment.Perception;

public class HasPacketBehavior extends Wander {

    @Override
    public void act(AgentState agentState, AgentAction agentAction) {

        Perception perception = agentState.getPerception();

        for (CellPerception neighbor : perception.getNeighbours()) {
            if (neighbor != null && neighbor.containsDestination(agentState.getCarry().get().getColor())) {
                agentAction.putPacket(neighbor.getX(), neighbor.getY());
                return;
            }
        }
        super.act(agentState, agentAction);
    }
}
