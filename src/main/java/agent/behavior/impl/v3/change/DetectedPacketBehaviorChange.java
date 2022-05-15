package agent.behavior.impl.v3.change;

import agent.behavior.BehaviorChange;

public class DetectedPacketBehaviorChange extends BehaviorChange {

    @Override
    public void updateChange() {

    }

    @Override
    public boolean isSatisfied() {

        return this.getAgentState().seesPacket();
    }
}
