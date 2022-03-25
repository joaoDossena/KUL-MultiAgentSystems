package agent.behavior.impl.second;

import agent.AgentAction;
import agent.AgentCommunication;
import agent.AgentState;
import environment.CellPerception;
import environment.world.packet.Packet;
import java.util.List;
import java.util.function.Predicate;

public class PacketVisibleBehavior extends VisibleBehavior<Packet> {

    @Override
    protected List<CellPerception> getTargets(AgentState agentState) {
        return agentState.getPerception().getPacketCells();
    }

    @Override
    public void act(AgentState agentState, AgentAction agentAction) {
        super.act(agentState, agentAction);
    }
}
