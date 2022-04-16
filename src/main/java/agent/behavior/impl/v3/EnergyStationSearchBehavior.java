package agent.behavior.impl.v3;

import agent.AgentAction;
import agent.AgentState;
import environment.CellPerception;
import environment.Coordinate;
import environment.world.packet.Packet;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class EnergyStationSearchBehavior extends SearchBehavior {


    @Override
    protected boolean doAction(AgentState agentState, AgentAction agentAction) {

        Optional<Packet> carry = agentState.getCarry();
        if(carry.isEmpty()){
            return false;
        }

        List<Coordinate> permittedMovesRel = agentState.getPerception().getPermittedMovesRel();
        if(permittedMovesRel.isEmpty()){
            agentAction.skip();
            return true;
        }

        var freeCell = permittedMovesRel.get(0);
        agentAction.putPacket(agentState.getX() + freeCell.getX(), agentState.getY() + freeCell.getY());
        return true;
    }

    @Override
    protected List<Coordinate> getMovesInOrder(AgentState agentState) {

        var permittedMovesRel = agentState.getPerception().getPermittedMovesRel();

        permittedMovesRel.sort(Comparator.comparingInt(coordinate -> compareGradients(coordinate, agentState)));

        return permittedMovesRel;
    }

    private Integer compareGradients(Coordinate coordinate, AgentState agentState) {

        CellPerception cell = agentState.getPerception().getCellPerceptionOnRelPos(coordinate.getX(), coordinate.getY());

        if (cell == null || cell.getGradientRepresentation().isEmpty()) {
            return Integer.MAX_VALUE;
        }

        return cell.getGradientRepresentation().get().getValue();
    }
}
