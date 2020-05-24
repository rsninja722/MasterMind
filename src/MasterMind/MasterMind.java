package MasterMind;

import java.util.ArrayList;

import MasterMind.StateManagers.Playing;
import MasterMind.StateManagers.PracticeConnecting;
import MasterMind.StateManagers.SelectRounds;
import MasterMind.StateManagers.TitleScreen;
import game.*;
import game.drawing.Draw;

/*
non essentials that would be nice to have:
screen shake
particles
explosions?
gold system
shop with peg hats!
big bright buttons
 */

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

    static public boolean clientReceivedMessage(String msg) {
        for(int i=0;i<clientMessagesIn.size();i++) {
            if(clientMessagesIn.get(i).equals(msg)) {
                clientMessagesIn.remove(i);
                return true;
            }
        }
        return false;
    }

    static public boolean botReceivedMessage(String msg) {
        for(int i=0;i<botMessagesIn.size();i++) {
            if(botMessagesIn.get(i).equals(msg)) {
                botMessagesIn.remove(i);
                return true;
            }
        }
        return false;
    }

    public MasterMind() {
        super(600, 750, 60, 60);

        state = GameState.TITLE_SCREEN;

        // prevent game from being resized
        Draw.frame.setResizable(false);
        Draw.allowFullScreen = false;

        LoopManager.startLoops(this);
    }

    public static void main(String[] args) {
        frameTitle = "Mastermind";
        new MasterMind();
    }

    @Override
    public void update() {
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
                break;
            case PLAYING:
                Playing.handlePlaying(isNewState);
                break;
            case RESULTS:
                break;
        }
    }

    @Override
    public void draw() {
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
                break;
            case PLAYING:
                Playing.drawPlaying();
                break;
            case RESULTS:
                break;
        }
    }

    @Override
    public void absoluteDraw() {
        switch (state) {
            case TITLE_SCREEN:
                break;
            case SELECTING_ROUNDS:
                break;
            case PRACTICE_CONNECTING:
                break;
            case CONNECTING:
                break;
            case PLAYING:
                break;
            case RESULTS:
                break;
        }
    }
}