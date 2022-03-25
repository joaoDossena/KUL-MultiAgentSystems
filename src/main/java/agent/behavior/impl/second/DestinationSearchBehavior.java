package agent.behavior.impl.second;

import agent.AgentAction;
import agent.AgentState;

public class DestinationSearchBehavior extends SearchBehavior {

    @Override
    public void act(AgentState agentState, AgentAction agentAction) {
        if(agentState.getMemoryFragment(agentState.getCarry().get().getColor().toString())!=null)
            System.out.println("Yeah I know the location!");
        super.act(agentState,agentAction);
    }

}
