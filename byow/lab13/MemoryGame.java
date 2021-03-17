package byow.lab13;

import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;

public class MemoryGame {
    private int width;
    private int height;
    private int round;
    private Random rand;
    private boolean gameOver;
    private boolean playerTurn;
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final String[] ENCOURAGEMENT = {"You can do this!", "I believe in you!",
                                                   "You got this!", "You're a star!", "Go Bears!",
                                                   "Too easy for you!", "Wow, so impressive!"};

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please enter a seed");
            return;
        }

        long seed = Long.parseLong(args[0]);
        Random rng = new Random(seed);
        MemoryGame game = new MemoryGame(40, 40, seed);
        game.startGame();
    }

    public MemoryGame(int width, int height, long seed) {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();

        rand = new Random(seed);
        round = 1;
        gameOver = false;
        playerTurn = false;
    }

    public String generateRandomString(int n) {
        String randoString = "";
        for (int i = 0; i < n; ++i) {
            int index = rand.nextInt(26);
            randoString += CHARACTERS[index];
        }
        return randoString;
    }

    public void drawFrame(String s) {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(this.width / 2, this.height / 2, s);
        if (playerTurn) {
            StdDraw.text(this.width / 2, this.height - 1, "Type!");
        } else {
            StdDraw.text(this.width / 2, this.height - 1, "Watch!");
        }
        StdDraw.textLeft(1, this.height - 1, "Round: " + round);

        Font smallerFont = new Font("Monaco", Font.BOLD, 15);
        StdDraw.setFont(smallerFont);
        StdDraw.textRight(this.width - 1, this.height - 1, ENCOURAGEMENT[rand.nextInt(7)]);
        StdDraw.line(0, this.height - 2, this.width, this.height - 2);
        StdDraw.show();
    }

    public void flashSequence(String letters) {
        for (int i = 0; i < letters.length(); ++i) {
            char c = letters.charAt(i);
            drawFrame(String.valueOf(c));
            StdDraw.pause(1000);
            StdDraw.clear(Color.BLACK);
            StdDraw.pause(500);
        }
        playerTurn = true;
        drawFrame("");
    }

    public String solicitNCharsInput(int n) {
        int numKeys = 0;
        String userString = "";
        while (numKeys < n) {
            if (StdDraw.hasNextKeyTyped()) {
                numKeys += 1;
                userString += StdDraw.nextKeyTyped();
                drawFrame(userString);
            }
            if (numKeys == n) {
                StdDraw.pause(1000);
            }
        }
        playerTurn = false;
        return userString;
    }

    public void startGame() {
        while (!gameOver) {
            String winningString = generateRandomString(round);
            drawFrame("Round: " + round);
            StdDraw.pause(2000);
            flashSequence(winningString);
            String input = solicitNCharsInput(round);
            if (!winningString.equals(input)) {
                gameOver = true;
                break;
            }
            round += 1;
        }
        drawFrame("Game Over! You made it to round: " + round);
    }

}
