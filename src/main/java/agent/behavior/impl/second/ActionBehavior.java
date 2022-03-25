package agent.behavior.impl.second;

import agent.AgentAction;
import agent.AgentCommunication;
import agent.AgentState;
import agent.behavior.Behavior;
import environment.CellPerception;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;

public abstract class ActionBehavior extends Behavior {

    @Override
    public void communicate(AgentState agentState, AgentCommunication agentCommunication) {
        // No communication
    }

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
