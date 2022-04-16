package agent.behavior.impl.v3.change;

import agent.behavior.BehaviorChange;
import environment.CellPerception;
import java.util.Arrays;
import java.util.Objects;

public class ReachedPacketBehaviorChange extends BehaviorChange {

    @Override
    public void updateChange() {

    }

    @Override
    public boolean isSatisfied() {

        return Arrays.stream(this.getAgentState().getPerception().getNeighbours())
                     .filter(Objects::nonNull)
                     .anyMatch(CellPerception::containsPacket);
    }
}
