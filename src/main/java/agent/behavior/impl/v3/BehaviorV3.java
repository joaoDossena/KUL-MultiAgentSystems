package agent.behavior.impl.v3;

import agent.AgentCommunication;
import agent.AgentMemoryFragment;
import agent.AgentState;
import agent.behavior.Behavior;
import environment.CellPerception;
import environment.Coordinate;
import environment.Perception;

import java.util.*;
import java.util.stream.Collectors;

import static agent.behavior.impl.v3.BehaviorV3.MemoryEnum.ENERGY_STATIONS;

public abstract class BehaviorV3 extends Behavior {

    @Override
    public final void communicate(AgentState agentState, AgentCommunication agentCommunication) {

        putDestinationsInMemory(agentState);
//        putEnergyStationsInMemory(agentState);

    }

    protected List<Coordinate> getPermittedMovesAbs(Coordinate coordinate, Perception perception) {

        return coordinate.getNeighboursAbsolute().stream()
                .filter(neighbour -> perception.getCellPerceptionOnAbsPos(neighbour.getX(), neighbour.getY()) != null)
                .filter(neighbour -> perception.getCellPerceptionOnAbsPos(neighbour.getX(), neighbour.getY()).isWalkable())
                .filter(neighbour -> !perception.getCellPerceptionOnAbsPos(neighbour.getX(), neighbour.getY()).containsAgent())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    protected Integer compareGradients(Coordinate coordinate, AgentState agentState) {

        CellPerception cell = agentState.getPerception().getCellPerceptionOnRelPos(coordinate.getX(), coordinate.getY());

        if (cell == null || cell.getGradientRepresentation().isEmpty()) {
            return Integer.MAX_VALUE;
        }

        return cell.getGradientRepresentation().get().getValue();
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

    protected List<Coordinate> aStar(CellPerception targetCell, AgentState agentState) {

        Coordinate goal;
        if (this instanceof EnergyStationVisibleBehavior) {
            goal = Coordinate.of(targetCell.getX(), targetCell.getY() - 1);
        } else {
            goal = Coordinate.of(targetCell.getX(), targetCell.getY());
        }

        Coordinate start = Coordinate.of(agentState.getX(), agentState.getY());

        Set<Coordinate> openSet = new HashSet<>();
        openSet.add(start);

        Map<Coordinate, Coordinate> cameFrom = new HashMap<>();

        Map<Coordinate, Integer> gScore = new HashMap<>();
        gScore.put(start, 0);

        Map<Coordinate, Double> fScore = new HashMap<>();
        fScore.put(start, heuristic(start, goal));

        while (!openSet.isEmpty()) {

            Coordinate current = openSet.stream().min(Comparator.comparing(fScore::get)).get();

            List<Coordinate> allNeighborsAbs = current.getNeighboursAbsolute();
            for (Coordinate neighbour : allNeighborsAbs) {
                if (neighbour.equals(goal)) {
                    cameFrom.put(neighbour, current);
                    return reconstructPath(cameFrom, neighbour);
                }
            }

            openSet.remove(current);

            List<Coordinate> permittedMoves = getPermittedMovesAbs(current, agentState.getPerception());
            for (Coordinate neighbour : permittedMoves) {

                var tentative_gScore = gScore.get(current) + 1;
                if (!gScore.containsKey(neighbour) || tentative_gScore < gScore.get(neighbour)) {
                    cameFrom.put(neighbour, current);
                    gScore.put(neighbour, tentative_gScore);
                    fScore.put(neighbour, tentative_gScore + heuristic(neighbour, goal));
                    openSet.add(neighbour);
                }
            }
        }
        return List.of();
    }

    private List<Coordinate> reconstructPath(Map<Coordinate, Coordinate> cameFrom, Coordinate current) {
        List<Coordinate> totalPath = new ArrayList<>();
        totalPath.add(current);
        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            totalPath.add(0, current);
        }
        return totalPath;
    }

    private double heuristic(Coordinate c1, Coordinate c2) {

        return Math.sqrt(
                (c1.getX() - c2.getX()) * (c1.getX() - c2.getX())
                        + (c1.getY() - c2.getY()) * (c1.getY() - c2.getY()));
    }

    enum MemoryEnum {
        ENERGY_STATIONS, LAST_MOVE;
    }
}
