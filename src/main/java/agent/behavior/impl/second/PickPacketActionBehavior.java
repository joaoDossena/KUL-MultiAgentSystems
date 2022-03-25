package agent.behavior.impl.second;

import agent.AgentAction;
import agent.AgentState;
import environment.CellPerception;
import java.util.function.Predicate;

public class PickPacketActionBehavior extends ActionBehavior {

    @Override
    protected void doAction(AgentAction agentAction, CellPerception cell) {
        agentAction.pickPacket(cell.getX(), cell.getY());
    }

    @Override
    protected Predicate<CellPerception> getContainsTargetPredicate(AgentState agentState) {
        return CellPerception::containsPacket;
    }
}
