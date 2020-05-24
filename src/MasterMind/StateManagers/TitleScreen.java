package MasterMind.StateManagers;

import MasterMind.Button;
import MasterMind.MasterMind;

public class TitleScreen {

    static Button practiceButton = new Button(300, 350, 200, 50, "Practice", TitleScreen::practicePress);
    static Button playButton = new Button(300, 450, 200, 50, "Play", TitleScreen::playPress);

    public static void handleTitleScreen(boolean isNewState) {
        practiceButton.update();
        playButton.update();
    }

    public static void drawTitleScreen() {
        practiceButton.draw();
        playButton.draw();
    }

    // button callbacks
    public static void practicePress() {
        MasterMind.isPractice = true;
        MasterMind.state = MasterMind.GameState.PRACTICE_CONNECTING;
    }

    public static void playPress() {
        MasterMind.state = MasterMind.GameState.CONNECTING;
    }
}
