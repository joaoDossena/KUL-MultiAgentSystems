package agent.behavior.impl.first;

import agent.AgentAction;
import agent.AgentCommunication;
import agent.AgentMemoryFragment;
import agent.AgentState;
import agent.behavior.impl.wander.Wander;
import environment.CellPerception;
import environment.Coordinate;
import environment.Mail;
import environment.Perception;
import environment.world.agent.AgentRep;
import environment.world.energystation.EnergyStation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NoPacketBehavior extends Wander {
    private final String ENERGY_STATIONS = "EnergyStations";

    private boolean goToStation(AgentAction agentAction,Perception perception,AgentState agentState){
        var currentEnergy=agentState.getBatteryState();
        var memoryfragment=agentState.getMemoryFragment(ENERGY_STATIONS);
        if(memoryfragment==null)return false;
        var stations=memoryfragment.getCoordinates();
        Coordinate destination=null;
        stations= (ArrayList<Coordinate>) perception.shortWithManhattanDistance(stations,agentState.getX(),agentState.getY());
        if(!agentState.hasCarry()) {
            if ((currentEnergy-calculateDistanceWithEnergy(10,agentState,new Coordinate(stations.get(0).getX(),stations.get(0).getY()-1)))<400){
                walkTowardsCoordinate(agentAction,agentState,new Coordinate(stations.get(0).getX(),stations.get(0).getY()-1));
                return true;
            }
        }
        return false;
    }



    @Override
    public void act(AgentState agentState, AgentAction agentAction) {
        if(goToStation(agentAction,agentState.getPerception(),agentState)) return;
        Perception perception = agentState.getPerception();
        checkForEnergyStations(agentState,perception);
        var neighbours = perception.getNeighbours();

        for(CellPerception neighbor : neighbours){
            if(neighbor != null && neighbor.containsPacket()){
                agentAction.pickPacket(neighbor.getX(), neighbor.getY());
                return;
            }
        }

        walkTowardsClosestPacket(agentState, agentAction);
    }

    private void walkTowardsClosestPacket(AgentState agentState, AgentAction agentAction) {
        Perception perception = agentState.getPerception();
        List<CellPerception> packets = perception.getPacketCells();

        // If no packets in perception, wander randomly.
        if(packets.isEmpty()){
            super.act(agentState, agentAction);
            return;
        }

        // Otherwise, look for closest packet.
        CellPerception minCell = perception.getClosestCell(packets, agentState.getX(), agentState.getY());

        Optional<Coordinate> minMoveOpt = perception.getShortestMoveToCell(minCell, agentState.getX(), agentState.getY());
        if(minMoveOpt.isEmpty()){
            agentAction.skip();
            return;
        }
        Coordinate minMove = minMoveOpt.get();

        agentState.addMemoryFragment("lastMove", new AgentMemoryFragment(new Coordinate(minMove.getX(), minMove.getY())));
        agentAction.step(agentState.getX() + minMove.getX(), agentState.getY() + minMove.getY());
    }
}
