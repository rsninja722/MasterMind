package MasterMind.StateManagers;

import java.awt.Color;

import MasterMind.MasterMind;
import game.drawing.Draw;

public class Results {

    public static void handleResults(boolean isNewState) {

    }

    public static void drawResults() {
        // background
        Draw.setColor(new Color(26, 26, 26));
        Draw.rect(0, 0, 2000, 2000);

        Draw.setFontSize(6);
        if(MasterMind.won) {
            Draw.setColor(Color.YELLOW);
            Draw.text("You win!", 200, 300);
        } else {
            Draw.setColor(Color.RED);
            Draw.text("You Lose!", 200, 300);
        }
    }

}
