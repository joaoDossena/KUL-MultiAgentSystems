package agent.behavior.impl.v4.packet;

import agent.AgentAction;
import agent.AgentState;
import agent.behavior.impl.v4.VisibleBehavior;
import environment.CellPerception;
import environment.Coordinate;
import environment.Environment;
import environment.world.packet.Packet;

import java.util.*;

public class FindPacketButDestinationUnreachable extends VisibleBehavior<Packet>{
    @Override
    protected Optional<CellPerception> getTarget(AgentState agentState) {

        var target = agentState.getPerception().getClosestCell(
                agentState.getPerception().getPacketCells(),
                agentState.getX(), agentState.getY());
        if(target == null) return Optional.empty();

        if(agentState.getColor().isPresent() &&
                !target.containsPacketOfColor(agentState.getColor().get()))
            return Optional.empty();

        return Optional.of(target);
    }

    @Override
    protected boolean doAction(AgentState agentState, AgentAction agentAction) {

        Optional<CellPerception> optionalTarget = Arrays.stream(agentState.getPerception().getNeighbours())
                .filter(Objects::nonNull)
                .filter(CellPerception::containsPacket)
                .findFirst();

        if (optionalTarget.isEmpty()) {
            return false;
        }


        if(!agentState.getPerception().hasNoNeighbouringPacket(this.generateAllMovesFromCoordinate(optionalTarget.get().toCoordinate()))) return false;
        else {
            var destinationMem = agentState.getMemoryFragment(agentState.getColor().get().toString());
            if (destinationMem != null) {
                var destinationCoordinate = destinationMem.getCoordinates().get(0);
                if (Environment.chebyshevDistance(destinationCoordinate, new Coordinate(agentState.getX(), agentState.getY())) <= 8)
                    return false;
            }
        }
        if(!agentState.getPerception().isReachable(new Coordinate(agentState.getX(),agentState.getY()),optionalTarget.get().toCoordinate())) return false;

        var cell = optionalTarget.get();

        if(agentState.getColor().isPresent() &&
                !cell.containsPacketOfColor(agentState.getColor().get()))
            return false;

        agentAction.pickPacket(cell.getX(), cell.getY());
        return true;
    }

    private List<Coordinate> generateAllMovesFromCoordinate(Coordinate coordinate) {
        List<Coordinate> res = new ArrayList<>();

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                if (x == 0 && y == 0)
                    continue;
                res.add(new Coordinate(coordinate.getX() + x, coordinate.getY() + y));
            }
        }
        return res;
    }


}
