package agent.behavior.impl.first.change;

import agent.AgentMemoryFragment;
import agent.behavior.BehaviorChange;
import environment.Coordinate;

public class ChargeBehaviorChange extends BehaviorChange {

    @Override
    public void updateChange() {
    }

    @Override
    public boolean isSatisfied() {
        if(this.getAgentState().getBatteryState()<1000) {
            if (!this.getAgentState().hasCarry()) {
                AgentMemoryFragment energyStationsFrag = this.getAgentState().getMemoryFragment("EnergyStations");
                if (energyStationsFrag != null) {
                    var coordinates = energyStationsFrag.getCoordinates();
                    for (Coordinate cor : coordinates) {
                        if (this.getAgentState().getX() == cor.getX() && this.getAgentState().getY() == (cor.getY() - 1)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}