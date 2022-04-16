package agent.behavior.impl.v3;

import agent.AgentAction;
import agent.AgentState;
import environment.CellPerception;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public abstract class ActionBehavior extends BehaviorV3 {

    @Override
    public final void act(AgentState agentState, AgentAction agentAction) {

        Optional<CellPerception> optionalTarget = Arrays.stream(agentState.getPerception().getNeighbours())
                .filter(Objects::nonNull)
                .filter(getContainsTargetPredicate(agentState))
                .findFirst();

        if(optionalTarget.isEmpty()) {
            agentAction.skip();
            return;
        }

        doAction(agentAction, optionalTarget.get());
    }

    protected abstract void doAction(AgentAction agentAction, CellPerception cell);

    protected abstract Predicate<CellPerception> getContainsTargetPredicate(AgentState agentState);
}
