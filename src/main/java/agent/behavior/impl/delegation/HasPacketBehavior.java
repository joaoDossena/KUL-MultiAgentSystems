package agent.behavior.impl.delegation;

import agent.AgentAction;
import agent.AgentMemoryFragment;
import agent.AgentState;
import agent.behavior.impl.wander.Wander;
import environment.CellPerception;
import environment.Coordinate;
import environment.Perception;

import java.util.List;

public class HasPacketBehavior extends Wander {

    @Override
    public void act(AgentState agentState, AgentAction agentAction) {

        Perception perception = agentState.getPerception();
        var neighbours = perception.getNeighbours();

        for(CellPerception neighbor : neighbours){
            if(neighbor != null && neighbor.containsDestination(agentState.getColor().get())){
                agentAction.putPacket(neighbor.getX(), neighbor.getY());
                return;
            }

        }
        moveUsingAStar(agentState, agentAction);
    }


    private void moveUsingAStar(AgentState agentState, AgentAction agentAction){
        Perception perception = agentState.getPerception();

        Coordinate agentCoord = new Coordinate(agentState.getX(), agentState.getY());
        String agentColor = agentState.getColor().get().toString();

        AgentMemoryFragment agentMemoryDestination = agentState.getMemoryFragment(agentColor);
        List<CellPerception> visibleDestinations = perception.getDestinationCells(agentState.getColor().get());

        if(!visibleDestinations.isEmpty()) {
            CellPerception dest = visibleDestinations.get(0);
            List<Coordinate> path = perception.aStar(agentCoord, dest);
            if(path.isEmpty()) {
                System.out.println("HasPacketBehaviour::moveUsingAStar: empty path");
            }
            else{
                Coordinate step = path.get(1);
                Coordinate lastMoveRelativeCoord = perception.getMoveFromAbsPositions(agentCoord, step);
                agentState.addMemoryFragment("lastMove", new AgentMemoryFragment(lastMoveRelativeCoord));
                agentAction.step(step.getX(), step.getY());
                return;
            }
        }
        else if(agentMemoryDestination != null){
            Coordinate target = perception.getClosestToCoordinate(agentMemoryDestination.getCoordinate());
            List<Coordinate> path = perception.aStar(agentCoord, perception.getCellPerceptionOnAbsPos(target.getX(),target.getY()));
            if(path.isEmpty()) {
                System.out.println("HasPacketBehaviour::moveUsingAStar: empty path");
            }
            else{
                Coordinate step = path.get(1);
                Coordinate lastMoveRelativeCoord = perception.getMoveFromAbsPositions(agentCoord, step);
                agentState.addMemoryFragment("lastMove", new AgentMemoryFragment(lastMoveRelativeCoord));
                agentAction.step(step.getX(), step.getY());
                return;
            }
        }

        super.act(agentState, agentAction);
    }
}
