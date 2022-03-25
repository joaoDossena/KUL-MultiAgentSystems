package agent.behavior.impl.second;

import agent.AgentAction;
import agent.AgentCommunication;
import agent.AgentMemoryFragment;
import agent.AgentState;
import agent.behavior.Behavior;
import environment.CellPerception;
import environment.Coordinate;
import environment.Item;
import environment.Perception;
import java.util.List;

public abstract class VisibleBehavior<T extends Item<?>> extends Behavior {

    protected abstract List<CellPerception> getTargets(AgentState agentState);

    @Override
    public void communicate(AgentState agentState, AgentCommunication agentCommunication) {
        // No communication
    }

    @Override
    public void act(AgentState agentState, AgentAction agentAction) {

        Perception perception = agentState.getPerception();

        CellPerception minCell = perception.getClosestCell(getTargets(agentState), agentState.getX(), agentState.getY());

        List<Coordinate> moves = List.of(
                new Coordinate(1, 1), new Coordinate(-1, -1),
                new Coordinate(1, -1), new Coordinate(-1, 1),
                new Coordinate(1, 0), new Coordinate(-1, 0),
                new Coordinate(0, 1), new Coordinate(0, -1)
        );

        Coordinate minMove = perception.getShortestMoveToCell(minCell, moves, agentState.getX(), agentState.getY());

        agentState.addMemoryFragment("lastMove", new AgentMemoryFragment(new Coordinate(minMove.getX(), minMove.getY())));
        agentAction.step(agentState.getX() + minMove.getX(), agentState.getY() + minMove.getY());
    }
}
