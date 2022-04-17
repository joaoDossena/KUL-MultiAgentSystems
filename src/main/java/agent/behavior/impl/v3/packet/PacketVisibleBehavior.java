package agent.behavior.impl.v3.packet;

import agent.AgentAction;
import agent.AgentState;
import agent.behavior.impl.v3.VisibleBehavior;
import environment.CellPerception;
import environment.world.packet.Packet;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class PacketVisibleBehavior extends VisibleBehavior<Packet> {

    @Override
    protected Optional<CellPerception> getTarget(AgentState agentState) {

        var target = agentState.getPerception().getClosestCell(
                agentState.getPerception().getPacketCells(),
                agentState.getX(), agentState.getY());

        return target == null ? Optional.empty() : Optional.of(target);
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
