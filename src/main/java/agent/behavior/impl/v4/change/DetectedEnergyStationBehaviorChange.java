package agent.behavior.impl.v4.change;

import agent.behavior.BehaviorChange;

public class DetectedEnergyStationBehaviorChange extends BehaviorChange {

    @Override
    public void updateChange() {

    }

    @Override
    public boolean isSatisfied() {
        return this.getAgentState().seesEnergyStation();
    }
}
