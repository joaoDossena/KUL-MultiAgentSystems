package environment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import util.Pair;

/**
 * A class to represent a 2-dimensional coordinate.
 */
public class Coordinate extends Pair<Integer, Integer> {

    public static Coordinate of(int x, int y) {
        return new Coordinate(x, y);
    }

    public Coordinate(int x, int y) {
        super(x, y);
    }

    public int getX() {
        return this.first;
    }

    public int getY() {
        return this.second;
    }

    public String toString() {
        return String.format("(%d,%d)", this.getX(), this.getY());
    }

    public Coordinate diff(Coordinate other) {
        return new Coordinate(first - other.first, second - other.second);
    }

    public Coordinate add(Coordinate other) {
        return new Coordinate(first + other.first, second + other.second);
    }

    public boolean any(Predicate<Integer> pred) {
        return pred.test(first) || pred.test(second);
    }

    public boolean all(Predicate<Integer> pred) {
        return pred.test(first) && pred.test(second);
    }

    /**
     * Returns a new Coordinate containing the sign of this (-1, 0 or 1)
     */
    public Coordinate sign() {
        int newFst = 0, newSnd = 0;

        if (first != 0)
            newFst = first < 0 ? -1 : 1;
        if (second != 0)
            newSnd = second < 0 ? -1 : 1;

        return new Coordinate(newFst, newSnd);
    }

    public Coordinate invertedSign() {
        int newFst = 0, newSnd = 0;

        if (first != 0)
            newFst = first < 0 ? 1 : -1;
        if (second != 0)
            newSnd = second < 0 ? 1 : -1;

        return new Coordinate(newFst, newSnd);
    }

    public List<Coordinate> getNeighboursAbsolute() {

        List<Coordinate> neighbours = new ArrayList<>();
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                if (x == 0 && y == 0)
                    continue;
                neighbours.add(new Coordinate(getX() + x, getY() + y));
            }
        }
        return neighbours;
    }

    public static List<Coordinate> getNeighboursRelative() {

        List<Coordinate> neighbours = new ArrayList<>();
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                if (x == 0 && y == 0)
                    continue;
                neighbours.add(new Coordinate(x, y));
            }
        }
        return neighbours;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Coordinate casted))
            return false;

        if (casted.getX() != getX())
            return false;

        return casted.getY() == getY();
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

}
