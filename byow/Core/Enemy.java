package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class Enemy {
    private Position currPos;
    private Position prevPos;
    private boolean hasKey;

    public Enemy(Position currPos) {
        this.currPos = new Position(currPos.getX(), currPos.getY());
        this.prevPos = currPos;
        hasKey = false;
    }

    public void draw(TETile[][] world) {
        world[prevPos.getX()][prevPos.getY()] = Tileset.FLOOR;
        if (hasKey) {
            world[currPos.getX()][currPos.getY()] = Tileset.ENEMYWITHKEY;
        } else {
            world[currPos.getX()][currPos.getY()] = Tileset.ENEMY;
        }
    }

    public void move(TETile[][] world, int randomDirection) {
        int x = currPos.getX();
        int y = currPos.getY();
        int newX = x;
        int newY = y;
        switch (randomDirection) {
            case 0:
                newY += 1;
                break;
            case 1:
                newX -= 1;
                break;
            case 2:
                newY -= 1;
                break;
            default:
                newX += 1;
                break;
        }
        TETile tile = world[newX][newY];
        if (tile != Tileset.FLOOR && tile != Tileset.KEY) {
            return;
        }
        if (tile == Tileset.KEY) {
            Game.setCurrentGameTip("An Enemy Has Picked Up The Key!");
            hasKey = true;
        }
        prevPos = new Position(x, y);
        currPos.setX(newX);
        currPos.setY(newY);
    }

    public Position getCurrPos() {
        return currPos;
    }
}
