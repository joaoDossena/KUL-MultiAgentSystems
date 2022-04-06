package agent;

import environment.Coordinate;

import java.util.ArrayList;

public class AgentMemoryFragment {
    private ArrayList<Coordinate> coordinates = null;

    public AgentMemoryFragment(Coordinate coordinate){
        coordinates=new ArrayList<>();
        addToCoordinatesList(coordinate);
    }

    public AgentMemoryFragment(ArrayList<Coordinate> coordinates){
        this.coordinates=coordinates;
    }

    public void addToCoordinatesList (Coordinate coordinate){
        if(!coordinates.contains(coordinate)) coordinates.add(coordinate);
    }

    public ArrayList<Coordinate> getCoordinates() { return this.coordinates; }
}
