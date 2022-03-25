package agent.behavior.impl.second;

import agent.AgentAction;
import agent.AgentState;
import environment.CellPerception;
import java.util.function.Predicate;

public class PutPacketActionBehavior extends ActionBehavior {

    @Override
    protected void doAction(AgentAction agentAction, CellPerception cell) {
        agentAction.putPacket(cell.getX(), cell.getY());
    }

    @Override
    protected Predicate<CellPerception> getContainsTargetPredicate(AgentState agentState) {
        return cellPerception -> cellPerception.containsDestination(agentState.getCarry().get().getColor());
    }

    @Override
    public void act(AgentState agentState, AgentAction agentAction) {

        super.act(agentState, agentAction);
    }
}
