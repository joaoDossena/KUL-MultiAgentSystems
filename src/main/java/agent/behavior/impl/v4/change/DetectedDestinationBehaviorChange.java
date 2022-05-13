package agent.behavior.impl.v4.change;

import agent.behavior.BehaviorChange;
import environment.world.packet.Packet;

import java.util.Optional;

public class DetectedDestinationBehaviorChange extends BehaviorChange {

    @Override
    public void updateChange() {

    }

    @Override
    public boolean isSatisfied() {

        Optional<Packet> carry = getAgentState().getCarry();
        if (carry.isEmpty()) {
            throw new RuntimeException("Should carry when behavior changes to DetectedDestinationBehaviorChange");
        }

        return this.getAgentState().seesDestination(carry.get().getColor());
    }
}
