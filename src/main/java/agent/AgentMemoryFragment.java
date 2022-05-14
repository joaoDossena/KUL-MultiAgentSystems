package agent;

import environment.Coordinate;

public class AgentMemoryFragment {
    private Coordinate coordinate = null;
    private boolean isReachable=false;

    public AgentMemoryFragment(Coordinate coordinate){
        this.coordinate = coordinate;
    }
    public AgentMemoryFragment(Boolean isReachable){
        this.isReachable = isReachable;
    }


    public Coordinate getCoordinate() { return this.coordinate; }

    public boolean getReachable() {
        return isReachable;
    }
}
