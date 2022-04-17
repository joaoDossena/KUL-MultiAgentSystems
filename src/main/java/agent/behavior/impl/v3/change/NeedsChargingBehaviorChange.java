package agent.behavior.impl.v3.change;

import agent.behavior.BehaviorChange;

import java.util.Random;

public class NeedsChargingBehaviorChange extends BehaviorChange {

    @Override
    public void updateChange() {

    }

    @Override
    public boolean isSatisfied() {

        Random r = new Random();
        int low = 400;
        int high = 600;
        int CRITICAL_LEVEL = r.nextInt(high-low) + low;;

        return this.getAgentState().getBatteryState() < CRITICAL_LEVEL;
    }
}
