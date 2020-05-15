package MasterMind.StateManagers;

import java.awt.Color;

import MasterMind.ConnectionHandler;
import MasterMind.MasterMind;
import game.drawing.Draw;

public class PracticeConnecting {

    public static void handlePracticeConnecting(boolean isNewState) {
        if (isNewState) {
            // create local server
            ConnectionHandler.startServer();

        }

        // connect client to local server
        ConnectionHandler.startClientIfNull("127.0.0.1");

        if (ConnectionHandler.serverReady && ConnectionHandler.clientConnected) {
            MasterMind.state = MasterMind.GameState.PLAYING;
        }
    }

    public static void drawPracticeConnecting() {
        if (!ConnectionHandler.serverReady) {
            Draw.setColor(Color.BLACK);
            Draw.setFontSize(2);
            Draw.text("starting server...", 200, 400);
        } else {
            if (!ConnectionHandler.clientConnected) {
                Draw.setColor(Color.BLACK);
                Draw.setFontSize(2);
                Draw.text("connecting to server...", 100, 400);
            }
        }
    }

}
