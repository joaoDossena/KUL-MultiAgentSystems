package agent.behavior.impl.first;

import agent.AgentAction;
import agent.AgentState;
import agent.behavior.impl.wander.Wander;
import environment.CellPerception;
import environment.Perception;

public class ChargeBehavior extends Wander {
    @Override
    public void act(AgentState agentState, AgentAction agentAction) {
        agentAction.skip();
    }
}
