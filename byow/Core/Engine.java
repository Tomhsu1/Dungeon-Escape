package byow.Core;

import byow.InputDemo.InputSource;
import byow.InputDemo.OurKeyboardInputSource;
import byow.InputDemo.StringInputDevice;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;

public class Engine {
//    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 40;
    private static final boolean KEYBOARD_INPUT = true;
    private static final boolean STRING_INPUT = false;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        /* Notes:
         * check first char input (N, L, or :Q (Q?)) => MAIN MENU
         * if == N, get seed, make wg, after S render world
         * if == L, get save
         * if == Q, quit out ?
         *
         * use keyboardInputSource
         * use processActions on each character, draw each move
         */
        Game.drawMainMenu();
        InputSource inputSource = new OurKeyboardInputSource();
        TERenderer ter = new TERenderer();

        TETile[][] finalWorldFrame = Game.handleOptionSelect(inputSource, KEYBOARD_INPUT);
        Game.setWorld(finalWorldFrame);

//        if (finalWorldFrame == null) {
//            return;
//        }

        ter.initialize(WIDTH, HEIGHT);
        Game.setRenderer(ter);

        ter.renderFrame(finalWorldFrame);

//        while (true) {
//            Game.render();
        Game.processActions(inputSource, KEYBOARD_INPUT);
//            Game.render(finalWorldFrame, ter);
//        }

        // check wht first char is

        // process string
        // render each player move


    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.

        /* for testing */
//        TERenderer ter = new TERenderer();
//        ter.initialize(WIDTH, HEIGHT);


        String ucInput = input.toUpperCase();
        InputSource inputSource = new StringInputDevice(ucInput);
        TETile[][] finalWorldFrame = Game.handleOptionSelect(inputSource, STRING_INPUT);
        Game.setWorld(finalWorldFrame);
        if (finalWorldFrame == null) {
            return null;
        }

//        while (inputSource.possibleNextInput()) {
        Game.processActions(inputSource, STRING_INPUT);
//        }

        /* generalize about ^^ using inputSource, make processing actions a
         * function on its own so we can use it for interactWithKeyboardInput()
         */

//        TETile[][] finalWorldFrame = wg.getWorld();

        /* for testing */
//        ter.renderFrame(finalWorldFrame);

        return finalWorldFrame;
    }

//    public static void main(String[] args) {
//        Engine e = new Engine();
//        TETile[][] e1 = e.interactWithInputString("n7193300625454684331saaawasdaawdwsd");
//        TETile[][] e2 = e.interactWithInputString("n7193300625454684331saaawasdaawd:q");
//        e2 = e.interactWithInputString("lwsd");
//        TERenderer ter = new TERenderer();
//        ter.initialize(WIDTH, HEIGHT);
////        ter.renderFrame(e1);
////        ter.renderFrame(e2);
//        System.out.println(Arrays.deepEquals(e1, e2));
//    }


}
