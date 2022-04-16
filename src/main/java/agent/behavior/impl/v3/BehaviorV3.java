package agent.behavior.impl.v3;

import agent.AgentCommunication;
import agent.AgentMemoryFragment;
import agent.AgentState;
import agent.behavior.Behavior;
import environment.CellPerception;
import environment.Coordinate;

import java.util.List;

import static agent.behavior.impl.v3.BehaviorV3.MemoryEnum.ENERGY_STATIONS;

public abstract class BehaviorV3 extends Behavior {

    @Override
    public void communicate(AgentState agentState, AgentCommunication agentCommunication) {

        putDestinationsInMemory(agentState);
        putEnergyStationsInMemory(agentState);

        // TODO: communicate the stations
    }


    protected void putEnergyStationsInMemory(AgentState agentState) {

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

    protected void putDestinationsInMemory(AgentState agentState) {

        var destinations = agentState.getPerception().getDestinationCells();
        destinations.forEach((k, v) ->
                agentState.addMemoryFragment(k.toString(), new AgentMemoryFragment(Coordinate.of(v.getX(), v.getY()))));
    }

    enum MemoryEnum {
        ENERGY_STATIONS, LAST_MOVE;
    }
}
