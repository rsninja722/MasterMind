package MasterMind.StateManagers;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JOptionPane;

import MasterMind.MasterMind;
import MasterMind.server.ServerHandler;
import MasterMind.Button;
import MasterMind.ConnectionHandler;
import game.drawing.Draw;

public class Connecting {

    static Button hostButton = new Button(300, 350, 200, 50, "Host", Connecting::host);
    static Button joinButton = new Button(300, 450, 200, 50, "Join", Connecting::join);

    static boolean hosting;
    static boolean connecting = false;
    static String ip;
    static String publicIp;

    public static void handleConnecting(boolean isNewState) {
        // tey to get public ip
        if (isNewState) {
            // https://stackoverflow.com/a/2939223
            try {
                URL pubIp = new URL("http://checkip.amazonaws.com");
                BufferedReader in = new BufferedReader(new InputStreamReader(pubIp.openStream()));

                publicIp = in.readLine();
                System.out.println(publicIp);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // let user pick host or join
        if (!connecting) {
            hostButton.update();
            joinButton.update();
            // start client/server
        } else {
            if (hosting) {
                ConnectionHandler.startServerIfNull();
                ConnectionHandler.startClientIfNull("127.0.0.1");
                MasterMind.clientReadyToJoin = true;
            } else {
                if (ip != null) {
                    MasterMind.clientReadyToJoin = true;
                    ConnectionHandler.startClientIfNull(ip);
                }
            }
        }

        // determine when everything is ready and connected
        if (hosting) {
            if (ConnectionHandler.serverReady && ConnectionHandler.clientConnected && ServerHandler.playerCount > 1) {
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

        // draw buttons
        if (!connecting) {
            hostButton.draw();
            joinButton.draw();
            // connection information
        } else {

            if (hosting) {
                Draw.setColor(Color.WHITE);
                Draw.setFontSize(2);
                Draw.text("1. enable port forwarding on port 9876", 20, 100);
                Draw.text("2. have a friend join", 20, 130);
                Draw.text((publicIp != null ? "by entering this ip address: " + publicIp : "using your public ip address"), 40, 150);
                if (!ConnectionHandler.serverReady) {
                    Draw.text("starting server...", 200, 400);
                } else if (!ConnectionHandler.clientConnected) {
                    Draw.text("connecting to server...", 100, 400);
                } else {
                    Draw.text("waiting for another player...", 100, 400);
                }

            } else {
                if (!ConnectionHandler.clientConnected) {
                    Draw.setColor(Color.WHITE);
                    Draw.setFontSize(2);
                    Draw.text("connecting to server...", 100, 400);
                }
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
