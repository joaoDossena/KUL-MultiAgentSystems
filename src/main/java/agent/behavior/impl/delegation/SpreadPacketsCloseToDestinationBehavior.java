package agent.behavior.impl.delegation;

import agent.AgentAction;
import agent.AgentCommunication;
import agent.AgentMemoryFragment;
import agent.AgentState;
import agent.behavior.Behavior;
import agent.behavior.impl.wander.BetterWander;
import environment.Coordinate;
import environment.Perception;
import environment.world.packet.Packet;
import environment.Environment;

import java.util.List;
import java.util.Optional;

public class SpreadPacketsCloseToDestinationBehavior extends BetterWander {

    @Override
    public void communicate(AgentState agentState, AgentCommunication agentCommunication) {

    }

    @Override
    public void act(AgentState agentState, AgentAction agentAction) {
        var destination=agentState.getPerception().getDestinationCells(agentState.getColor().get());
        if(destination!=null) {
            agentState.addMemoryFragment(agentState.getColor().toString(), new AgentMemoryFragment(destination.get(0).getCoordinate()));
        }
        Optional<Packet> carry = agentState.getCarry();
        if (carry.isEmpty()) {
            throw new RuntimeException("Should carry when behavior is SpreadPacketsCloseToDestinationBehavior");
        }
        var destinationMem = agentState.getMemoryFragment(agentState.getColor().get().toString());
        if ( destinationMem!=null){
            var reachableDestMem=agentState.getMemoryFragment("isDestinationReachable");
            if(reachableDestMem==null||!reachableDestMem.getReachable()){
                agentState.addMemoryFragment("isDestinationReachable",new AgentMemoryFragment(agentState.getPerception().isReachable(new Coordinate(agentState.getX(),agentState.getY()),destinationMem.getCoordinate())));
            }
            var destinationCoordinate = destinationMem.getCoordinate();
            if (Environment.chebyshevDistance(destinationCoordinate, new Coordinate(agentState.getX(), agentState.getY())) <= 8) {
                var PossibleCoordinatesForPackage = generateAllMovesFromCoordinate(new Coordinate(agentState.getX(), agentState.getY()));
                var packageCoordinate = findBestNewLocation(PossibleCoordinatesForPackage, destinationCoordinate, agentState.getPerception());
                if (packageCoordinate != null) {
                    agentAction.putPacket(packageCoordinate.getX(), packageCoordinate.getY());
                    return;
                }
            }
        }
        super.act(agentState,agentAction);
    }

    private Coordinate findBestNewLocation(List<Coordinate> possibleCoordinatesForPackage, Coordinate destinationCoordinate, Perception perception) {
        var sortedCoordinates = prioritizeWithManhattan(possibleCoordinatesForPackage,perception,destinationCoordinate);
        for( Coordinate coor : sortedCoordinates){
            if(perception.getCellPerceptionOnAbsPos(coor.getX(),coor.getY()).isWalkable())
                if(perception.hasNoNeighbouringPacket(generateAllMovesFromCoordinate(coor))) return coor;
        }
        return null;
    }

}
