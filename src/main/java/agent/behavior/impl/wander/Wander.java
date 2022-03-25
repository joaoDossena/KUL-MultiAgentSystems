package agent.behavior.impl.wander;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import agent.AgentAction;
import agent.AgentCommunication;
import agent.AgentMemoryFragment;
import agent.AgentState;
import agent.behavior.Behavior;
import environment.Coordinate;
import environment.Perception;

public class Wander extends Behavior {

    @Override
    public void communicate(AgentState agentState, AgentCommunication agentCommunication) {
        // No communication
    }
    @Override
    public void act(AgentState agentState, AgentAction agentAction) {
        List<Coordinate> moves = new ArrayList<>(List.of(
                new Coordinate(1, 1), new Coordinate(-1, -1),
                new Coordinate(1, 0), new Coordinate(-1, 0),
                new Coordinate(0, 1), new Coordinate(0, -1),
                new Coordinate(1, -1), new Coordinate(-1, 1)
        ));

        var perception = agentState.getPerception();
        var optimizedMoves=new ArrayList<Coordinate>(avoidWorthlessMoves(moves,perception));
        //TODO first use prioritized move else move Randomly
        if(!optimizedMoves.isEmpty() && actRandomly(agentState,agentAction,optimizedMoves))
            return;
        else
        if(!actRandomly(agentState,agentAction,moves)) {
            agentAction.skip();
        }

    }

    public List<Coordinate> avoidWorthlessMoves(List<Coordinate> moves, Perception perception){
        var optimizedMoves=new ArrayList<Coordinate>(moves);
        var vision= perception.getAllVision();

        if(perception.getSelfX()==1||perception.getSelfX()==2){
            optimizedMoves.remove(new Coordinate(-1,0));
        }
        else if(perception.getSelfX()==vision.length-2||perception.getSelfX()==vision.length-3){
            optimizedMoves.remove(new Coordinate(1,0));
        }

        if(perception.getSelfX()==1||perception.getSelfX()==2){
            optimizedMoves.remove(new Coordinate(-1,0));
        }
        else if(perception.getSelfX()==vision.length-2||perception.getSelfX()==vision.length-3){
            optimizedMoves.remove(new Coordinate(1,0));
        }

        return optimizedMoves;
    }

    public boolean actRandomly(AgentState agentState, AgentAction agentAction,List<Coordinate> moves) {
        AgentMemoryFragment fragment = agentState.getMemoryFragment("lastMove");
        Coordinate undoMove = null;
        if (fragment != null)  undoMove = fragment.getCoordinate().invertedSign();
        moves=addAllExceptForbidden(moves,undoMove);
        // Shuffle moves randomly
        Collections.shuffle(moves);
        if(undoMove != null)
            moves.add(undoMove);

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
                return true;
            }
        }

        return false;

    }

    public List<Coordinate> addAllExceptForbidden(List<Coordinate> moves, Coordinate forbidden){
        List<Coordinate> res = new ArrayList<>();
        for(Coordinate move : moves){
            if(!move.equals(forbidden)) res.add(move);
        }
        return res;
    }
}
