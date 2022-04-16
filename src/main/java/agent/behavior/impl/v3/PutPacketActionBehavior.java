package agent.behavior.impl.v3;

import agent.AgentAction;
import agent.AgentState;
import environment.CellPerception;
import environment.world.packet.Packet;

import java.util.Optional;
import java.util.function.Predicate;

public class PutPacketActionBehavior extends ActionBehavior {

    @Override
    protected void doAction(AgentAction agentAction, CellPerception cell) {
        
        agentAction.putPacket(cell.getX(), cell.getY());
    }

    @Override
    protected Predicate<CellPerception> getContainsTargetPredicate(AgentState agentState) {

        Optional<Packet> carry = agentState.getCarry();
        if (carry.isEmpty()) {
            throw new RuntimeException("Should carry when behavior is PutPacketActionBehavior");
        }

        return cellPerception -> cellPerception.containsDestination(carry.get().getColor());
    }
}
