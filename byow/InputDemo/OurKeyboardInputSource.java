package byow.InputDemo;

import byow.Core.Game;
import edu.princeton.cs.introcs.StdDraw;

/** @source Borrowed from Josh Hug's KeyboardInputSource. */
public class OurKeyboardInputSource implements InputSource {
    @Override
    public char getNextKey() {
        while (true) {
//            Game.withoutRenderFrame();
//            System.out.println(StdDraw.mouseX());
            int mouseX = (int) StdDraw.mouseX();
            int mouseY = (int) StdDraw.mouseY();
            Game.setMouseX(mouseX);
            Game.setMouseY(mouseY);
            if (Game.getRenderer() != null) {
                Game.render();
//            } else if (Game.getWorld() != null) {
//                Game.renderHUD();
            }
            if (StdDraw.hasNextKeyTyped()) {
                return Character.toUpperCase(StdDraw.nextKeyTyped());
            }
        }
    }

    @Override
    public boolean possibleNextInput() {
        return true;
    }
}
