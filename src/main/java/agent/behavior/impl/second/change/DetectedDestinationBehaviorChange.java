package agent.behavior.impl.second.change;

import agent.behavior.BehaviorChange;

public class DetectedDestinationBehaviorChange extends BehaviorChange {

    @Override
    public void updateChange() {

    }

    @Override
    public boolean isSatisfied() {
        return this.getAgentState().seesDestination(this.getAgentState().getCarry().get().getColor());
    }
}
