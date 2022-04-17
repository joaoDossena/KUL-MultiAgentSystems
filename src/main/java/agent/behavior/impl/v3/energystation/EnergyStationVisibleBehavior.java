package agent.behavior.impl.v3.energystation;

import agent.AgentAction;
import agent.AgentState;
import agent.behavior.impl.v3.VisibleBehavior;
import environment.CellPerception;
import environment.Coordinate;
import environment.world.energystation.EnergyStation;

import java.util.List;
import java.util.Optional;

public class EnergyStationVisibleBehavior extends VisibleBehavior<EnergyStation> {

    @Override
    protected Optional<CellPerception> getTarget(AgentState agentState) {

        var target = agentState.getPerception().getClosestCell(
                agentState.getPerception().getEnergyStations(),
                agentState.getX(), agentState.getY());

        return target == null ? Optional.empty() : Optional.of(target);
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
