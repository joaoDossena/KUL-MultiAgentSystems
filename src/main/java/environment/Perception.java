package environment;


import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import agent.AgentState;
import environment.world.agent.AgentRep;
import gui.video.ItemDrawer.LinePoints;

/**
 * A class for perceptions for agents. A Perception is a part of the World, perceived
 * by an Agent. A Perception will be filled with Representations of the Items that
 * Agent sees.
 */
public class Perception {

    /**
     * The height of this Perception.
     */
    private int height;

    /**
     * The width of this Perception.
     */
    private int width;


    /**
     * The x-coordinate of self within this Perception.
     */
    private int selfX;

    /**
     * The y-coordinate of self within this Perception.
     */
    private int selfY;

    /**
     * The absolute x-coordinate of the (0,0) position of this Perception.
     */
    private int offsetX;

    /**
     * The absolute y-coordinate of the (0,0) position of this Perception.
     */
    private int offsetY;

    private final CellPerception[][] cells;


    //--------------------------------------------------------------------------
    //		CONSTRUCTOR
    //--------------------------------------------------------------------------

    /**
     * Initializes a new Perception object
     *
     * @param width   width of the Perception
     * @param height  height of the Perception
     * @param offsetX horizontal offset of the Perception
     * @param offsetY vertical offset of the Perception
     */
    public Perception(int width, int height, int offsetX, int offsetY) {
        cells = new CellPerception[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                cells[i][j] = new CellPerception(offsetX + i, offsetY + j);
            }
        }
        setWidth(width);
        setHeight(height);
        setOffsetX(offsetX);
        setOffsetY(offsetY);
    }

    //--------------------------------------------------------------------------
    //		INSPECTORS
    //--------------------------------------------------------------------------


    /**
     * Gets a CellPerception from this Perception using absolute coordinates
     * (the coordinates of the environment/worlds).
     *
     * @return The cell perception if x and y fall within this Perception's bounds, null otherwise.
     */
    @Nullable
    public CellPerception getCellPerceptionOnAbsPos(int x, int y) {
        int dx = x - getOffsetX();
        int dy = y - getOffsetY();
        return getCellAt(dx, dy);
    }

    /**
     * Gets a CellPerception from this Perception using coordinates
     * relative to the agent's position. x and y may be negative.
     *
     * @return The cell perception if x and y fall within this Perception's relative bounds, null otherwise.
     */
    @Nullable
    public CellPerception getCellPerceptionOnRelPos(int x, int y) {
        int dx = getSelfX() + x;
        int dy = getSelfY() + y;
        return getCellAt(dx, dy);
    }


    /**
     * Returns all Representations that are situated right next to the agent that issued this
     * Perception.
     *
     * @return The Representations neighbouring the AgentRep of the agent that issued this
     * Perception
     */
    public CellPerception[] getNeighbours() {
        CellPerception[] neighbours = new CellPerception[8]; // 8 squares surround the agentRep
        int next = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i != 0 || j != 0) {
                    neighbours[next] = getCellAt(getSelfX() + i, getSelfY() + j);
                    next++;
                }
            }
        }
        return neighbours;
    }

    /**
     * Returns all Representations that are situated right next to the agent that issued this
     * Perception. The cells are in successive order in the returning array:
     * <p>
     * 7 0 1
     * 6 A 2
     * 5 4 3
     *
     * @return The Representations neighbouring the AgentRep of the agent that issued this
     * Perception
     */

    public CellPerception[] getNeighboursInOrder() {
        CellPerception[] neighbours = new CellPerception[8]; // 8 squares surround the AgentRep
        int next = 0;
        neighbours[next++] = getCellPerceptionOnRelPos(0, -1);
        neighbours[next++] = getCellPerceptionOnRelPos(1, -1);
        neighbours[next++] = getCellPerceptionOnRelPos(1, 0);
        neighbours[next++] = getCellPerceptionOnRelPos(1, 1);
        neighbours[next++] = getCellPerceptionOnRelPos(0, 1);
        neighbours[next++] = getCellPerceptionOnRelPos(-1, 1);
        neighbours[next++] = getCellPerceptionOnRelPos(-1, 0);
        neighbours[next] = getCellPerceptionOnRelPos(-1, -1);
        return neighbours;
    }


    /**
     * Returns the distance between 2 coordinates
     *
     * @param x1 First position's X
     * @param y1 First position's Y
     * @param x2 Second position's X
     * @param y2 Second position's Y
     * @return The distance between these coordinates
     */
    public static int distance(int x1, int y1, int x2, int y2) {
        // Bird's-eye view (number of steps an agent would need to cover the distance)
        return Math.max(Math.abs(x1 - x2), Math.abs(y1 - y2));
    }

    public static int euclideanDistance(int x1, int y1, int x2, int y2) {
        return (int) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    /**
     * Returns the Manhattan distance between 2 coordinates
     *
     * @param x1 First position's X
     * @param y1 First position's Y
     * @param x2 Second position's X
     * @param y2 Second position's Y
     * @return The Manhattan distance between these coordinates
     */
    public static int manhattanDistance(int x1, int y1, int x2, int y2) {
        //Manhattan distance
        return Math.abs(y2 - y1) + Math.abs(x2 - x1);
    }

    /**
     * Returns the distance between 2 CellPerceptions
     *
     * @param ap1 the first CellPerception
     * @param ap2 the second CellPerception
     * @return the distance between the two CellPerceptions
     */
    public static int distance(CellPerception ap1, CellPerception ap2) {
        return Math.max(Math.abs(ap2.getY() - ap1.getY()), Math.abs(ap2.getX() - ap1.getX()));
    }

    /**
     * Returns the Manhattan distance between 2 CellPerceptions
     *
     * @param ap1 the first CellPerception
     * @param ap2 the second CellPerception
     * @return the manhattan distance between the two CellPerceptions
     */
    public static int ManhattanDistance(CellPerception ap1, CellPerception ap2) {
        return Math.abs(ap2.getY() - ap1.getY()) + Math.abs(ap2.getX() - ap1.getX());
    }

    //--------------------------------------------------------------------------
    //		MUTATORS
    //--------------------------------------------------------------------------

    //--------------------------------------------------------------------------
    //		GETTERS & SETTERS
    //--------------------------------------------------------------------------


    /**
     * Gets the X coordinate of the agent that issued this Perception
     *
     * @return This perception's selfX
     */
    public int getSelfX() {
        return selfX;
    }

    /**
     * Gets the Y coordinate of the agent that issued this Perception
     *
     * @return This perception's selfY
     */
    public int getSelfY() {
        return selfY;
    }

    /**
     * Sets the x-coordinate of the owner of this perception
     *
     * @param x The x-coordinate of the self in this perception
     *          (not in the world)
     */
    public void setSelfX(int x) {
        selfX = x;
    }

    /**
     * Sets the y-coordinate of the owner of this perception
     *
     * @param y The y-coordinate of the self in this perception
     *          (not in the world)
     */
    public void setSelfY(int y) {
        selfY = y;
    }

    /**
     * Sets a given Representation at the given coordinates in this perception
     *
     * @param x    The X coordinate
     * @param y    The Y coordinate
     * @param cell The Representation that is to be set here
     */
    public void setCellPerceptionAt(int x, int y, CellPerception cell) {
        cells[x][y] = cell;
    }

    /**
     * Sets the width of this Perception
     *
     * @param w The new width value
     */
    private void setWidth(int w) {
        this.width = w;
    }

    /**
     * Sets the height of this Perception
     *
     * @param h The new height value
     */
    private void setHeight(int h) {
        this.height = h;
    }

    /**
     * Returns the CellPerception that is positioned on the given coordinates in this Perception.
     */
    @Nullable
    public CellPerception getCellAt(int x, int y) {
        try {
            return cells[x][y];
        } catch (ArrayIndexOutOfBoundsException exc) {
            return null;
        }
    }

    /**
     * Gets the width of this Perception
     *
     * @return This Perception's width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Gets the height of this Perception
     *
     * @return This Perception's height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Gets the horizontal offset of this Perception with respect to the
     * coordinates of the environment/worlds.
     *
     * @return The horizontal offset
     */
    public int getOffsetX() {
        return this.offsetX;
    }

    /**
     * Gets the vertical offset of this Perception with respect to the
     * coordinates of the environment/worlds.
     *
     * @return The vertical offset
     */
    public int getOffsetY() {
        return this.offsetY;
    }

    /**
     * Sets the horizontal offset of this Perception.
     *
     * @param offsetX The new horizontal offset
     */
    private void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
    }

    /**
     * Sets the vertical offset of this Perception.
     *
     * @param offsetY The new vertical offset
     */
    private void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }


    /**
     * Determine the shape of the view that should be drawn in the environment.
     *
     * @return A list of LinePoints that contains all the lines that should be drawn.
     */
    public List<LinePoints> getShape() {
        List<LinePoints> shape = new ArrayList<>();

        final int maxX = cells.length;
        final int maxY = cells[0].length;

        for (int i = 0; i < maxX; i++) {
            for (int j = 0; j < maxY; j++) {
                if (getCellAt(i, j) != null) {
                    if (i + 1 >= maxX || getCellAt(i + 1, j) == null) {
                        shape.add(new LinePoints(new Coordinate(offsetX + i + 1, offsetY + j),
                                new Coordinate(offsetX + i + 1, offsetY + j + 1)));
                    }
                    if (i - 1 < 0 || getCellAt(i - 1, j) == null) {
                        shape.add(new LinePoints(new Coordinate(offsetX + i, offsetY + j),
                                new Coordinate(offsetX + i, offsetY + j + 1)));
                    }
                    if (j + 1 >= maxY || getCellAt(i, j + 1) == null) {
                        shape.add(new LinePoints(new Coordinate(offsetX + i, offsetY + j + 1),
                                new Coordinate(offsetX + i + 1, offsetY + j + 1)));
                    }
                    if (j - 1 < 0 || getCellAt(i, j - 1) == null) {
                        shape.add(new LinePoints(new Coordinate(offsetX + i, offsetY + j),
                                new Coordinate(offsetX + i + 1, offsetY + j)));
                    }
                }
            }
        }
        return shape;
    }


    public void addRep(int i, int j, Representation rep) {
        cells[i][j].addRep(rep);
    }


    public void nullifyCellAt(int i, int j) {
        try {
            cells[i][j] = null;
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
    }

    //--------------------------------------------------------------------------
    //		OUR OWN METHODS
    //--------------------------------------------------------------------------


    public List<CellPerception> getPacketCells() {
        List<CellPerception> cellsWithPackets = new ArrayList<>();
        for (CellPerception[] cellLine : cells) {
            for (CellPerception cell : cellLine) {
                if (cell != null && cell.containsPacket()) {
                    cellsWithPackets.add(cell);
                }
            }
        }
        return cellsWithPackets;
    }

    public List<CellPerception> getPacketCellsForColor(Color clr) {
        List<CellPerception> cellsWithPackets = new ArrayList<>();
        for (CellPerception[] cellLine : cells) {
            for (CellPerception cell : cellLine) {
                if (cell != null && cell.containsPacketOfColor(clr)) {
                    cellsWithPackets.add(cell);
                }
            }
        }
        return cellsWithPackets;
    }

    public List<CellPerception> getEnergyStations() {
        List<CellPerception> energyStations = new ArrayList<>();
        for (CellPerception[] cellLine : cells) {
            for (CellPerception cell : cellLine) {
                if (cell != null && cell.containsEnergyStation()) {
                    energyStations.add(cell);
                }
            }
        }
        return energyStations;
    }

    //TODO Our implementation
    public List<CellPerception> getDestinationCells(Color color) {
        List<CellPerception> cellsWithDestination = new ArrayList<>();
        for (CellPerception[] cellLine : cells) {
            for (CellPerception cell : cellLine) {
                if (cell != null && cell.containsDestination(color)) {
                    cellsWithDestination.add(cell);
                }
            }
        }
        return cellsWithDestination;
    }

    public Map<Color, CellPerception> getDestinationCells() {
        Map<Color, CellPerception> cellsWithDestination = new HashMap<>();
        for (CellPerception[] cellLine : cells) {
            for (CellPerception cell : cellLine) {
                if (cell != null && cell.getDestination() != null) {
                    cellsWithDestination.put(cell.getDestination().getColor(), cell);
                }
            }
        }
        return cellsWithDestination;
    }

    public List<Coordinate> sortWithManhattanDistance(List<Coordinate> possibleCells, int x, int y) {
        // TODO: Now we're using manhattanDistance distance, but in the future we might need to account for walls and stuff.

        HashMap<Coordinate, Integer> possibleWithDistanceMap = new HashMap<>();
        for (Coordinate possibleCell : possibleCells) {
            int distance = manhattanDistance(possibleCell.getX(), possibleCell.getY(), x, y);
            possibleWithDistanceMap.put(possibleCell, distance);
        }
        Map<Coordinate, Integer> sortedHM = sortByValue(possibleWithDistanceMap);
        List<Coordinate> sortedDestinations = new ArrayList<>();
        for (Map.Entry<Coordinate, Integer> item : sortedHM.entrySet()) {
            sortedDestinations.add(item.getKey());
        }
        return sortedDestinations;
    }

    //TODO Add to a UTIL
    // function to sort hashmap by values
    public static HashMap<Coordinate, Integer> sortByValue(HashMap<Coordinate, Integer> hm) {
        // Create a list from elements of HashMap
        List<Map.Entry<Coordinate, Integer>> list =
                new LinkedList<Map.Entry<Coordinate, Integer>>(hm.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<Coordinate, Integer>>() {
            public int compare(Map.Entry<Coordinate, Integer> o1,
                               Map.Entry<Coordinate, Integer> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        // put data from sorted list to hashmap
        HashMap<Coordinate, Integer> temp = new LinkedHashMap<Coordinate, Integer>();
        for (Map.Entry<Coordinate, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    // TODO: Now we're using euclidian distance, but in the future we might need to account for walls and stuff.
    public CellPerception getClosestCell(List<CellPerception> possibleCells, int x, int y) {

        if (possibleCells.isEmpty()){
            return null;
        }

        CellPerception minCell = possibleCells.get(0);

        int minDistance = euclideanDistance(minCell.getX(), minCell.getY(), x, y);

        for (int i = 1; i < possibleCells.size(); i++) {
            int distance = euclideanDistance(possibleCells.get(i).getX(), possibleCells.get(i).getY(), x, y);
            if (distance < minDistance) {
                minDistance = distance;
                minCell = possibleCells.get(i);
            }
        }
        return minCell;
    }

    public Optional<Coordinate> getShortestMoveToCell(CellPerception cell, int agentX, int agentY) {

        List<Coordinate> moves = getPermittedMovesRel();
        if (moves == null || moves.isEmpty()) {
            return Optional.empty();
        }

        Coordinate minMove = moves.get(0);
        int minDistance = -1;

        for (Coordinate move : moves) {
            int x = move.getX();
            int y = move.getY();
            int distanceAfterMove = euclideanDistance(agentX + x, agentY + y, cell.getX(), cell.getY());

            if (minDistance == -1 || distanceAfterMove < minDistance) {
                minMove = move;
                minDistance = distanceAfterMove;
            }
        }
        return Optional.of(minMove);
    }

    public List<Coordinate> getPermittedMovesRel() {

        return Coordinate.getNeighboursRelative().stream()
                .filter(neighbour -> getCellPerceptionOnRelPos(neighbour.getX(), neighbour.getY()) != null)
                .filter(neighbour -> getCellPerceptionOnRelPos(neighbour.getX(), neighbour.getY()).isWalkable())
                .filter(neighbour -> !getCellPerceptionOnRelPos(neighbour.getX(), neighbour.getY()).containsAgent())
                .collect(Collectors.toCollection(ArrayList::new));
    }


    public CellPerception[][] getAllVision() {
        return cells;
    }

    public ArrayList<AgentRep> getVisibleAgents() {
        ArrayList<AgentRep> agentReps = new ArrayList<>();
        for (CellPerception[] cellLine : cells) {
            for (CellPerception cell : cellLine) {
                if (cell != null && cell.containsAgent()) {
                    agentReps.add(cell.getAgentRepresentation().get());
                }
            }
        }
        return agentReps;
    }

    protected List<Coordinate> getWalkableNeighbours(Coordinate coordinate) {

        return coordinate.getNeighboursAbsolute().stream()
                .filter(neighbour -> getCellPerceptionOnAbsPos(neighbour.getX(), neighbour.getY()) != null)
                .filter(neighbour -> getCellPerceptionOnAbsPos(neighbour.getX(), neighbour.getY()).isWalkable())
                .filter(neighbour -> !getCellPerceptionOnAbsPos(neighbour.getX(), neighbour.getY()).containsAgent())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public boolean isNeighbour(Coordinate c1, Coordinate c2) {
        return Math.abs(c1.getX() - c2.getX()) <= 1 && Math.abs(c1.getY() - c2.getY()) <= 1;
    }

    public Optional<Coordinate> isReachable(Coordinate from, Coordinate to) {
        if(getCellPerceptionOnAbsPos(to.getX(), to.getY()) == null) return Optional.empty();
        if(isNeighbour(from, to)) return Optional.of(to);
        List<Coordinate> moves = aStar(from, getCellPerceptionOnAbsPos(to.getX(), to.getY()));
        if(moves.isEmpty()) return Optional.empty();
        return Optional.of(moves.get(1));
    }

    public boolean packetIsProblematic(List<Coordinate> neighboursOfPacket, Optional<Coordinate> reachable){
        //packet touch other Packet(s) and it is reachable
        //packet is far from Destination and it is reachable
        return (reachable.isPresent() &&
                (!hasNoBlockingNeighbour(neighboursOfPacket) ));
    }

    public Coordinate getShortestMoveToCell(CellPerception cell, List<Coordinate> moves, int agentX, int agentY) {
        Coordinate minMove = moves.get(0);
        int minDistance = -1;

        for (int i = 0; i < moves.size(); i++) {
            Coordinate move = moves.get(i);
            int x = move.getX();
            int y = move.getY();
            int distanceAfterMove = euclideanDistance(agentX + x, agentY + y, cell.getX(), cell.getY());
            if (getCellPerceptionOnRelPos(x, y) != null && getCellPerceptionOnRelPos(x, y).isWalkable())
            {
                if(minDistance == -1 || distanceAfterMove < minDistance){
                    minMove = move;
                    minDistance = distanceAfterMove;
                }

            }
        }
        return minMove;
    }

    public boolean hasNoBlockingNeighbour( List<Coordinate> neighbours) {
        for(Coordinate neighbor : neighbours){
            CellPerception cell = getCellPerceptionOnAbsPos(neighbor.getX(),neighbor.getY());

            if(cell != null && (cell.containsPacket() || cell.containsAnyDestination()
                    || cell.containsWall()))
                return false;
        }
        return true;
    }

    public List<Coordinate> aStar(Coordinate from, CellPerception targetCell) {

        Coordinate goal;
        goal = Coordinate.of(targetCell.getX(), targetCell.getY());


        Coordinate start = Coordinate.of(from.getX(), from.getY());

        Set<Coordinate> openSet = new HashSet<>();
        openSet.add(start);

        Map<Coordinate, Coordinate> cameFrom = new HashMap<>();

        Map<Coordinate, Integer> gScore = new HashMap<>();
        gScore.put(start, 0);

        Map<Coordinate, Double> fScore = new HashMap<>();
        fScore.put(start, euclideanDistance(start, goal));

        while (!openSet.isEmpty()) {

            Coordinate current = openSet.stream().min(Comparator.comparing(fScore::get)).get();

            List<Coordinate> allNeighborsAbs = current.getNeighboursAbsolute();
            for (Coordinate neighbour : allNeighborsAbs) {
                if (neighbour.equals(goal)) {
                    cameFrom.put(neighbour, current);
                    return reconstructPath(cameFrom, neighbour);
                }
            }

            openSet.remove(current);

            List<Coordinate> permittedMoves = getWalkableNeighbours(current);
            for (Coordinate neighbour : permittedMoves) {

                var tentative_gScore = gScore.get(current) + 1;
                if (!gScore.containsKey(neighbour) || tentative_gScore < gScore.get(neighbour)) {
                    cameFrom.put(neighbour, current);
                    gScore.put(neighbour, tentative_gScore);
                    fScore.put(neighbour, tentative_gScore + euclideanDistance(neighbour, goal));
                    openSet.add(neighbour);
                }
            }
        }
        return List.of();
    }

    private List<Coordinate> reconstructPath(Map<Coordinate, Coordinate> cameFrom, Coordinate current) {
        List<Coordinate> totalPath = new ArrayList<>();
        totalPath.add(current);
        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            totalPath.add(0, current);
        }
        return totalPath;
    }

    protected double euclideanDistance(Coordinate c1, Coordinate c2) {

        return Math.sqrt(
                (c1.getX() - c2.getX()) * (c1.getX() - c2.getX())
                        + (c1.getY() - c2.getY()) * (c1.getY() - c2.getY()));
    }

    public Coordinate getClosestToCoordinate(Coordinate destCoordinate) {
        double distance=Double.MAX_VALUE;
        CellPerception nearestCell=null;
        for(CellPerception[] cellsOfLine : cells)
            for(CellPerception cell:cellsOfLine){
                if(euclideanDistance(cell.toCoordinate(),destCoordinate)<distance){
                    distance=euclideanDistance(cell.toCoordinate(),destCoordinate);
                    nearestCell=cell;
                }
            }
        return nearestCell.toCoordinate();
    }
}

