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

public abstract class BehaviorV3 extends Behavior {

    @Override
    public final void communicate(AgentState agentState, AgentCommunication agentCommunication) {

        putDestinationsInMemory(agentState);
    }

    protected List<Coordinate> getAllPermittedMovesInRandomOrderRel(AgentState agentState) {

        var permittedMovesRel = agentState.getPerception().getPermittedMovesRel();

        Collections.shuffle(permittedMovesRel);

        return permittedMovesRel;
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

    private void putDestinationsInMemory(AgentState agentState) {

        var destinations = agentState.getPerception().getDestinationCells();
        destinations.forEach((color, cell) ->
                agentState.addMemoryFragment(color.toString(), new AgentMemoryFragment(Coordinate.of(cell.getX(), cell.getY()))));
    }

    protected Optional<Coordinate> findBestMove(CellPerception minCell, AgentState agentState) {

        List<Coordinate> path = aStar(minCell, agentState);

        // If A* failed, just follow the gradients
        if(path.isEmpty()){
            var permittedMovesRel = agentState.getPerception().getPermittedMovesRel();
            permittedMovesRel.sort(Comparator.comparingInt(coordinate -> compareGradients(coordinate, agentState)));
            return permittedMovesRel.stream().findFirst()
                    .map(rel -> Coordinate.of(rel.getX() + agentState.getX(), rel.getY() + agentState.getY()));
        }

        return path.stream().skip(1).findFirst();
    }

    private List<Coordinate> aStar(CellPerception targetCell, AgentState agentState) {

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
        fScore.put(start, euclideanDistance(start, goal));

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
                    fScore.put(neighbour, tentative_gScore + euclideanDistance(neighbour, goal));
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

    protected double euclideanDistance(Coordinate c1, Coordinate c2) {

        return Math.sqrt(
                (c1.getX() - c2.getX()) * (c1.getX() - c2.getX())
                        + (c1.getY() - c2.getY()) * (c1.getY() - c2.getY()));
    }

    enum MemoryEnum {
        ENERGY_STATIONS, LAST_MOVES;
    }
}
