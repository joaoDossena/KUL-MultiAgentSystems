package agent.behavior.impl.delegation;

import agent.AgentAction;
import agent.AgentCommunication;
import agent.AgentMemoryFragment;
import agent.AgentState;
import agent.behavior.Behavior;
import agent.behavior.impl.wander.BetterWander;
import agent.behavior.impl.wander.Wander;
import environment.Coordinate;
import environment.Perception;
import environment.world.packet.Packet;
import environment.Environment;

import java.util.List;
import java.util.Optional;

public class SpreadPacketsCloseToDestinationBehavior extends Wander {

    @Override
    public void communicate(AgentState agentState, AgentCommunication agentCommunication) {

    }

    @Override
    public void act(AgentState agentState, AgentAction agentAction) {
        var destination=agentState.getPerception().getDestinationCells(agentState.getColor().get());
        if(destination!=null&&!destination.isEmpty()) {
            agentState.addMemoryFragment(agentState.getColor().toString(), new AgentMemoryFragment(destination.get(0).getCoordinate()));
        }
        Optional<Packet> carry = agentState.getCarry();
        if (carry.isEmpty()) {
            throw new RuntimeException("Should carry when behavior is SpreadPacketsCloseToDestinationBehavior");
        }
        var destinationMem = agentState.getMemoryFragment(agentState.getColor().get().toString());
        if (destinationMem!=null){
            var reachableDestMem = agentState.getMemoryFragment("isDestinationReachable");
            if(reachableDestMem == null || !reachableDestMem.getReachable()){
                var reachableCoord = agentState.getPerception().isReachable(new Coordinate(agentState.getX(),agentState.getY()), destinationMem.getCoordinate());
                agentState.addMemoryFragment("isDestinationReachable", new AgentMemoryFragment(!reachableCoord.isEmpty()));
            }
            var possiblePackagePotitions= generateAllMovesFromCoordinate(new Coordinate(agentState.getX(),agentState.getY()));
            for (Coordinate coor : possiblePackagePotitions) {
                if (isOkayToPlacePacket(agentState,coor)) {
                    agentAction.putPacket(coor.getX(), coor.getY());
                    return;
                }
            }
        }
        super.act(agentState,agentAction);
    }

    public boolean isOkayToPlacePacket(AgentState agentState,Coordinate coor){
        return agentState.getPerception().getCellPerceptionOnAbsPos(coor.getX(),coor.getY())!=null&&
                agentState.getPerception().getCellPerceptionOnAbsPos(coor.getX(),coor.getY()).isWalkable()&&
                agentState.getPerception().hasNoBlockingNeighbour(generateAllMovesFromCoordinate(coor));
    }



}
