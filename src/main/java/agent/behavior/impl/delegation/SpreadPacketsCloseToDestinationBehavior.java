package agent.behavior.impl.delegation;

import agent.AgentAction;
import agent.AgentCommunication;
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
        Optional<Packet> carry = agentState.getCarry();
        if (carry.isEmpty()) {
            throw new RuntimeException("Should carry when behavior is SpreadPacketsCloseToDestinationBehavior");
        }
        var destinationMem = agentState.getMemoryFragment(agentState.getCarry().get().getColor().toString());
        if ( destinationMem!=null){
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
