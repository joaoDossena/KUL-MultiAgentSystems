package agent.behavior.impl.v3;

import agent.AgentCommunication;
import agent.AgentMemoryFragment;
import agent.AgentState;
import agent.behavior.Behavior;
import environment.CellPerception;
import environment.Coordinate;
import environment.Perception;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static agent.behavior.impl.v3.BehaviorV3.MemoryEnum.ENERGY_STATIONS;

public abstract class BehaviorV3 extends Behavior {

    @Override
    public final void communicate(AgentState agentState, AgentCommunication agentCommunication) {

        putDestinationsInMemory(agentState);
        putEnergyStationsInMemory(agentState);

    }

    protected List<Coordinate> getPermittedMovesAbs(Coordinate coordinate, Perception perception) {

        return coordinate.getNeighboursAbsolute().stream()
                .filter(neighbour -> perception.getCellPerceptionOnAbsPos(neighbour.getX(), neighbour.getY()) != null)
                .filter(neighbour -> perception.getCellPerceptionOnAbsPos(neighbour.getX(), neighbour.getY()).isWalkable())
                .filter(neighbour -> !perception.getCellPerceptionOnAbsPos(neighbour.getX(), neighbour.getY()).containsAgent())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private void putEnergyStationsInMemory(AgentState agentState) {

        List<CellPerception> chargers = agentState.getPerception().getEnergyStations();

        for (CellPerception station : chargers) {
            var stations = agentState.getMemoryFragment(ENERGY_STATIONS.name());
            if (stations == null) {
                agentState.addMemoryFragment(ENERGY_STATIONS.name(), new AgentMemoryFragment(Coordinate.of(station.getX(), station.getY())));
            } else {
                stations.addToCoordinatesList(station.toCoordinate());
                agentState.addMemoryFragment(ENERGY_STATIONS.name(), stations);
            }
        }
    }

    private void putDestinationsInMemory(AgentState agentState) {

        var destinations = agentState.getPerception().getDestinationCells();
        destinations.forEach((k, v) ->
                agentState.addMemoryFragment(k.toString(), new AgentMemoryFragment(Coordinate.of(v.getX(), v.getY()))));
    }

    enum MemoryEnum {
        ENERGY_STATIONS, LAST_MOVE;
    }
}
