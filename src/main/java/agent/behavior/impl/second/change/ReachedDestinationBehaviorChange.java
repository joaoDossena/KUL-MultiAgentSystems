package agent.behavior.impl.second.change;

import agent.behavior.BehaviorChange;
import environment.CellPerception;
import java.awt.Color;
import java.util.Arrays;
import java.util.Objects;

public class ReachedDestinationBehaviorChange extends BehaviorChange {

    private Color packetColor;

    @Override
    public void updateChange() {
        this.packetColor = this.getAgentState().getCarry().get().getColor();
    }

    @Override
    public boolean isSatisfied() {

        return Arrays.stream(this.getAgentState().getPerception().getNeighbours())
                     .filter(Objects::nonNull)
                     .anyMatch(cellPerception -> cellPerception.containsDestination(packetColor));
    }
}
