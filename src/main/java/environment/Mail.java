package environment;

import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;

public class Mail {

    public Mail(String from, String to, String message){
        this.from=from;
        this.to=to;
        this.message=message;
    }

    public Mail(String from, String to, ArrayList<Coordinate> coordinates){
        this.from=from;
        this.to=to;
        this.coordinates=coordinates;
    }

    private ArrayList<Coordinate> coordinates;

    private String from;

    private String message;

    private String to;

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getMessage() {
        return message;
    }

    public ArrayList<Coordinate> getCoordinates(){ return coordinates;}

}
