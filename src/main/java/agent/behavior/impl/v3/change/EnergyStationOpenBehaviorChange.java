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
        int numberOfOccupied = 0;
        for (int i = 0; i < cells.length; i++) {
            for (int j = 3; j < cells[i].length; j++) {
                if (cells[i][j] != null && cells[i][j - 1] != null && cells[i][j - 2] != null && cells[i][j - 3] != null
                        && cells[i][j].containsEnergyStation() &&
                        (cells[i][j - 1].containsAgent()
                                || cells[i][j - 2].containsAgent()
                                || cells[i][j - 3].containsAgent())) {
                    numberOfOccupied++;
                }
            }
        }
        return numberOfOccupied == 0 || numberOfOccupied != this.getAgentState().getPerception().getEnergyStations().size();
    }
}
