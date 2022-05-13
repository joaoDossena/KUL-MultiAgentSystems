package agent.behavior.impl.v4.packet;

import agent.AgentAction;
import agent.AgentState;
import agent.behavior.impl.v4.VisibleBehavior;
import environment.CellPerception;
import environment.world.packet.Packet;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class PacketVisibleBehavior extends VisibleBehavior<Packet> {

    @Override
    protected Optional<CellPerception> getTarget(AgentState agentState) {

        var target = agentState.getPerception().getClosestCell(
                agentState.getPerception().getPacketCells(),
                agentState.getX(), agentState.getY());
        if(target == null) return Optional.empty();

        if(agentState.getColor().isPresent() &&
                !target.containsPacketOfColor(agentState.getColor().get()))
            return Optional.empty();

        return Optional.of(target);
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

        if(agentState.getColor().isPresent() &&
                !cell.containsPacketOfColor(agentState.getColor().get()))
            return false;

        agentAction.pickPacket(cell.getX(), cell.getY());
        return true;
    }
}
