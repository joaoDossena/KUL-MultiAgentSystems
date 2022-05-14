package agent.behavior.impl.delegation;

import agent.AgentAction;
import agent.AgentMemoryFragment;
import agent.AgentState;
import agent.behavior.impl.wander.Wander;
import environment.CellPerception;
import environment.Coordinate;
import environment.Perception;

import java.util.ArrayList;
import java.util.List;

public class HasPacketBehavior extends Wander {

    @Override
    public void act(AgentState agentState, AgentAction agentAction) {

        Perception perception = agentState.getPerception();
        var neighbours = perception.getNeighbours();

        for(CellPerception neighbor : neighbours){
            if(neighbor != null && neighbor.containsDestination(agentState.getCarry().get().getColor())){
                agentAction.putPacket(neighbor.getX(), neighbor.getY());
                return;
            }

        }
        moveUsingAStar(agentState, agentAction);
    }


    public void moveUsingAStar(AgentState agentState, AgentAction agentAction){
        Perception perception = agentState.getPerception();

        List<CellPerception> visibleDestinations = perception.getDestinationCells(agentState.getCarry().get().getColor());
        Coordinate agentCoord = new Coordinate(agentState.getX(), agentState.getY());

        if(!visibleDestinations.isEmpty()) {
            CellPerception dest = visibleDestinations.get(0);
            List<Coordinate> path = perception.aStar(agentCoord, dest);
            if(path.isEmpty()) {
                System.out.println("HasPacketBehaviour::moveUsingAStar: empty path");
            }
            else{
                Coordinate step = path.get(1);
                agentAction.step(step.getX(), step.getY());
                return;
            }
        }
        else{
            var agentMemoryDestination=agentState.getMemoryFragment(agentState.getColor().get().toString());
            if(agentMemoryDestination!=null){
                Coordinate target=perception.getClosestToCoordinate(agentMemoryDestination.getCoordinate());
                List<Coordinate> path = perception.aStar(agentCoord, perception.getCellPerceptionOnAbsPos(target.getX(),target.getY()));
                if(path.isEmpty()) {
                    System.out.println("HasPacketBehaviour::moveUsingAStar: empty path");
                }
                else{
                    Coordinate step = path.get(1);
                    agentAction.step(step.getX(), step.getY());
                    return;
                }
            }

        }

        super.act(agentState, agentAction);
    }
}
