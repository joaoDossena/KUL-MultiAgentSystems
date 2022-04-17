package agent.behavior.impl.v3;

import agent.AgentAction;
import agent.AgentState;
import environment.CellPerception;
import environment.Coordinate;
import environment.Item;

import java.util.*;

public abstract class VisibleBehavior<T extends Item<?>> extends BehaviorV3 {

    protected abstract Optional<CellPerception> getTarget(AgentState agentState);

    protected abstract boolean doAction(AgentState agentState, AgentAction agentAction);

    @Override
    public final void act(AgentState agentState, AgentAction agentAction) {

        if (doAction(agentState, agentAction)) {
            return;
        }

        var target = getTarget(agentState);
        if (target.isEmpty()) {
            agentAction.skip();
            return;
        }

        var bestMoveOpt = findBestMove(target.get(), agentState);

        if (bestMoveOpt.isEmpty()) {
            agentAction.skip();
            return;
        }

        agentAction.step(bestMoveOpt.get().getX(), bestMoveOpt.get().getY());
    }
}
