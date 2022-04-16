package agent.behavior.impl.v3.change;

import agent.behavior.BehaviorChange;
import environment.world.packet.Packet;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class ReachedDestinationBehaviorChange extends BehaviorChange {

    @Override
    public void updateChange() {

    }

    @Override
    public boolean isSatisfied() {

        Optional<Packet> carry = getAgentState().getCarry();
        if (carry.isEmpty()) {
            throw new RuntimeException("Should carry when behavior changes to ReachedDestinationBehaviorChange");
        }

        return Arrays.stream(this.getAgentState().getPerception().getNeighbours())
                .filter(Objects::nonNull)
                .anyMatch(cellPerception -> cellPerception.containsDestination(carry.get().getColor()));
    }
}
