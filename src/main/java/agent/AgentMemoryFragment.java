package agent;

import environment.Coordinate;

public class AgentMemoryFragment {
    private Coordinate coordinate = null;

    public AgentMemoryFragment(Coordinate coordinate){
        this.coordinate = coordinate;
    }

    public Coordinate getCoordinate() { return this.coordinate; }
}
