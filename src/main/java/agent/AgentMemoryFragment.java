package agent;

import environment.Coordinate;

import java.util.ArrayList;
import java.util.List;

public class AgentMemoryFragment {
    private Coordinate coordinate = null;
    private boolean isReachable=false;
    private List<Coordinate> coordinates=new ArrayList<>();

    public AgentMemoryFragment(Coordinate coordinate) {
        coordinates = new ArrayList<>();
        addToCoordinatesList(coordinate);
    }
    public AgentMemoryFragment(Boolean isReachable){
        this.isReachable = isReachable;
    }

    public AgentMemoryFragment(List<Coordinate> coordinates) {
        this.coordinates = coordinates;
    }

    public Coordinate getCoordinate() { return this.coordinate; }

    public boolean getReachable() {
        return isReachable;
    }
    public void addToCoordinatesList(Coordinate coordinate) {
        if (!coordinates.contains(coordinate)){
            coordinates.add(coordinate);
        }
    }

    public List<Coordinate> getCoordinates() {
        return this.coordinates;
    }
}
