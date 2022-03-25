package agent.behavior.impl.wander;

import agent.AgentAction;
import agent.AgentCommunication;
import agent.AgentMemoryFragment;
import agent.AgentState;
import agent.behavior.Behavior;
import environment.Coordinate;
import environment.Perception;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Wander extends Behavior {

    @Override
    public void communicate(AgentState agentState, AgentCommunication agentCommunication) {
        // No communication
    }

    @Override
    public void act(AgentState agentState, AgentAction agentAction) {
        AgentMemoryFragment fragment = agentState.getMemoryFragment("lastMove");
        Coordinate undoPreviousMove = null, previousMove;
        List<Coordinate> possibleCurrentMoves = generateAllMovesFromCoordinate(new Coordinate(0, 0));
        List<Coordinate> accessibleFromPreviousAndCurrent = null;

        if (fragment != null) {
            previousMove = fragment.getCoordinate();
            undoPreviousMove = previousMove.invertedSign();
            accessibleFromPreviousAndCurrent = commonElements(possibleCurrentMoves, generateAllMovesFromCoordinate(undoPreviousMove));
            accessibleFromPreviousAndCurrent.add(undoPreviousMove);
        }

        // Potential moves an agent can make (radius of 1 around the agent)
        List<Coordinate> moves = possibleCurrentMoves;
        if (accessibleFromPreviousAndCurrent != null) {
            moves = removeIntersection(possibleCurrentMoves, accessibleFromPreviousAndCurrent); // priority #1
            Collections.shuffle(moves);
            moves.addAll(accessibleFromPreviousAndCurrent);
        }

        var perception = agentState.getPerception();
        var optimizedMoves = new ArrayList<Coordinate>(avoidWorthlessMoves(moves, perception));
        //TODO first use prioritized move else move Randomly

        performMove(agentState, agentAction, optimizedMoves);

    }

    public List<Coordinate> avoidWorthlessMoves(List<Coordinate> moves, Perception perception) {
        var optimizedMoves = new ArrayList<Coordinate>(moves);
        var vision = perception.getAllVision();

        if (perception.getSelfX() == 1 || perception.getSelfX() == 2) {
            optimizedMoves.remove(new Coordinate(-1, 0));
            optimizedMoves.add(new Coordinate(-1, 0));
        } else if (perception.getSelfX() == vision.length - 2 || perception.getSelfX() == vision.length - 3) {
            optimizedMoves.remove(new Coordinate(1, 0));
            optimizedMoves.add(new Coordinate(1, 0));
        }

        if (perception.getSelfY() == 1 || perception.getSelfY() == 2) {
            optimizedMoves.remove(new Coordinate(0, -1));
            optimizedMoves.add(new Coordinate(0, -1));
        } else if (perception.getSelfY() == vision.length - 2 || perception.getSelfY() == vision.length - 3) {
            optimizedMoves.remove(new Coordinate(0, 1));
            optimizedMoves.add(new Coordinate(0, 1));
        }

        return optimizedMoves;
    }

    public void performMove(AgentState agentState, AgentAction agentAction, List<Coordinate> moves) {

        // Check for viable moves
        for (var move : moves) {
            var perception = agentState.getPerception();
            int x = move.getX();
            int y = move.getY();

            // If the area is null, it is outside the bounds of the environment
            //  (when the agent is at any edge for example some moves are not possible)
            if (perception.getCellPerceptionOnRelPos(x, y) != null && perception.getCellPerceptionOnRelPos(x, y).isWalkable()) {
                agentState.addMemoryFragment("lastMove", new AgentMemoryFragment(new Coordinate(x, y)));
                agentAction.step(agentState.getX() + x, agentState.getY() + y);
                return;
            }
        }
        agentAction.skip();
    }

    public List<Coordinate> removeIntersection(List<Coordinate> moves, List<Coordinate> forbiddenMoves) {
        List<Coordinate> res = new ArrayList<>();
        for (Coordinate move : moves) {
            if (!forbiddenMoves.contains(move))
                res.add(move);
        }
        return res;
    }

    public List<Coordinate> generateAllMovesFromCoordinate(Coordinate coordinate) {
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

    public List<Coordinate> commonElements(List<Coordinate> l1, List<Coordinate> l2) {
        List<Coordinate> res = new ArrayList<>();
        for (Coordinate c1 : l1) {
            if (l2.contains(c1))
                res.add(c1);
        }
        return res;
    }


}
