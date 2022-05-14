package agent.behavior.impl.delegation.change;

import agent.behavior.BehaviorChange;

public class DestinationBecameReachableBehaviorChange extends BehaviorChange{
    private boolean hasPacket = false;

    @Override
    public void updateChange() {

    }

    @Override
    public boolean isSatisfied() {
        if(this.getAgentState().getMemoryFragment("isDestinationReachable")==null)
            return false;
        else
            return this.getAgentState().getMemoryFragment("isDestinationReachable").getReachable();

    }
}

