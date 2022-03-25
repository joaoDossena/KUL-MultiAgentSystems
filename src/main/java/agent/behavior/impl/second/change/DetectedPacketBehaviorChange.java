package agent.behavior.impl.second.change;

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
