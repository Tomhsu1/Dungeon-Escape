package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WorldGeneration {
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
     */
    private static final int WIDTH = 80;
    private static final int HEIGHT = 40;
    private static final int MAX_ROOMS = 16;
    private static final int MIN_ROOMS = 8;
    private static List<Room> rooms;
    private static List<Hallway> hallways;
    private static List<Enemy> enemies;
    private static Player player;
    private static Random rng;
    private static int lockedDoorRoomIndex;

    private static void drawHallwayWalls(TETile[][] world) {
        for (Hallway h : hallways) {
            h.getWallPositions(world);
        }
        for (Hallway h : hallways) {
            h.drawWalls(world, rng);
        }
    }

    private static void drawEnemies(TETile[][] world) {
        for (Enemy e : enemies) {
            e.draw(world);
        }
    }

    private static void initPlayer(TETile[][] world) {
        Room firstRoom = rooms.get(0);
        // highlighting the first room's floor tiles
//        for (Position p: firstRoom.floorPositions) {
//            world[p.getX()][p.getY()] = Tileset.FLOWER;
//        }
        if (player == null) {
            player = new Player(firstRoom.getMiddle(), 5);
        } else {
//            int remainingHealth = player.getHealth();
//            player = new Player(firstRoom.getMiddle(), remainingHealth);
            player.setCurrPos(firstRoom.getMiddle());
            player.removeKey();
//            player.setHealth(remainingHealth);
        }
        player.draw(world);
        Game.setPlayer(player);
    }

    private static void initEnemies(TETile[][] world) {
        enemies = new ArrayList<>();
        int numEnemies = RandomUtils.uniform(rng, 1, rooms.size());
        while (numEnemies > 0) {
            int randomRoomIndex = RandomUtils.uniform(rng, 1, rooms.size());
            Room randomRoom = rooms.get(randomRoomIndex);
            if (!randomRoom.getStartsWithEnemy()) {
                randomRoom.setStartsWithEnemyTrue();
                Position enemyPos = randomRoom.getMiddle();
                while (world[enemyPos.getX()][enemyPos.getY()] != Tileset.FLOOR) {
                    List<Position> floorTiles = randomRoom.getFloorPositions();
                    int randomFloorIndex = RandomUtils.uniform(rng, floorTiles.size());
                    enemyPos = floorTiles.get(randomFloorIndex);
                }
                Enemy e = new Enemy(enemyPos);
                enemies.add(e);
                numEnemies -= 1;
            }
        }
    }

    private static void initLockedDoor(TETile[][] world) {
        int randomRoomIndex = RandomUtils.uniform(rng, 1, rooms.size());
        lockedDoorRoomIndex = randomRoomIndex;
        List<Position> floorTiles = rooms.get(randomRoomIndex).getFloorPositions();
        int randomFloorIndex = RandomUtils.uniform(rng, floorTiles.size());
        Position randomFloor = floorTiles.get(randomFloorIndex);
        world[randomFloor.getX()][randomFloor.getY()] = Tileset.STAIRS;
    }

    private static void initKey(TETile[][] world) {
        int randomRoomIndex = RandomUtils.uniform(rng, 1, rooms.size());
        while (randomRoomIndex == lockedDoorRoomIndex) {
            randomRoomIndex = RandomUtils.uniform(rng, 1, rooms.size());
        }
        List<Position> floorTiles = rooms.get(randomRoomIndex).getFloorPositions();
        int randomFloorIndex = RandomUtils.uniform(rng, floorTiles.size());
        Position randomFloor = floorTiles.get(randomFloorIndex);
        world[randomFloor.getX()][randomFloor.getY()] = Tileset.KEY;
    }

    private static void initRooms(TETile[][] world) {
        int numRooms = RandomUtils.uniform(rng, MIN_ROOMS, MAX_ROOMS);
        rooms = new ArrayList<>();
        int n = 0;

        // randomly generate rooms
        while (n < numRooms) {
            int x = RandomUtils.uniform(rng, 0, WIDTH - Room.MAX_SIZE);
            int y = RandomUtils.uniform(rng, Room.MAX_SIZE, HEIGHT - 3);
            Room r = new Room(rng, x, y);
            if (!r.intersectsOtherRoom(world)) {
                rooms.add(r);
                r.draw(world, rng);
                ++n;
            }
        }
    }

    private static void initHallways(TETile[][] world) {
        // randomly generate hallways
        hallways = new ArrayList<>();
        for (int r = 1; r < rooms.size(); ++r) {
            // For all remaining rooms get the center of the room and the previous room
            int currX = rooms.get(r).getMiddle().getX();
            int prevX = rooms.get(r - 1).getMiddle().getX();
            int currY = rooms.get(r).getMiddle().getY();
            int prevY = rooms.get(r - 1).getMiddle().getY();
            Hallway horizontal;
            Hallway vertical;

            // Give a 50/50 chance of which 'L' shaped connecting hallway to tunnel out
            if (RandomUtils.uniform(rng, 1) == 0) {
                horizontal = new Hallway(world, currX, prevX, false, currY);
                vertical = new Hallway(world, currY, prevY, true, prevX);
            } else {
                vertical = new Hallway(world, currY, prevY, true, currX);
                horizontal = new Hallway(world, currX, prevX, false, prevY);
            }

            hallways.add(horizontal);
            hallways.add(vertical);
        }
    }

    public static void initEntities(TETile[][] world) {
        initLockedDoor(world);
        initKey(world);
        initPlayer(world);
        initEnemies(world);
    }

    public static TETile[][] blankWorld() {
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        return world;
    }

    public static TETile[][] generateWorld() {
        // initialize tiles
        TETile[][] world = blankWorld();

        initRooms(world);
        initHallways(world);
        drawHallwayWalls(world);

        initEntities(world);
        drawEnemies(world);
        return world;
    }

    public static TETile[][] initWorld(long seed) {
        rng = new Random(seed);
//        // initialize the tile rendering engine with a window of size WIDTH x HEIGHT
//        TERenderer ter = new TERenderer();
//        ter.initialize(WIDTH, HEIGHT);
//
        TETile[][] world = generateWorld();

//        initPlayer(world);
//        // draws the world to the screen
//        ter.renderFrame(world);
        return world;
    }

//    public WorldGeneration(long seed) {
//        rng = new Random(seed);
////        // initialize the tile rendering engine with a window of size WIDTH x HEIGHT
////        TERenderer ter = new TERenderer();
////        ter.initialize(WIDTH, HEIGHT);
//
//        // initialize tiles
//        world = new TETile[WIDTH][HEIGHT];
//        for (int x = 0; x < WIDTH; x += 1) {
//            for (int y = 0; y < HEIGHT; y += 1) {
//                world[x][y] = Tileset.NOTHING;
//            }
//        }
//        generateWorld();
//        initPlayer();
////        // draws the world to the screen
////        ter.renderFrame(world);
//    }

//    public TETile[][] getWorld() {
//        return world;
//    }

    public static boolean movePlayer(TETile[][] world, char direction) {
        boolean progressedFloor = player.move(world, direction, rng);
        player.draw(world);
        return progressedFloor;
    }

    public static void moveEnemies(TETile[][] world) {
        for (Enemy e: enemies) {
            int direction = RandomUtils.uniform(rng, 4);
            int randomMoveChance = RandomUtils.uniform(rng, 2);
            if (randomMoveChance == 0) {
                e.move(world, direction);
                e.draw(world);
            }
        }
    }

    public static void removeEnemy(int x, int y) {
        for (Enemy e: enemies) {
            Position eCurrPos = e.getCurrPos();
            if (eCurrPos.getX() == x && eCurrPos.getY() == y) {
                enemies.remove(e);
                return;
            }
        }
    }

    public static void setPlayer(Player p) {
        player = p;
    }
//    public static void main(String[] args) {
////        Random rng = new Random(6235);
////        rng = new Random(59877773);
//        // initialize the tile rendering engine with a window of size WIDTH x HEIGHT
//        TERenderer ter = new TERenderer();
//        ter.initialize(WIDTH, HEIGHT);
//
//        // initialize tiles
////        TETile[][] world = new TETile[WIDTH][HEIGHT];
////        for (int x = 0; x < WIDTH; x += 1) {
////            for (int y = 0; y < HEIGHT; y += 1) {
////                world[x][y] = Tileset.NOTHING;
////            }
////        }
//        long seed = 5197880843569031643L;
//        TETile[][] world = initWorld(seed);
////        WorldGeneration wg = new WorldGeneration(seed);
//
//        // testing movement of avatar
//        for (int i = 0; i < 4; ++i) {
//            player.move(world, 's', rng);
//            player.draw(world);
//        }
//        // shouldn't move into the wall
//        player.move(world, 'a', rng);
//        player.draw(world);
//        player.move(world, 'd', rng);
//        player.draw(world);
//
//        // draws the world to the screen
//        ter.renderFrame(world);
//    }
}
