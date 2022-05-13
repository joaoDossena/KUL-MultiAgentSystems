package agent.behavior.impl.v4.change;

import agent.behavior.BehaviorChange;

public class DetectedPacketBehaviorChange extends BehaviorChange {

    @Override
    public void updateChange() {

    }

    @Override
    public boolean isSatisfied() {
        System.out.println(" has color-----------------------------");
        System.out.println(this.getAgentState()+" has color-----------------------------");

        return this.getAgentState().seesPacket();
    }
}
