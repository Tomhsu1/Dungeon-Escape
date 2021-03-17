package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

public class Player {
    /** Notes:
     * position => x and y
     *
     * initialize position in first room generated
     *
     * move function, maybe takes in direction as input
     *
     * represented as @ tile
     *
     * can pick up key/open doors??
     *
     * other actions
     *
     * keep track of current pos AND last pos so that we can redraw the floor in
     * the floor tile in the last position
     */

    private Position currPos;
    private Position prevPos;
    private boolean hasKey;
    private int health;

    public Player(Position currPos, int chosenHealth) {
//        this.currPos = new Position(currPos.getX(), currPos.getY());
//        this.prevPos = currPos;
        setCurrPos(currPos);
        hasKey = false;
        health = chosenHealth;
    }

    public void giveKey() {
        hasKey = true;
    }

    public void removeKey() {
        hasKey = false;
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            Game.gameOver();
        }
    }

    public void gainHealth(int gainedHealth) {
        health += gainedHealth;
    }

    public int getHealth() {
        return health;
    }

    public void setCurrPos(Position pos) {
        currPos = new Position(pos.getX(), pos.getY());
        prevPos = currPos;
    }

    public void draw(TETile[][] world) {
        world[prevPos.getX()][prevPos.getY()] = Tileset.FLOOR;
        world[currPos.getX()][currPos.getY()] = Tileset.AVATAR;
    }

    /** Moves the player in the desired direction. If the new position is a
     * wall tile, then do nothing.
     * @param direction - the desired direction, as a char
     */
    public boolean move(TETile[][] world, char direction, Random rng) {
        boolean progressedFloor = false;
        int x = currPos.getX();
        int y = currPos.getY();
        int newX = x;
        int newY = y;
        switch (direction) {
            case 'W':
                newY += 1;
                break;
            case 'A':
                newX -= 1;
                break;
            case 'S':
                newY -= 1;
                break;
            case 'D':
                newX += 1;
                break;
            default:
                break;
        }
        TETile tile = world[newX][newY];
        if (tile == Tileset.WALL) {
            return false;
        } else if (tile == Tileset.STAIRS && !hasKey) {
            Game.setCurrentGameTip("You need a key to unlock this door.");
            return false;
        } else if (tile == Tileset.STAIRS && hasKey) {
//            WorldGeneration.generateWorld(world);
//            Game.render();
            progressedFloor = true;
        } else if (tile == Tileset.KEY) {
            // add key to inventory
            giveKey();
            Game.setCurrentGameTip("You have found a key.");
        } else if (tile == Tileset.ENEMY || tile == Tileset.ENEMYWITHKEY) {
            Encounter enc = new Encounter(rng);
            int outcome = enc.checkWin();
            if (outcome == Encounter.WIN) {
                Game.setCurrentGameTip("Enemy Defeated!");
                int chance = RandomUtils.uniform(rng, 3);
                if (chance == 0) {
                    gainHealth(1);
                    Game.setCurrentGameTip("Enemy Defeated! You Gain A Life.");
                }
                Game.setEnemiesDefeated(Game.getEnemiesDefeated() + 1);
            } else if (outcome == Encounter.LOSE) {
                Game.setCurrentGameTip("You Lost!");
                takeDamage(1);
            } else if (outcome == Encounter.NAN) {
                Game.setCurrentGameTip("You Lost! You Were Supposed To Enter A Number!");
                takeDamage(1);
            } else if (outcome == Encounter.NAC) {
                Game.setCurrentGameTip("You Lost! You Were Supposed To Enter R, P, or S!");
                takeDamage(1);
            } else { // REPLAY
                Game.setCurrentGameTip("Get The Key To Unlock The Next Floor!");
            }
            if (tile == Tileset.ENEMYWITHKEY) {
                giveKey();
                Game.setCurrentGameTip("You Have Retrieved The Key From The Enemy!");
            }
            WorldGeneration.removeEnemy(newX, newY);
        }

        prevPos = new Position(x, y);
        currPos.setX(newX);
        currPos.setY(newY);
        return progressedFloor;
    }
}
