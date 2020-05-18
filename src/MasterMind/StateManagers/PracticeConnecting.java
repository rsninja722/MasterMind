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
            MasterMind.clientReadyToJoin = true;
        }

        // connect bot to local server
        ConnectionHandler.startBotIfNull("127.0.0.1");

        // connect client to local server
        if(ConnectionHandler.botConnected) {
            ConnectionHandler.startClientIfNull("127.0.0.1");
        }

        if (ConnectionHandler.serverReady && ConnectionHandler.clientConnected && ConnectionHandler.botConnected) {
            MasterMind.isPractice = true;
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
