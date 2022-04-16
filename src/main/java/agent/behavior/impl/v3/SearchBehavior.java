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

import static agent.behavior.impl.v3.BehaviorV3.MemoryEnum.LAST_MOVE;

public abstract class SearchBehavior extends BehaviorV3 {

    @Override
    public void act(AgentState agentState, AgentAction agentAction) {

        var permittedMovesRel = agentState.getPerception().getPermittedMovesRel();
        Collections.shuffle(permittedMovesRel);

        actWithPermittedMovesRel(agentState, agentAction, permittedMovesRel);
    }

    protected void actWithPermittedMovesRel(AgentState agentState, AgentAction agentAction, List<Coordinate> permittedMovesRel) {

        var optimizedMoves = getOptimizedMoves(agentState, permittedMovesRel);

        if (optimizedMoves == null || optimizedMoves.isEmpty()) {
            agentAction.skip();
            return;
        }

        var bestMove = optimizedMoves.get(0);
        agentState.addMemoryFragment(LAST_MOVE.name(), new AgentMemoryFragment(Coordinate.of(bestMove.getX(), bestMove.getY())));
        agentAction.step(agentState.getX() + bestMove.getX(), agentState.getY() + bestMove.getY());
    }

    protected List<Coordinate> getOptimizedMoves(AgentState agentState, List<Coordinate> moves) {

        moves = getMovesWithPreviousMovesAtTheEnd(agentState, moves);
        moveEdgeMovesAtTheEnd(agentState, moves);

        return moves;
    }

    protected List<Coordinate> getMovesWithPreviousMovesAtTheEnd(AgentState agentState, List<Coordinate> moves) {

        var lastMove = agentState.getMemoryFragment(LAST_MOVE.name());

        if (lastMove == null) {
            return moves;
        }

        var prevRelPos = lastMove.getCoordinates().get(0).invertedSign();

        var availableMovesOfPrevPos = prevRelPos.getNeighboursAbsolute();

        var accessibleOnlyFromCurrent = moves.stream()
                .filter(Predicate.not(availableMovesOfPrevPos::contains))
                .collect(Collectors.toCollection(ArrayList::new));
        accessibleOnlyFromCurrent.remove(prevRelPos);

        var accessibleFromPreviousAndCurrent = moves.stream()
                .filter(availableMovesOfPrevPos::contains)
                .collect(Collectors.toCollection(ArrayList::new));
        accessibleFromPreviousAndCurrent.add(prevRelPos);

        var prioritizedMoves = new ArrayList<Coordinate>();
        prioritizedMoves.addAll(accessibleOnlyFromCurrent);
        prioritizedMoves.addAll(accessibleFromPreviousAndCurrent);

        return prioritizedMoves;
    }

    private void moveEdgeMovesAtTheEnd(AgentState agentState, List<Coordinate> moves) {

        var selfX = agentState.getPerception().getSelfX();
        var selfY = agentState.getPerception().getSelfY();
        var visionLen = agentState.getPerception().getAllVision().length;

        var left = Coordinate.of(-1, 0);
        var right = Coordinate.of(1, 0);
        var up = Coordinate.of(0, -1);
        var down = Coordinate.of(0, 1);

        moveToEndOfList(moves, left, (selfX == 1 || selfX == 2));
        moveToEndOfList(moves, right, (selfX == visionLen - 2 || selfX == visionLen - 3));
        moveToEndOfList(moves, up, (selfY == 1 || selfY == 2));
        moveToEndOfList(moves, down, (selfY == visionLen - 2 || selfY == visionLen - 3));
    }

    private void moveToEndOfList(List<Coordinate> list, Coordinate element, boolean predicate) {

        if (predicate && list.contains(element)) {
            list.remove(element);
            list.add(element);
        }
    }
}
