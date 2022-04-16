package agent.behavior.impl.v3;

import agent.AgentAction;
import agent.AgentState;
import environment.CellPerception;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;

public abstract class ActionBehavior extends BehaviorV3 {

    @Override
    public void act(AgentState agentState, AgentAction agentAction) {

        Arrays.stream(agentState.getPerception().getNeighbours())
                .filter(Objects::nonNull)
                .filter(getContainsTargetPredicate(agentState))
                .findFirst()
                .ifPresent(cell -> doAction(agentAction, cell));
    }

    protected abstract void doAction(AgentAction agentAction, CellPerception cell);

    protected abstract Predicate<CellPerception> getContainsTargetPredicate(AgentState agentState);
}
