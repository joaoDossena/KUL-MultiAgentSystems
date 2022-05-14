package agent.behavior.impl.delegation.change;

import agent.behavior.BehaviorChange;

public class PutPacketBehaviorChange extends BehaviorChange {

    private boolean hasPacket = false;

    @Override
    public void updateChange() {
        this.hasPacket = this.getAgentState().hasCarry();
    }

    @Override
    public boolean isSatisfied() {
        return !this.hasPacket;
    }
}
