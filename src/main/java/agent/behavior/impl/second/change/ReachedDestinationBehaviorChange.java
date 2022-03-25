package agent.behavior.impl.second.change;

import agent.behavior.BehaviorChange;
import java.util.Arrays;
import java.util.Objects;

public class ReachedDestinationBehaviorChange extends BehaviorChange {

    @Override
    public void updateChange() {

    }

    @Override
    public boolean isSatisfied() {

        return Arrays.stream(this.getAgentState().getPerception().getNeighbours())
                     .filter(Objects::nonNull)
                     .anyMatch(cellPerception -> cellPerception.containsDestination(this.getAgentState().getCarry().get().getColor()));
    }
}
