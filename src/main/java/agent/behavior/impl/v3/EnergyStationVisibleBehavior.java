package agent.behavior.impl.v3;

import agent.AgentAction;
import agent.AgentState;
import environment.CellPerception;
import environment.world.energystation.EnergyStation;

import java.util.List;

public class EnergyStationVisibleBehavior extends VisibleBehavior<EnergyStation> {

    @Override
    protected List<CellPerception> getTargets(AgentState agentState) {

        return agentState.getPerception().getEnergyStations();
    }

    @Override
    protected boolean doAction(AgentState agentState, AgentAction agentAction) {

        int x = agentState.getX();
        int y = agentState.getY();

        CellPerception cellDown = agentState.getPerception().getCellPerceptionOnAbsPos(x, y + 1);

        if (cellDown == null || !cellDown.containsEnergyStation()) {
            return false;
        }

        agentAction.skip();
        return true;
    }
}
