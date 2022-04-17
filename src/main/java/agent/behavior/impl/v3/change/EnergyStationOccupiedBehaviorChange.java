package agent.behavior.impl.v3.change;

import agent.behavior.BehaviorChange;
import environment.CellPerception;
import environment.Coordinate;

import java.util.Arrays;

public class EnergyStationOccupiedBehaviorChange extends BehaviorChange {

    @Override
    public void updateChange() {

    }

    @Override
    public boolean isSatisfied() {

        CellPerception[][] cells = this.getAgentState().getPerception().getAllVision();
        int numberOfOccupied = 0;
        for (int i = 0; i < cells.length; i++) {
            for (int j = 1; j < cells[i].length; j++) {
                if (cells[i][j] != null
                        && cells[i][j].containsEnergyStation()
                        && cells[i][j-1].containsAgent()
                        && !cells[i][j-1].getAgentRepresentation().get().getName().equals(this.getAgentState().getName())) {
                    numberOfOccupied++;
                }
            }
        }
        return numberOfOccupied == this.getAgentState().getPerception().getEnergyStations().size();
    }
}
