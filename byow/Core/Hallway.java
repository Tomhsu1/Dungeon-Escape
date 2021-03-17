package byow.Core;

import java.util.Random;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class Hallway extends Room {
    /** Notes:
     * keep a list of positions for each tile of the hallway within the 2d world
     *
     * width of the floor section is 1, but length of hallway should
     * be random
     *
     * can be horizontal or vertical, should be able to make turns in hallways
     * (straight hallways that intersect)
     *
     *
     * maybe randomly choose a starting position of an existing room/hallway
     * to start drawing a hallway/room
     *
     * https://roguesharp.wordpress.com/2016/04/03/
     * roguesharp-v3-tutorial-connecting-rooms-with-hallways/
     * use this ^
     * basically first create random rooms, then connect current room to prev
     * room using an L hallway connecting both centers, randomly select between
     * two L orientations
     *
     * make createVerticalHallway
     * make createHorizontalHallway
     *
     * figure out a way to draw walls correctly
     *
     */

//    private final int HORIZONTAL = 0;
//    private final int VERTICAL = 1;
//    private final int MAX_LENGTH = 15;
//    /** The width of the hallway, including two surrounding walls. */
//    private final int WIDTH = 3;
//    /** The length of the hallway, to be selected randomly. */
//    private int length;
//    /** The beginning FLOOR tile. */
//    private Position end1;
//    /** The end FLOOR tile. */
//    private Position end2;
//    /** The middle FLOOR tile. */
//    private Position middle;
//    /** Horizontal or vertical. */
//    private int orientation;
//    /** A list of the positions of every wall. */
//    private List<Position> wallPositions;

    private final boolean HORIZONTAL = false;
    private final boolean VERTICAL = true;
    private final int WIDTH = 3;
    private int length;
    private boolean orientation;
    private Position startPos;

    public Hallway(TETile[][] world, int start, int end, boolean orientation, int constantPos) {
        width = WIDTH;

        this.orientation = orientation;
        if (orientation == HORIZONTAL) {
            length = Math.abs(start - end);
            setFloorPositionsHorizontal(start, end, constantPos);
            startPos = new Position(start, constantPos);
        } else {
            length = Math.abs(start - end);
            setFloorPositionsVertical(start, end, constantPos);
            startPos = new Position(constantPos, start);
        }
        drawFloors(world);
    }

    /** Adds all wall positions to a list. */
//    private void getWallPositions() {
//        // top and bottom walls
//        for (int i = upperLeft.getX(); i <= lowerRight.getX(); ++i) {
//            Position topWall = new Position(i, upperLeft.getY());
//            Position bottomWall = new Position(i, lowerRight.getY());
//            wallPositions.add(topWall);
//            wallPositions.add(bottomWall);
//        }
//        // right and left walls
//        for (int j = upperLeft.getY() - 1; j >= lowerRight.getY() + 1; --j) {
//            Position leftWall = new Position(upperLeft.getX(), j);
//            Position rightWall = new Position(lowerRight.getX(), j);
//            wallPositions.add(leftWall);
//            wallPositions.add(rightWall);
//        }
//    }

    /** Adds all horizontal floor positions to a list. */
    private void setFloorPositionsHorizontal(int startX, int endX, int yPos) {
        for (int x = Math.min(startX, endX); x <= Math.max(startX, endX); ++x) {
            floorPositions.add(new Position(x, yPos));
        }
    }

    /** Adds all vertical floor positions to a list. */
    private void setFloorPositionsVertical(int startY, int endY, int xPos) {
        for (int y = Math.min(startY, endY); y <= Math.max(startY, endY); ++y) {
            floorPositions.add(new Position(xPos, y));
        }
    }

    void getWallPositions(TETile[][] world) {
        for (Position fp : floorPositions) {
            int x = fp.getX();
            int y = fp.getY();
            if (orientation == HORIZONTAL) {
                if (world[x][y + 1] == Tileset.NOTHING) {
                    wallPositions.add(new Position(x, y + 1));
                }
                if (world[x][y - 1] == Tileset.NOTHING) {
                    wallPositions.add(new Position(x, y - 1));
                }
            } else {
                if (world[x - 1][y] == Tileset.NOTHING) {
                    wallPositions.add(new Position(x - 1, y));
                }
                if (world[x + 1][y] == Tileset.NOTHING) {
                    wallPositions.add(new Position(x + 1, y));
                }
            }
        }
        // check corners of floors to be walls
        for (int x = startPos.getX() - 1; x <= startPos.getX() + 1; x += 2) {
            for (int y = startPos.getY() - 1; y <= startPos.getY() + 1; y += 2) {
                if (world[x][y] == Tileset.NOTHING) {
                    wallPositions.add(new Position(x, y));
                }
            }
        }
    }

    public void drawFloors(TETile[][] world) {
        for (Position fp : floorPositions) {
            world[fp.getX()][fp.getY()] = Tileset.FLOOR;
        }
    }

    public void drawWalls(TETile[][] world, Random rng) {
        for (Position wp : wallPositions) {
            world[wp.getX()][wp.getY()] = Tileset.WALL;
        }
    }
}
