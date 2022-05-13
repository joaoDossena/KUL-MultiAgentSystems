package agent.behavior.impl.v4.change;

import agent.behavior.BehaviorChange;
import environment.Coordinate;

public class PickedPacketBehaviorChange extends BehaviorChange {

    @Override
    public void updateChange() {

    }

    @Override
    public boolean isSatisfied() {
        System.out.println(" has color-----------------------------");
        System.out.println(this.getAgentState()+" has color-----------------------------");

        if(this.getAgentState().getCarry().isEmpty()) return false;
        var destinationMem = this.getAgentState().getMemoryFragment(this.getAgentState().getColor().get().toString());
        if (destinationMem != null) {
            var destinationCoordinate = destinationMem.getCoordinates().get(0);
            return this.getAgentState().hasCarry() &&
                    this.getAgentState().getPerception().isReachable(new Coordinate(this.getAgentState().getX(), this.getAgentState().getY()), destinationCoordinate);
        }
        return this.getAgentState().hasCarry();
    }

}
