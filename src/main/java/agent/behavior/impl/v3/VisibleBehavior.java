package agent.behavior.impl.v3;

import agent.AgentAction;
import agent.AgentState;
import environment.CellPerception;
import environment.Coordinate;
import environment.Item;

import java.util.*;

public abstract class VisibleBehavior<T extends Item<?>> extends BehaviorV3 {

    protected abstract Optional<CellPerception> getTarget(AgentState agentState);

    protected abstract boolean doAction(AgentState agentState, AgentAction agentAction);


    @Override
    public final void act(AgentState agentState, AgentAction agentAction) {

        if (doAction(agentState, agentAction)) {
            return;
        }

        var target = getTarget(agentState);
        if (target.isEmpty()) {
            agentAction.skip();
            return;
        }

        var bestMoveOpt = findBestMove(target.get(), agentState);
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
}
