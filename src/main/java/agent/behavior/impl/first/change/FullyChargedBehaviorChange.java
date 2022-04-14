package agent.behavior.impl.first.change;

import agent.behavior.BehaviorChange;

public class FullyChargedBehaviorChange extends BehaviorChange {

    private static boolean isFullyCharged=false;
    private static boolean hasPacket=false;


    @Override
    public void updateChange() {

    }

    @Override
    public boolean isSatisfied() {
        if(this.getAgentState().getBatteryState()==1000) return true;
        return false;
    }
}
