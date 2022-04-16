package agent.behavior.impl.v3;

import agent.AgentState;
import environment.CellPerception;
import environment.world.destination.Destination;
import environment.world.packet.Packet;

import java.util.List;
import java.util.Optional;

public class DestinationVisibleBehavior extends VisibleBehavior<Destination> {

    @Override
    protected List<CellPerception> getTargets(AgentState agentState) {

        Optional<Packet> carry = agentState.getCarry();
        if (carry.isEmpty()) {
            throw new RuntimeException("Should carry when behavior is DestinationVisibleBehavior");
        }

        return agentState.getPerception().getDestinationCells(carry.get().getColor());
    }
}
