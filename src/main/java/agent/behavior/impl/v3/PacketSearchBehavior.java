package agent.behavior.impl.v3;

import agent.AgentAction;
import agent.AgentState;
import environment.Coordinate;

import java.util.Collections;
import java.util.List;

public class PacketSearchBehavior extends SearchBehavior {

    @Override
    protected boolean doAction(AgentState agentState, AgentAction agentAction) {
        return false;
    }

    @Override
    protected List<Coordinate> getMovesInOrder(AgentState agentState) {

        var permittedMovesRel = agentState.getPerception().getPermittedMovesRel();
        Collections.shuffle(permittedMovesRel);
        System.out.println("Agent Name: " + agentState.getName() + ", Agent Pos: " + agentState.getX() + agentState.getY() + ", permittedRel: " + permittedMovesRel);
        return permittedMovesRel;
    }
}
