package agent.behavior.impl.delegation.change;

import agent.behavior.BehaviorChange;
import environment.Coordinate;

public class PickedPacketUnreachableBehaviorChange extends BehaviorChange {

    @Override
    public void updateChange() {

    }

    @Override
    public boolean isSatisfied() {
        System.out.println(" has color-----------------------------");
        System.out.println(this.getAgentState()+" has color-----------------------------");

        var destinationMem = this.getAgentState().getMemoryFragment(this.getAgentState().getColor().get().toString());
        if (destinationMem != null) {
            var destinationCoordinate = destinationMem.getCoordinate();
            return this.getAgentState().hasCarry() &&
                    !this.getAgentState().getPerception().isReachable(new Coordinate(this.getAgentState().getX(), this.getAgentState().getY()), destinationCoordinate);
        } else {
            return false;
        }
    }
}
