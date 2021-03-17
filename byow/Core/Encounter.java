package byow.Core;

import edu.princeton.cs.introcs.StdDraw;

import java.util.Random;

public class Encounter {
    private Random rng;
//    private Player player;
    public static final int WIN = 0;
    public static final int LOSE = 1;
    public static final int REPLAY = 2;
    // not a number
    public static final int NAN = 3;
    // not a character
    public static final int NAC = 4;

    public Encounter(Random random) {
        rng = random;
//        player = p;
    }

    private int checkSameNumber() {
        int targetNum = RandomUtils.uniform(rng, 3);
        Game.setCurrentGameTip("ENCOUNTER: Guess my number { (0) (1) (2) }");
        if (Game.getRenderer() != null) {
            Game.render();
//            } else if (Game.getWorld() != null) {
//                Game.renderHUD();
            while (true) {
                if (StdDraw.hasNextKeyTyped()) {
                    int userNum = Character.getNumericValue(StdDraw.nextKeyTyped());
                    if (userNum == targetNum) {
                        return WIN;
                    } else if (!(userNum == 1 || userNum == 2 || userNum == 0)) {
                        return NAN;
                    } else {
                        return LOSE;
                    }
                }
            }
        }
        return REPLAY;
    }

    private int playRPS(String gameTip) {
        String[] choices = new String[]{"R", "P", "S"};
        String enemyChoice = choices[RandomUtils.uniform(rng, 3)];
        Game.setCurrentGameTip(gameTip);
        if (Game.getRenderer() != null) {
            Game.render();
            while (true) {
                if (StdDraw.hasNextKeyTyped()) {
                    String userChoice = String.valueOf(StdDraw.nextKeyTyped()).toUpperCase();
                    if ((userChoice.equals("R") && enemyChoice.equals("S"))
                            || (userChoice.equals("P") && enemyChoice.equals("R"))
                            || (userChoice.equals("S") && enemyChoice.equals("P"))) {
                        return WIN;
                    } else if ((userChoice.equals("R") && enemyChoice.equals("P"))
                            || (userChoice.equals("P") && enemyChoice.equals("S"))
                            || (userChoice.equals("S") && enemyChoice.equals("R"))) {
                        return LOSE;
                    } else if (userChoice.equals(enemyChoice)) {
                        return playRPS("Tie! - Choose (R) Rock, (P) Paper, or (S) Scissors Again!");
                    } else {
                        return NAC;
                    }
                }
            }
        }
        return REPLAY;
    }

    private int playBlackJack() {
        int enemyNum = 0;
        int userNum = 0;
        int waitingTime = 1500;
        if (Game.getRenderer() != null) {
            Game.setCurrentGameTip("ENCOUNTER: Blackjack - Get 21 Or A Higher Score To Win! Ready?");
            Game.render();
            StdDraw.pause(3000);
            Game.setCurrentGameTip("Score - Enemy: " + enemyNum + ", You: " + userNum + " - Press (D) to Draw and (H) to Hold");
            Game.render();
            while (userNum < 21) {
                if (StdDraw.hasNextKeyTyped()) {
                    String userChoice = String.valueOf(StdDraw.nextKeyTyped()).toUpperCase();
                    if (userChoice.equals("D")) {
                        userNum += RandomUtils.uniform(rng, 1, 11);
                        Game.setCurrentGameTip("Score - Enemy: " + enemyNum + ", You: " + userNum + " - Press (D) to Draw and (H) to Hold");
                        Game.render();
                    } else if (userChoice.equals("H")) {
                        break;
                    }
                }
            }
            while (enemyNum < 16) {
                enemyNum += RandomUtils.uniform(rng, 1,11);
                Game.setCurrentGameTip("Score - Enemy: " + enemyNum + ", You: " + userNum + " - Press (D) to Draw and (H) to Hold");
                Game.render();
                StdDraw.pause(waitingTime);
            }
            if (userNum > 21 || (enemyNum <= 21 && enemyNum > userNum)) {
                Game.setCurrentGameTip("Final Score - Enemy: " + enemyNum + ", You: " + userNum);
                Game.render();
                StdDraw.pause(waitingTime);
                return LOSE;
            } else if (enemyNum > 21 || (userNum <= 21 && enemyNum < userNum)) {
                Game.setCurrentGameTip("Final Score - Enemy: " + enemyNum + ", You: " + userNum);
                Game.render();
                StdDraw.pause(waitingTime);
                return WIN;
            } else if (enemyNum == userNum) {
                Game.setCurrentGameTip("Tie! Try Again!");
                Game.render();
                StdDraw.pause(waitingTime);
                return playBlackJack();
            }
        }
        return REPLAY;
    }

//    private void damagePlayer(int damage) {
//        player.takeDamage(damage);
//    }

    public int checkWin() {
        int game = RandomUtils.uniform(rng, 3);
        if (game == 0) {
            return checkSameNumber();
        } else if (game == 1) {
            return playBlackJack();
        } else {
            return playRPS("ENCOUNTER: Choose (R) Rock, (P) Paper, or (S) Scissors!");
        }

    }
}
