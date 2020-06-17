package MasterMind;

/*
James N
2020.06.17
MasterMind
The server code was written in collaboration with will, clients were made separately 
*/

import java.awt.Color;
import java.util.ArrayList;

import MasterMind.StateManagers.Connecting;
import MasterMind.StateManagers.Playing;
import MasterMind.StateManagers.PracticeConnecting;
import MasterMind.StateManagers.Results;
import MasterMind.StateManagers.SelectRounds;
import MasterMind.StateManagers.TitleScreen;
import game.*;
import game.audio.Sounds;
import game.drawing.Camera;
import game.drawing.Draw;

public class MasterMind extends GameJava {

    public enum GameState {
        TITLE_SCREEN, SELECTING_ROUNDS, PRACTICE_CONNECTING, CONNECTING, PLAYING, RESULTS
    }

    static public GameState state;
    static GameState lastState;

    static public boolean gameRunning = true;

    static public boolean isPractice = false;

    static public ArrayList<String> clientMessagesIn = new ArrayList<String>();
    static public ArrayList<String> clientMessagesOut = new ArrayList<String>();

    static public boolean clientReadyToJoin = false;

    static public ArrayList<String> botMessagesIn = new ArrayList<String>();
    static public ArrayList<String> botMessagesOut = new ArrayList<String>();

    static public int rounds = 1;

    static public int updateDelay = 0;

    static public boolean won;

    static public int cameraShakeX = 0;
    static public int cameraShakeY = 0;

    static public boolean clientReceivedMessage(String msg) {
        for (int i = 0; i < clientMessagesIn.size(); i++) {
            if (clientMessagesIn.get(i).equals(msg)) {
                clientMessagesIn.remove(i);
                return true;
            }
        }
        return false;
    }

    static public boolean botReceivedMessage(String msg) {
        for (int i = 0; i < botMessagesIn.size(); i++) {
            if (botMessagesIn.get(i).equals(msg)) {
                botMessagesIn.remove(i);
                return true;
            }
        }
        return false;
    }

    public MasterMind() {
        super(900, 750, 60, 60);

        state = GameState.TITLE_SCREEN;

        // prevent game from being resized
        Draw.frame.setResizable(false);
        Draw.allowFullScreen = false;

        Sounds.ajustGain("theme", 0.85f);
        Sounds.loop("theme");

        LoopManager.startLoops(this);
    }

    public static void main(String[] args) {
        frameTitle = "Mastermind";
        new MasterMind();
    }

    @Override
    public void update() {
        if (updateDelay == 0) {
            // determine if new state
            boolean isNewState = false;
            if (state != lastState) {
                isNewState = true;
            }
            lastState = state;

            Utils.putInDebugMenu("client messages", clientMessagesIn.toString());
            Utils.putInDebugMenu("bot    messages", botMessagesIn.toString());

            switch (state) {
                case TITLE_SCREEN:
                    TitleScreen.handleTitleScreen(isNewState);
                    break;
                case SELECTING_ROUNDS:
                    SelectRounds.handleSelectRounds(isNewState);
                    break;
                case PRACTICE_CONNECTING:
                    PracticeConnecting.handlePracticeConnecting(isNewState);
                    break;
                case CONNECTING:
                    Connecting.handleConnecting(isNewState);
                    break;
                case PLAYING:
                    Playing.handlePlaying(isNewState);
                    break;
                case RESULTS:
                    Results.handleResults(isNewState);
                    break;
            }
        } else {
            updateDelay--;
        }
    }

    @Override
    public void draw() {
        Camera.x = cameraShakeX;
        Camera.y = cameraShakeY;

        cameraShakeX -= cameraShakeX == 0 ? 0 : (cameraShakeX > 0 ? 1 : -1);
        cameraShakeY -= cameraShakeY == 0 ? 0 : (cameraShakeY > 0 ? 1 : -1);

        switch (state) {
            case TITLE_SCREEN:
                TitleScreen.drawTitleScreen();
                break;
            case SELECTING_ROUNDS:
                SelectRounds.drawSelectRounds();
                break;
            case PRACTICE_CONNECTING:
                PracticeConnecting.drawPracticeConnecting();
                break;
            case CONNECTING:
                Connecting.drawConnecting();
                break;
            case PLAYING:
                Playing.drawPlaying();
                break;
            case RESULTS:
                Results.drawResults();
                break;
        }
    }

    @Override
    public void absoluteDraw() {
        // divider for chat
        Draw.setColor(new Color(36, 36, 36));
        Draw.rect(600, 375, 3, 750);
    }
}