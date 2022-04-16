package environment;

import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.List;

public class Mail {

    public Mail(String from, String to, String message){
        this.from=from;
        this.to=to;
        this.message=message;
    }

    public Mail(String from, String to, List<Coordinate> coordinates){
        this.from=from;
        this.to=to;
        this.coordinates=coordinates;
    }

    private List<Coordinate> coordinates;

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

    public List<Coordinate> getCoordinates(){ return coordinates;}

}
