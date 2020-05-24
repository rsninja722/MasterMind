package MasterMind.StateManagers;

import java.awt.Color;

import MasterMind.Button;
import MasterMind.MasterMind;
import MasterMind.MasterMind.GameState;
import game.drawing.Draw;

public class SelectRounds {


    static Button decreaseButton = new Button(200, 300, 35, 35, "-", SelectRounds::decreaseRoundCount);
    static Button increaseButton = new Button(400, 300, 35, 35, "+", SelectRounds::increaseRoundCount);
    static Button readyButton = new Button(300, 500, 100, 50, "ready", SelectRounds::ready);
    
    public static void handleSelectRounds(boolean isNewState) {
        increaseButton.update();
        decreaseButton.update();
        readyButton.update();

        for (int i = 0; i < MasterMind.clientMessagesIn.size(); i++) {
            if (MasterMind.clientMessagesIn.get(i).startsWith("[AP]RoundsSetTo")) {
                MasterMind.rounds = Integer.parseInt(MasterMind.clientMessagesIn.get(i).substring(15));
                MasterMind.clientMessagesIn.remove(i);
            }
        }

        if(MasterMind.clientReceivedMessage("[AP]Game Starting")) {
            MasterMind.state = GameState.PLAYING;
        }
        
        
    }

    public static void ready() {
        if(MasterMind.isPractice) {
            MasterMind.botMessagesOut.add("[AP]ready");

        }
        
        MasterMind.clientMessagesOut.add("[AP]ready");
    }

    public static void drawSelectRounds() {

        // background
        Draw.setColor(new Color(26, 26, 26));
        Draw.rect(0, 0, 2000, 2000);

        Draw.setColor(Color.WHITE);
        Draw.setFontSize(4);
        Draw.text("select amount", 150, 100);
        Draw.text("of rounds", 150, 150);
        Draw.text("to play", 150, 200);

        Draw.setFontSize(3);
        Draw.text("rounds: " + MasterMind.rounds, 222, 310);

        increaseButton.draw();
        decreaseButton.draw();
        readyButton.draw();

    }

    public static void increaseRoundCount() {
        MasterMind.clientMessagesOut.add("[AP]rounds" + (MasterMind.rounds + 1));
    }

    public static void decreaseRoundCount() {
        if(MasterMind.rounds > 1) {
            MasterMind.clientMessagesOut.add("[AP]rounds" + (MasterMind.rounds - 1));
        }
    }

}