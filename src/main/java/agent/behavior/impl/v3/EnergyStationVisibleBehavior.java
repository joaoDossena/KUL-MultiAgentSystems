package agent.behavior.impl.v3;

import agent.AgentState;
import environment.CellPerception;
import environment.world.energystation.EnergyStation;

import java.util.List;

public class EnergyStationVisibleBehavior extends VisibleBehavior<EnergyStation> {

    @Override
    protected List<CellPerception> getTargets(AgentState agentState) {

        return agentState.getPerception().getEnergyStations();
    }
}
