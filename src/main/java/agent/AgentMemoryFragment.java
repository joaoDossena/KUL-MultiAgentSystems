package agent;

import environment.Coordinate;

import java.util.ArrayList;
import java.util.List;

public class AgentMemoryFragment {
    private final List<Coordinate> coordinates;

    public AgentMemoryFragment(Coordinate coordinate) {
        coordinates = new ArrayList<>();
        addToCoordinatesList(coordinate);
    }

    public AgentMemoryFragment(List<Coordinate> coordinates) {
        this.coordinates = coordinates;
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
