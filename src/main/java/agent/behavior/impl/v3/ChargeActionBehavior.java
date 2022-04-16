package agent.behavior.impl.v3;

import agent.AgentAction;
import agent.AgentState;
import environment.CellPerception;

import java.util.function.Predicate;

public class ChargeActionBehavior extends ActionBehavior {

    @Override
    protected void doAction(AgentAction agentAction, CellPerception cell) {

        throw new RuntimeException("Should not be here.");
    }

    @Override
    protected Predicate<CellPerception> getContainsTargetPredicate(AgentState agentState) {

        return x -> false;
    }
}
