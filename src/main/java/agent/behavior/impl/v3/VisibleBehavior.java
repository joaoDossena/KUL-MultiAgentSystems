package agent.behavior.impl.v3;

import agent.AgentAction;
import agent.AgentState;
import environment.CellPerception;
import environment.Coordinate;
import environment.Item;
import environment.Perception;

import java.util.*;
import java.util.stream.Collectors;

public abstract class VisibleBehavior<T extends Item<?>> extends BehaviorV3 {

    protected abstract List<CellPerception> getTargets(AgentState agentState);

    protected abstract boolean doAction(AgentState agentState, AgentAction agentAction);


    @Override
    public final void act(AgentState agentState, AgentAction agentAction) {

        if (doAction(agentState, agentAction)) {
            return;
        }

        var targets = getTargets(agentState);
        if (targets == null || targets.isEmpty()) {
            agentAction.skip();
            return;
        }

        var minCell = agentState.getPerception().getClosestCell(targets, agentState.getX(), agentState.getY());

        if (minCell == null) {
            agentAction.skip();
            return;
        }

        var bestMoveOpt = findBestMove(minCell, agentState);
//        var minMoveOpt = agentState.getPerception().getShortestMoveToCell(minCell, agentState.getX(), agentState.getY());

        if (bestMoveOpt.isEmpty()) {
            agentAction.skip();
            return;
        }

        agentAction.step(bestMoveOpt.get().getX(), bestMoveOpt.get().getY());
    }

    private Optional<Coordinate> findBestMove(CellPerception minCell, AgentState agentState) {

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

}
