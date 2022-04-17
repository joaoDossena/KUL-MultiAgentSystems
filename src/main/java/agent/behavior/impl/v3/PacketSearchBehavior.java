package agent.behavior.impl.v3;

import agent.AgentAction;
import agent.AgentState;
import environment.Coordinate;

import java.util.List;

public class PacketSearchBehavior extends SearchBehavior {

    @Override
    protected boolean doAction(AgentState agentState, AgentAction agentAction) {
        return false;
    }

    @Override
    protected List<Coordinate> getMovesInOrderRel(AgentState agentState) {

        return getAllPermittedMovesInRandomOrderRel(agentState);
    }
}
