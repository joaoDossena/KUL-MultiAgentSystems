package agent.behavior.impl.v3;

import agent.AgentAction;
import agent.AgentState;
import environment.CellPerception;
import environment.world.packet.Packet;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class PacketVisibleBehavior extends VisibleBehavior<Packet> {

    @Override
    protected List<CellPerception> getTargets(AgentState agentState) {

        return agentState.getPerception().getPacketCells();
    }

    @Override
    protected boolean doAction(AgentState agentState, AgentAction agentAction) {

        Optional<CellPerception> optionalTarget = Arrays.stream(agentState.getPerception().getNeighbours())
                .filter(Objects::nonNull)
                .filter(CellPerception::containsPacket)
                .findFirst();

        if (optionalTarget.isEmpty()) {
            return false;
        }

        var cell = optionalTarget.get();

        agentAction.pickPacket(cell.getX(), cell.getY());
        return true;
    }
}
