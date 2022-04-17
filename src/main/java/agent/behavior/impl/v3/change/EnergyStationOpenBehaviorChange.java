package agent.behavior.impl.v3.change;

import agent.behavior.BehaviorChange;
import environment.CellPerception;

public class EnergyStationOpenBehaviorChange extends BehaviorChange {

    @Override
    public void updateChange() {

    }

    @Override
    public boolean isSatisfied() {

        CellPerception[][] cells = this.getAgentState().getPerception().getAllVision();
        for (int i = 0; i < cells.length; i++) {
            for (int j = 1; j < cells[i].length; j++) {
                if (cells[i][j] != null && cells[i][j].containsEnergyStation() && !cells[i][j-1].containsAgent()) {
                    return true;
                }
            }
        }
        return false;
    }
}
