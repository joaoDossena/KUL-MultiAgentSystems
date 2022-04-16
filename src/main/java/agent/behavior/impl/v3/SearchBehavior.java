package agent.behavior.impl.v3;

import agent.AgentAction;
import agent.AgentMemoryFragment;
import agent.AgentState;
import environment.Coordinate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class SearchBehavior extends BehaviorV3 {

    @Override
    public void act(AgentState agentState, AgentAction agentAction) {

        var availableMoves = agentState.getPerception().getPermittedMovesRel();
        Collections.shuffle(availableMoves);

        actWithLimitedMoves(agentState, agentAction, availableMoves);
    }

    protected void actWithLimitedMoves(AgentState agentState, AgentAction agentAction, List<Coordinate> permittedMoves) {

        var prioritizedMoves = getMovesWithWorstAtTheEnd(agentState, permittedMoves);
        var optimizedMoves = getOptimizedMoves(agentState, prioritizedMoves);

        if (optimizedMoves == null || optimizedMoves.isEmpty()) {
            agentAction.skip();
            return;
        }

        var bestMove = optimizedMoves.get(0);
        agentState.addMemoryFragment("lastMove", new AgentMemoryFragment(new Coordinate(bestMove.getX(), bestMove.getY())));
        agentAction.step(agentState.getX() + bestMove.getX(), agentState.getY() + bestMove.getY());
    }

    protected List<Coordinate> getMovesWithWorstAtTheEnd(AgentState agentState, List<Coordinate> availableMoves) {

        var lastMove = agentState.getMemoryFragment("lastMove");

        if (lastMove == null) {
            return availableMoves;
        }

        var prevRelPos = lastMove.getCoordinates().get(0).invertedSign();

        var availableMovesOfPrevPos = prevRelPos.getNeighboursAbsolute();

        var accessibleOnlyFromCurrent = availableMoves.stream()
                .filter(Predicate.not(availableMovesOfPrevPos::contains))
                .collect(Collectors.toCollection(ArrayList::new));
        accessibleOnlyFromCurrent.remove(prevRelPos);

        var accessibleFromPreviousAndCurrent = availableMoves.stream()
                .filter(availableMovesOfPrevPos::contains)
                .collect(Collectors.toCollection(ArrayList::new));
        accessibleFromPreviousAndCurrent.add(prevRelPos);

        var prioritizedMoves = new ArrayList<Coordinate>();
        prioritizedMoves.addAll(accessibleOnlyFromCurrent);
        prioritizedMoves.addAll(accessibleFromPreviousAndCurrent);

        return prioritizedMoves;
    }

    protected List<Coordinate> getOptimizedMoves(AgentState agentState, List<Coordinate> moves) {

        var perception = agentState.getPerception();
        var vision = perception.getAllVision();

        if (perception.getSelfX() == 1 || perception.getSelfX() == 2) {
            moves.remove(new Coordinate(-1, 0));
            moves.add(new Coordinate(-1, 0));
        } else if (perception.getSelfX() == vision.length - 2 || perception.getSelfX() == vision.length - 3) {
            moves.remove(new Coordinate(1, 0));
            moves.add(new Coordinate(1, 0));
        }

        if (perception.getSelfY() == 1 || perception.getSelfY() == 2) {
            moves.remove(new Coordinate(0, -1));
            moves.add(new Coordinate(0, -1));
        } else if (perception.getSelfY() == vision.length - 2 || perception.getSelfY() == vision.length - 3) {
            moves.remove(new Coordinate(0, 1));
            moves.add(new Coordinate(0, 1));
        }

        return moves;
    }
}
