package MasterMind.StateManagers;

import java.awt.Color;

import MasterMind.ConnectionHandler;
import MasterMind.MasterMind;
import game.drawing.Draw;

public class PracticeConnecting {

    public static void handlePracticeConnecting(boolean isNewState) {
        if (isNewState) {
            // create local server
            ConnectionHandler.startServerIfNull();
            MasterMind.clientReadyToJoin = true;
        }

        // connect bot to local server
        ConnectionHandler.startBotIfNull("127.0.0.1");

        // connect client to local server
        if (ConnectionHandler.botConnected) {
            ConnectionHandler.startClientIfNull("127.0.0.1");
        }

        if (ConnectionHandler.serverReady && ConnectionHandler.clientConnected && ConnectionHandler.botConnected) {
            MasterMind.state = MasterMind.GameState.SELECTING_ROUNDS;
        }
    }

    public static void drawPracticeConnecting() {
        // background
        Draw.setColor(new Color(26, 26, 26));
        Draw.rect(0, 0, 2000, 2000);
        if (!ConnectionHandler.serverReady) {
            Draw.setColor(Color.WHITE);
            Draw.setFontSize(2);
            Draw.text("starting server...", 200, 400);
        } else {
            if (!ConnectionHandler.clientConnected) {
                Draw.setColor(Color.WHITE);
                Draw.setFontSize(2);
                Draw.text("connecting to server...", 100, 400);
            }
        }
    }

}
