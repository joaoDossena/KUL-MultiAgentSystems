package agent.behavior.impl.v3.change;

import agent.behavior.BehaviorChange;

public class NeedsChargingBehaviorChange extends BehaviorChange {

    @Override
    public void updateChange() {

    }

    @Override
    public boolean isSatisfied() {

        int CRITICAL_LEVEL = 400;

        return this.getAgentState().getBatteryState() < CRITICAL_LEVEL;
    }
}
