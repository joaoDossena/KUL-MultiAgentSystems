package agent.behavior.impl.advanced;

import agent.AgentAction;
import agent.AgentState;
import agent.behavior.impl.v3.BehaviorV3;
import environment.Coordinate;

import java.util.Collections;
import java.util.List;

public class AdvancedEnergyStationOccupiedBehavior extends BehaviorV3 {

    @Override
    public void act(AgentState agentState, AgentAction agentAction) {

        // Potential moves an agent can make (radius of 1 around the agent)
        List<Coordinate> moves = Coordinate.getNeighboursRelative();

        // Shuffle moves randomly
        Collections.shuffle(moves);

        // Check for viable moves
        for (var move : moves) {
            var perception = agentState.getPerception();
            int x = move.getX();
            int y = move.getY();

            // If the area is null, it is outside the bounds of the environment
            //  (when the agent is at any edge for example some moves are not possible)
            if (perception.getCellPerceptionOnRelPos(x, y) != null && perception.getCellPerceptionOnRelPos(x, y).isWalkable()) {
                agentAction.step(agentState.getX() + x, agentState.getY() + y);
                return;
            }
        }

        // No viable moves, skip turn
        agentAction.skip();

    }
}
