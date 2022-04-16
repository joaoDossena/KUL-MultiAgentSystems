package agent.behavior.impl.v3;

import agent.AgentAction;
import agent.AgentState;
import environment.Coordinate;

import java.util.Collections;
import java.util.List;

public class PacketSearchBehavior extends SearchBehavior {

    @Override
    protected boolean initAct(AgentState agentState, AgentAction agentAction) {
        return false;
    }

    @Override
    protected List<Coordinate> getMovesInOrder(AgentState agentState) {

        var permittedMovesRel = agentState.getPerception().getPermittedMovesRel();
        Collections.shuffle(permittedMovesRel);
        return permittedMovesRel;
    }
}
