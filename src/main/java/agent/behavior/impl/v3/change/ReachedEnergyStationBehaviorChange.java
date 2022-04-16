package agent.behavior.impl.v3.change;

import agent.behavior.BehaviorChange;
import environment.CellPerception;

public class ReachedEnergyStationBehaviorChange extends BehaviorChange {

    @Override
    public void updateChange() {

    }

    @Override
    public boolean isSatisfied() {

        int x = getAgentState().getX();
        int y = getAgentState().getY();

        CellPerception cellDown = this.getAgentState().getPerception().getCellPerceptionOnAbsPos(x, y + 1);

        return cellDown != null && cellDown.containsEnergyStation();
    }
}
