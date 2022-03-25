package agent.behavior.impl.second;

import agent.AgentAction;
import agent.AgentCommunication;
import agent.AgentState;
import environment.CellPerception;
import environment.world.destination.Destination;
import java.util.List;

public class DestinationVisibleBehavior extends VisibleBehavior<Destination> {

    @Override
    protected List<CellPerception> getTargets(AgentState agentState) {
        return agentState.getPerception().getDestinationCells(agentState.getCarry().get().getColor());
    }

    @Override
    public void act(AgentState agentState, AgentAction agentAction) {
        super.act(agentState, agentAction);
    }
}
