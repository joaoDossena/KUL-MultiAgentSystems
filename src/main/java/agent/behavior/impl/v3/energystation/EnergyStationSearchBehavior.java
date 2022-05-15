package agent.behavior.impl.v3.energystation;

import agent.AgentAction;
import agent.AgentState;
import agent.behavior.impl.v3.SearchBehavior;
import environment.CellPerception;
import environment.Coordinate;
import environment.world.packet.Packet;

import java.util.*;

public class EnergyStationSearchBehavior extends SearchBehavior {


    @Override
    protected boolean doAction(AgentState agentState, AgentAction agentAction) {

        Optional<Packet> carry = agentState.getCarry();
        if (carry.isEmpty()) {
            return false;
        }

        // Now we have the packet, so check if we see destinations and move to them
        if(agentState.seesDestination(carry.get().getColor())) {
            goToDestination(agentState, agentAction);
            return true;
        }

        // Otherwise, just put the packet down
        List<Coordinate> permittedMovesRel =
                agentState.getPerception().getPermittedMovesRel().stream()
                        .filter(coordinate -> agentState.getPerception()
                                .getCellPerceptionOnRelPos(coordinate.getX(), coordinate.getY() + 1) != null)
                        .filter(coordinate -> !agentState.getPerception()
                                .getCellPerceptionOnRelPos(coordinate.getX(), coordinate.getY() + 1)
                                .containsEnergyStation())
                        .toList();

        if (permittedMovesRel.isEmpty()) {
            agentAction.skip();
            return true;
        }

        var freeCell = permittedMovesRel.get(0);
        agentAction.putPacket(agentState.getX() + freeCell.getX(), agentState.getY() + freeCell.getY());
        return true;
    }

    private void goToDestination(AgentState agentState, AgentAction agentAction) {

        Optional<CellPerception> optionalTarget = Arrays.stream(agentState.getPerception().getNeighbours())
                .filter(Objects::nonNull)
                .filter(cellPerception -> cellPerception.containsDestination(agentState.getCarry().get().getColor()))
                .findFirst();

        // Reached destination, put packet
        if (optionalTarget.isPresent()) {
            var cell = optionalTarget.get();
            agentAction.putPacket(cell.getX(), cell.getY());
            return;
        }

        // Otherwise, go towards destination
        var target = agentState.getPerception().getClosestCell(
                agentState.getPerception().getDestinationCells(agentState.getCarry().get().getColor()),
                agentState.getX(), agentState.getY());

        var bestMoveOpt = findBestMove(target, agentState);
        if (bestMoveOpt.isEmpty()) {
            agentAction.skip();
            return;
        }

        agentAction.step(bestMoveOpt.get().getX(), bestMoveOpt.get().getY());
    }

    @Override
    protected List<Coordinate> getMovesInOrderRel(AgentState agentState) {

        var permittedMovesRel = agentState.getPerception().getPermittedMovesRel();

        permittedMovesRel.sort(Comparator.comparingInt(coordinate -> compareGradients(coordinate, agentState)));

        return permittedMovesRel;
    }
}
