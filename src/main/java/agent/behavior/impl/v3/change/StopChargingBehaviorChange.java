package agent.behavior.impl.v3.change;

import agent.behavior.BehaviorChange;

public class StopChargingBehaviorChange extends BehaviorChange {


    @Override
    public void updateChange() {

    }

    @Override
    public boolean isSatisfied() {

        int STOP_CHARGING_LEVEL = 900;

        return this.getAgentState().getBatteryState() > STOP_CHARGING_LEVEL;
    }
}
