package MasterMind.StateManagers;

import java.awt.Color;

import javax.swing.JOptionPane;

import MasterMind.MasterMind;
import MasterMind.Button;
import MasterMind.ConnectionHandler;
import game.drawing.Draw;

public class Connecting {

    static Button hostButton = new Button(300, 350, 200, 50, "Host", Connecting::host);
    static Button joinButton = new Button(300, 450, 200, 50, "Join", Connecting::join);

    static boolean hosting;
    static boolean connecting = false;
    static String ip;

    public static void handleConnecting(boolean isNewState) {
        if(!connecting) {
            hostButton.update();
            joinButton.update();
        } else {
            if(hosting) {
                ConnectionHandler.startServerIfNull();
                ConnectionHandler.startClientIfNull("127.0.0.1");
                MasterMind.clientReadyToJoin = true;
            } else {
                if(ip != null) {
                    MasterMind.clientReadyToJoin = true;
                    ConnectionHandler.startClientIfNull(ip);
                }
            }
        }

        if(hosting) {
            if (ConnectionHandler.serverReady && ConnectionHandler.clientConnected) {
                MasterMind.state = MasterMind.GameState.SELECTING_ROUNDS;
            }
        } else {
            if (ConnectionHandler.clientConnected) {
                MasterMind.state = MasterMind.GameState.SELECTING_ROUNDS;
            }
        }
    }
    
    public static void drawConnecting() {
        // background
        Draw.setColor(new Color(26, 26, 26));
        Draw.rect(0, 0, 2000, 2000);
        
        if(!connecting) {
            hostButton.draw();
            joinButton.draw();
        }

        if(hosting) {
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
        } else {
            if (!ConnectionHandler.clientConnected) {
                Draw.setColor(Color.WHITE);
                Draw.setFontSize(2);
                Draw.text("connecting to server...", 100, 400);
            }
        }
    }


    public static void host() {
        connecting = true;
        hosting = true;
    }
    
    public static void join() {
        connecting = true;
        hosting = false;
        ip = JOptionPane.showInputDialog("Enter host's Ip");
    }
}
