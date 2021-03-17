package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Room {
    /** Notes:
     * maybe have room extend hallway? or should we write an interface for both,
     * since they are both "structures"
     *
     *
     * can just just use java.awt.Rectangle
     * for position, just use java.awt.Point
     */

    public static final int MAX_SIZE = 10;
    public static final int MIN_SIZE = 4;
    protected int width;
    private int height;
    private Position upperLeft;
    private Position lowerRight;
    protected Position middle;
    protected List<Position> wallPositions;
    protected List<Position> floorPositions;
    private boolean startsWithEnemy;

    public Room(Random rng, int x, int y) {
        width = RandomUtils.uniform(rng, MIN_SIZE, MAX_SIZE) - 1;
        height = RandomUtils.uniform(rng, MIN_SIZE, MAX_SIZE) - 1;
        upperLeft = new Position(x, y);
        lowerRight = new Position(x + width, y - height);
        middle = new Position(x + width / 2, y - height / 2);
        wallPositions = new ArrayList<>();
        floorPositions = new ArrayList<>();
        setWallPositions();
        setFloorPositions();
        startsWithEnemy = false;
    }

    public Room() {
        wallPositions = new ArrayList<>();
        floorPositions = new ArrayList<>();
    }

    /** Draws itself onto the world. */
    public void draw(TETile[][] world, Random rng) {
        for (Position fp : floorPositions) {
//            world[fp.getX()][fp.getY()] = TETile.colorVariant(Tileset.FLOOR, 100, 100, 100, rng);
            world[fp.getX()][fp.getY()] = Tileset.FLOOR;
        }
        for (Position wp : wallPositions) {
//            world[wp.getX()][wp.getY()] = TETile.colorVariant(Tileset.WALL, 50, 50, 50, rng);
            world[wp.getX()][wp.getY()] = Tileset.WALL;
        }
    }

    /** Returns false if all wall tiles are Tileset.NOTHING. */
    public boolean intersectsOtherRoom(TETile[][] world) {
        for (Position p : wallPositions) {
            if (world[p.getX()][p.getY()] != Tileset.NOTHING) {
                return true;
            }
        }
        return false;
    }

    public Position getMiddle() {
        return middle;
    }

    public List<Position> getFloorPositions() {
        return floorPositions;
    }

    /** Adds all wall positions to a list. */
    private void setWallPositions() {
        // top and bottom walls
        for (int i = upperLeft.getX(); i <= lowerRight.getX(); ++i) {
            Position topWall = new Position(i, upperLeft.getY());
            Position bottomWall = new Position(i, lowerRight.getY());
            wallPositions.add(topWall);
            wallPositions.add(bottomWall);
        }
        // right and left walls
        for (int j = upperLeft.getY() - 1; j >= lowerRight.getY() + 1; --j) {
            Position leftWall = new Position(upperLeft.getX(), j);
            Position rightWall = new Position(lowerRight.getX(), j);
            wallPositions.add(leftWall);
            wallPositions.add(rightWall);
        }
    }

    /** Adds all floor positions to a list. */
    private void setFloorPositions() {
        for (int x = upperLeft.getX() + 1; x <= lowerRight.getX() - 1; ++x) {
            for (int y = upperLeft.getY() - 1; y >= lowerRight.getY() + 1; --y) {
                floorPositions.add(new Position(x, y));
            }
        }
    }

    public void setStartsWithEnemyTrue() {
        startsWithEnemy = true;
    }

    public boolean getStartsWithEnemy() {
        return startsWithEnemy;
    }
}
