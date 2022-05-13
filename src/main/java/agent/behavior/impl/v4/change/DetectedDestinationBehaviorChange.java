package agent.behavior.impl.v4.change;

import agent.behavior.BehaviorChange;
import environment.Coordinate;
import environment.world.packet.Packet;

import java.util.Optional;

public class DetectedDestinationBehaviorChange extends BehaviorChange {

    @Override
    public void updateChange() {

    }

    @Override
    public boolean isSatisfied() {
        System.out.println(" has color-----------------------------");
        System.out.println(this.getAgentState()+" has color-----------------------------");

        Optional<Packet> carry = this.getAgentState().getCarry();
        if (carry.isEmpty()) {
            throw new RuntimeException("Should carry when behavior changes to DetectedDestinationBehaviorChange");
        }
        var destinationMem = this.getAgentState().getMemoryFragment(this.getAgentState().getColor().get().toString());
        if ( destinationMem!=null) {
            var destinationCoordinate = destinationMem.getCoordinates().get(0);
            return this.getAgentState().seesDestination(carry.get().getColor()) &&
                    !this.getAgentState().getPerception().isReachable(new Coordinate(this.getAgentState().getX(),this.getAgentState().getY()),destinationCoordinate);
        }
        return false;
    }
}
