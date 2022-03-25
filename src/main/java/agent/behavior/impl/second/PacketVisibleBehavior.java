package agent.behavior.impl.second;

import agent.AgentState;
import environment.CellPerception;
import environment.world.packet.Packet;
import java.util.List;

public class PacketVisibleBehavior extends VisibleBehavior<Packet> {

    @Override
    protected List<CellPerception> getTargets(AgentState agentState) {
        return agentState.getPerception().getPacketCells();
    }
}
