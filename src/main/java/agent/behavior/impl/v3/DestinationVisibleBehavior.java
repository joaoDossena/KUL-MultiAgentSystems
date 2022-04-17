package agent.behavior.impl.v3;

import agent.AgentAction;
import agent.AgentState;
import environment.CellPerception;
import environment.world.destination.Destination;
import environment.world.packet.Packet;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class DestinationVisibleBehavior extends VisibleBehavior<Destination> {

    @Override
    protected Optional<CellPerception> getTarget(AgentState agentState) {

        Optional<Packet> carry = agentState.getCarry();
        if (carry.isEmpty()) {
            throw new RuntimeException("Should carry when behavior is DestinationVisibleBehavior");
        }

        var target = agentState.getPerception().getClosestCell(
                agentState.getPerception().getDestinationCells(carry.get().getColor()),
                agentState.getX(), agentState.getY());

        return target == null ? Optional.empty() : Optional.of(target);
    }

    @Override
    protected boolean doAction(AgentState agentState, AgentAction agentAction) {

        Optional<Packet> carry = agentState.getCarry();
        if (carry.isEmpty()) {
            throw new RuntimeException("Should carry when behavior changes to ReachedDestinationBehaviorChange");
        }

        Optional<CellPerception> optionalTarget = Arrays.stream(agentState.getPerception().getNeighbours())
                .filter(Objects::nonNull)
                .filter(cellPerception -> cellPerception.containsDestination(carry.get().getColor()))
                .findFirst();

        if (optionalTarget.isEmpty()) {
            return false;
        }

        var cell = optionalTarget.get();

        agentAction.putPacket(cell.getX(), cell.getY());
        return true;
    }
}
