package byow.Core;

import byow.InputDemo.InputSource;
import byow.InputDemo.StringInputDevice;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.In;
import edu.princeton.cs.introcs.StdDraw;
import edu.princeton.cs.introcs.StdRandom;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Game {
    public static final int WIDTH = 80;
    public static final int HEIGHT = 40;
    private static final int MAIN_MENU_DIM = 40;
    private static StringBuilder inputString = new StringBuilder();
    private static TETile[][] world;
    private static TERenderer ter;
    private static int mouseX;
    private static int mouseY;
    private static int floor = 1;
    private static Player player;
    private static int playerHealth;
    private static int enemiesDefeated = 0;
    private static boolean gameOver = false;
    private static String currentGameTip = "Get The Key To Unlock The Next Floor";

    /** Processes the substring after a new game seed, or after a load.
     * Will move the avatar or save and quit.
     * @param inputSource the input source provided
     */
    public static void processActions(InputSource inputSource, boolean keyboardInput) {
        while (inputSource.possibleNextInput()) {
            if (keyboardInput) {
                Game.render();
            }
            char c = inputSource.getNextKey();
            if (c == ':' && inputSource.getNextKey() == 'Q') {
                // save and quit
                // return out?
                save();
                if (keyboardInput) {
                    System.exit(0);
                } else {
                    return;
                }
            } else if (c == 'W' || c == 'A' || c == 'S' || c == 'D') {
                // move player
                inputString.append(c);
                WorldGeneration.moveEnemies(world);
                boolean progressedFloor = WorldGeneration.movePlayer(world, c);
                if (progressedFloor) {
                    inputString = new StringBuilder();
                    long newSeed = StdRandom.uniform(Long.MAX_VALUE);
                    world = WorldGeneration.initWorld(newSeed);
                    inputString.append("N" + newSeed + "S");
                    setWorld(world);
                    floor += 1;
                    setCurrentGameTip("Get The Key To Unlock The Next Floor");
                }
//                System.out.println(inputString);
                // if using keyboard input, we render after each move
                if (keyboardInput) {
//                    ter.renderFrame(world);
                    Game.render();
                }
            }
        }
    }

    private static TETile getMousePosition() {
        if (mouseY < HEIGHT && mouseY >= 0 && mouseX < WIDTH && mouseX >= 0) {
            return world[mouseX][mouseY];
        }
        return Tileset.NOTHING;
    }

    private static void drawSeed(String seed) {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(MAIN_MENU_DIM / 2, MAIN_MENU_DIM / 2 + 2, "Enter Seed:");
        StdDraw.text(MAIN_MENU_DIM / 2, MAIN_MENU_DIM / 2, seed);
        if (!seed.equals("")) {
            StdDraw.text(MAIN_MENU_DIM / 2, MAIN_MENU_DIM / 2 - 2, "Start Game (S)");
        }
        StdDraw.show();
    }

    public static TETile[][] handleOptionSelect(InputSource inputSource, boolean keyboardInput) {
        TETile[][] finalWorldFrame = null;
        while (true) {
            char first = inputSource.getNextKey();
            if (first == 'N') {
                inputString = new StringBuilder();
                inputString.append(first);
                long seed = processSeed(inputSource, keyboardInput);
                finalWorldFrame = WorldGeneration.initWorld(seed);
                break;
            } else if (first == 'L') {
                // get saved world and put it in finalWorldFrame
                finalWorldFrame = load();
                break;
            } else if (first == 'Q') {
                if (keyboardInput) {
                    System.exit(0);
                }
            }
        }
        return finalWorldFrame;
    }

    private static long processSeed(InputSource inputSource, boolean keyboardInput) {
        StringBuilder seed = new StringBuilder();
        if (keyboardInput) {
            drawSeed(seed.toString());
        }
        while (inputSource.possibleNextInput()) {
            char c = inputSource.getNextKey();
            if (!(Character.isDigit(c) || c == 'S')) {
                continue;
            }
            inputString.append(c);
            if (c == 'S') {
                break;
            }
            seed.append(c);
            if (keyboardInput) {
                drawSeed(seed.toString());
            }
        }
        return Long.parseLong(seed.toString());
    }

    private static void save() {
        try {
            File saveFile = new File("save_data.txt");
            FileWriter myWriter = new FileWriter(saveFile);
//            inputString.append(floor);
            inputString.append(" " + floor);
            inputString.append(" " + playerHealth);
            inputString.append(" " + enemiesDefeated);
            myWriter.write(inputString.toString());
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static TETile[][] load() {
        setWorld(WorldGeneration.blankWorld());
        In in = new In("save_data.txt");
        if (in.isEmpty()) {
            return null;
        }
        String saveString = in.readString();
        if (in.isEmpty()) {
            floor = 0;
        } else {
            floor = in.readInt();
        }
        if (in.isEmpty()) {
            playerHealth = 0;
        } else {
            playerHealth = in.readInt();
        }
        if (in.isEmpty()) {
            enemiesDefeated = 0;
        } else {
            enemiesDefeated = in.readInt();
        }
        player = new Player(new Position(0, 0), playerHealth);
        WorldGeneration.setPlayer(player);

//        floor = Character.getNumericValue(saveString.charAt(saveString.length() - 1));
//        saveString = saveString.substring(0, saveString.length() - 1);

//        return interactWithInputString(saveString);
        InputSource inputSource = new StringInputDevice(saveString);
        TETile[][] finalWorldFrame = Game.handleOptionSelect(inputSource, false);
        if (finalWorldFrame == null) {
            return null;
        }

        setWorld(finalWorldFrame);
//        render();
//        while (inputSource.possibleNextInput()) {
        Game.processActions(inputSource, false);
//        }
        return finalWorldFrame;
    }

    public static void render() {
        if (gameOver) {
            StdDraw.clear(Color.black);
            Font font = new Font("Monaco", Font.PLAIN, 80);
            StdDraw.setFont(font);
            StdDraw.text(WIDTH / 2, HEIGHT / 2 + 4, "GAME OVER");
            font = new Font("Monaco", Font.PLAIN, 30);
            StdDraw.setFont(font);
            StdDraw.text(WIDTH / 2, HEIGHT / 2 - 1, "You made it to floor " + floor);

            StdDraw.text(WIDTH / 2, HEIGHT / 2 - 3.5, "and defeated " + enemiesDefeated + " enemies.");

            StdDraw.text(WIDTH / 2, 2, "Quit (Q)");
            StdDraw.show();


            while (true) {
                if (StdDraw.hasNextKeyTyped() ) {
                    String c = String.valueOf(StdDraw.nextKeyTyped()).toUpperCase();
                    if (c.equals("Q")) {
                        System.exit(0);
                    }
                }
            }
        }

        ter.renderFrame(world);

        // HUD
        TETile mousedOverTile = getMousePosition();
//            Font font = new Font("Monaco", Font.PLAIN, 30);
//            StdDraw.setFont(font);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.textRight(WIDTH - 1, HEIGHT - 1, "Tile: " + mousedOverTile.description());

        StdDraw.textLeft(1, HEIGHT - 1, "Floor: " + floor);
        StdDraw.text(WIDTH / 2, HEIGHT - 1, currentGameTip);

        setPlayerHealth();
        StdDraw.textLeft(1, HEIGHT - 2, "Lives: " + playerHealth);

        StdDraw.textLeft(1, HEIGHT - 3, "Enemies Defeated: " + enemiesDefeated);

        StdDraw.show();
//        System.out.println(mousedOverTile.description());
    }

//    public static void renderHUD() {
//        TETile mousedOverTile = getMousePosition(world);
////            Font font = new Font("Monaco", Font.PLAIN, 30);
////            StdDraw.setFont(font);
//        StdDraw.setPenColor(Color.WHITE);
//        StdDraw.textRight(WIDTH - 1, HEIGHT - 1, mousedOverTile.description());
//        StdDraw.show();
//        StdDraw.setPenColor(Color.BLACK);
////        System.out.println(mousedOverTile.description());
//    }

    public static void drawMainMenu() {
        StdDraw.setCanvasSize(MAIN_MENU_DIM * 16, MAIN_MENU_DIM * 16);
        Font font = new Font("Monaco", Font.PLAIN, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, MAIN_MENU_DIM);
        StdDraw.setYscale(0, MAIN_MENU_DIM);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
        StdDraw.setPenColor(Color.WHITE);

        int middle = MAIN_MENU_DIM / 2;

        StdDraw.text(middle, middle, "Load Game (L)");
        StdDraw.text(middle, middle + 2, "New Game (N)");
        StdDraw.text(middle, middle - 2, "Quit (Q)");
        StdDraw.text(middle, MAIN_MENU_DIM - 10, "Grass Brawler Dungeon Crawler");

        StdDraw.show();

    }

    public static void gameOver() {
//        StdDraw.clear(Color.BLACK);
//        StdDraw.setPenColor(Color.WHITE);
//
//        int middle = MAIN_MENU_DIM / 2;
//
//        StdDraw.text(middle, middle, "GAME OVER");
////        StdDraw.text(middle, middle + 2, "New Game (N)");
////        StdDraw.text(middle, middle - 2, "");
////        StdDraw.text(middle, MAIN_MENU_DIM - 10, "CS game");
//
//        StdDraw.show();
        gameOver = true;
//        setCurrentGameTip("GAME OVER: You lost all of your health!");
    }

    public static TETile[][] getWorld() {
        return world;
    }

    public static void setWorld(TETile[][] gameWorld) {
        world = gameWorld;
    }

    public static TERenderer getRenderer() {
        return ter;
    }

    public static void setRenderer(TERenderer renderer) {
        ter = renderer;
    }

    public static void setMouseX(int x) {
        mouseX = x;
    }

    public static void setMouseY(int y) {
        mouseY = y;
    }

    public static void setCurrentGameTip(String s) {
        currentGameTip = s;
    }

    public static String getCurrentGameTip() {
        return currentGameTip;
    }

    private static void setPlayerHealth(int health) {
        playerHealth = health;
    }

    private static void setPlayerHealth() {
        playerHealth = player.getHealth();
    }

    public static void setPlayer(Player p) {
        player = p;
    }

    public static void setEnemiesDefeated(int enemies) {
        enemiesDefeated = enemies;
    }

    public static int getEnemiesDefeated() {
        return enemiesDefeated;
    }

//    public static void main(String[] args) {
//        gameOver();
//        render();
//    }
}
