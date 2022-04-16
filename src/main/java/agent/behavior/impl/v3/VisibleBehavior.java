package agent.behavior.impl.v3;

import agent.AgentAction;
import agent.AgentState;
import environment.CellPerception;
import environment.Item;

import java.util.List;

public abstract class VisibleBehavior<T extends Item<?>> extends BehaviorV3 {

    protected abstract List<CellPerception> getTargets(AgentState agentState);

    @Override
    public final void act(AgentState agentState, AgentAction agentAction) {

        var minCell = agentState.getPerception().getClosestCell(getTargets(agentState), agentState.getX(), agentState.getY());

        if (minCell == null) {
            agentAction.skip();
            return;
        }

        var minMoveOpt = agentState.getPerception().getShortestMoveToCell(minCell, agentState.getX(), agentState.getY());

        if (minMoveOpt.isEmpty()) {
            agentAction.skip();
            return;
        }

        agentAction.step(agentState.getX() + minMoveOpt.get().getX(), agentState.getY() + minMoveOpt.get().getY());
    }
}
