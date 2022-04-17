package agent.behavior.impl.v3;

import agent.AgentAction;
import agent.AgentState;
import environment.CellPerception;
import environment.Coordinate;
import environment.world.energystation.EnergyStation;

import java.util.List;
import java.util.Optional;

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

        // Charge
        if (cellDown != null && cellDown.containsEnergyStation()) {
            agentAction.skip();
            return true;
        }

        Optional<Coordinate> chargePositionOptional =
                agentState.getPerception().getPermittedMovesRel()
                        .stream()
                        .filter(coordinate -> agentState.getPerception()
                                .getCellPerceptionOnRelPos(coordinate.getX(), coordinate.getY() + 1) != null)
                        .filter(coordinate -> agentState.getPerception()
                                .getCellPerceptionOnRelPos(coordinate.getX(), coordinate.getY() + 1)
                                .containsEnergyStation())
                        .findFirst();

        // Go to charge position
        if (chargePositionOptional.isPresent()) {
            agentAction.step(agentState.getX() + chargePositionOptional.get().getX(),
                    agentState.getY() + chargePositionOptional.get().getY());
            return true;
        }

        return false;
    }
}
