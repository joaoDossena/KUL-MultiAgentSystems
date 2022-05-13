package agent.behavior.impl.v4.change;

import agent.behavior.BehaviorChange;

import java.util.Random;

public class NeedsChargingBehaviorChange extends BehaviorChange {

    @Override
    public void updateChange() {

    }

    @Override
    public boolean isSatisfied() {

        Random random = new Random();
        int low = 500;
        int high = 600;

        int CRITICAL_LEVEL = random.nextInt(high - low) + low;

        return this.getAgentState().getBatteryState() < CRITICAL_LEVEL;
    }
}
