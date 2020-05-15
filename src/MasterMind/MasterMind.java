package MasterMind;

import MasterMind.StateManagers.PracticeConnecting;
import MasterMind.StateManagers.TitleScreen;
import MasterMind.gameComponents.CodePeg;
import game.*;

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
        TITLE_SCREEN, PRACTICECONNECTING, CONNECTING, PLAYING, RESULTS
    }

    static public GameState state;
    static GameState lastState;

    static CodePeg peg;

    public MasterMind() {
        super(600, 800, 60, 60);

        state = GameState.TITLE_SCREEN;

        peg = new CodePeg(100, 100, 4);

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

        switch (state) {
            case TITLE_SCREEN:
            peg.update();
                TitleScreen.handleTitleScreen(isNewState);
                break;
            case PRACTICECONNECTING:
                PracticeConnecting.handlePracticeConnecting(isNewState);
                break;
            case CONNECTING:
                break;
            case PLAYING:
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
                peg.draw();
                break;
            case PRACTICECONNECTING:
                PracticeConnecting.drawPracticeConnecting();
                break;
            case CONNECTING:
                break;
            case PLAYING:
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
            case PRACTICECONNECTING:
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