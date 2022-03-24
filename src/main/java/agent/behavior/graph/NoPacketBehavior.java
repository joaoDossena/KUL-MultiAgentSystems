package agent.behavior.graph;

import agent.AgentAction;
import agent.AgentState;
import agent.behavior.wander.Wander;
import environment.CellPerception;
import environment.Perception;

public class NoPacketBehavior extends Wander {

    @Override
    public void act(AgentState agentState, AgentAction agentAction) {

        Perception perception = agentState.getPerception();

        for (CellPerception neighbor : perception.getNeighbours()) {
            if (neighbor != null && neighbor.containsPacket()) {
                agentAction.pickPacket(neighbor.getX(), neighbor.getY());
                return;
            }
        }
        super.act(agentState, agentAction);
    }
}
