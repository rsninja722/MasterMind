package MasterMind.StateManagers;

import java.awt.Color;
import java.util.ArrayList;

import MasterMind.Button;
import MasterMind.MasterMind;
import MasterMind.gameComponents.Hole;
import MasterMind.gameComponents.Peg;
import MasterMind.gameComponents.Hole.HoleType;
import game.Input;
import game.Utils;
import game.drawing.Draw;

public class Playing {

    static Peg[] bottomPegs = new Peg[6];
    static Peg[] hintPegs = new Peg[2];

    static ArrayList<Peg> pegs = new ArrayList<Peg>();

    static boolean waiting = true;

    static int turn = 1;

    static boolean isBreaker = false;

    static boolean showConfirm = false;
    static Button confirmButton = new Button(500, 325, 150, 50, "Confirm", Playing::confirm);

    public static void handlePlaying(boolean isNewState) {

        if (isNewState) {
            // generate board holes
            Hole.generateHoles();

            // generate bottom pegs
            for (int i = 0; i < 6; i++) {
                bottomPegs[i] = new Peg(155 + (i * 55), 650, i + 1, Peg.PegType.CODE);
            }

            // generate hint pegs
            for (int i = 0; i < 2; i++) {
                hintPegs[i] = new Peg(30 + (i * 30), 650, i + 1, Peg.PegType.HINT);
            }
        }

        // determine what the client is
        if (MasterMind.clientReceivedMessage("[WC]YouAreCodeBreaker")) {
            isBreaker = true;
            waiting = false;
        }
        if (MasterMind.clientReceivedMessage("[WC]SendCodePlease")) {
            isBreaker = false;
            waiting = false;
        }

        // when ready to send guess
        if (MasterMind.clientReceivedMessage("[WA]WaitingForGuess")) {
            waiting = false;
        }

        // when ready to send guess
        for (int i = 0; i < MasterMind.clientMessagesIn.size(); i++) {
            if (MasterMind.clientMessagesIn.get(i).charAt(0) == 'H') {
                String hint = MasterMind.clientMessagesIn.get(i);
                MasterMind.clientMessagesIn.remove(i);

                for (int j = 1; j < 5; j++) {
                    int pegColor = Integer.parseInt(hint.substring(j, j + 1));
                    if (pegColor != 0) {
                        Hole.hintHoles[10 - turn][j - 1].pegColor = pegColor;
                    }
                }
            }
        }

        // make bot make code
        if (MasterMind.isPractice) {
            if (MasterMind.botReceivedMessage("[WC]SendCodePlease")) {
                MasterMind.botMessagesOut
                        .add("C" + Utils.rand(1, 6) + Utils.rand(1, 6) + Utils.rand(1, 6) + Utils.rand(1, 6));
            }
            if (MasterMind.botReceivedMessage("[WG]SendAcknowledgementPlease")) {
                MasterMind.botMessagesOut.add("[WA]acknowledgement");
            }
        }

        if (Input.mouseClick(0)) {
            // grab new code pegs
            for (int i = 0; i < bottomPegs.length; i++) {
                if (bottomPegs[i].touchingMouse()) {
                    pegs.add(new Peg(155 + (i * 55), 650, i + 1, Peg.PegType.CODE));
                }
            }

            // grab new hint pegs
            for (int i = 0; i < hintPegs.length; i++) {
                if (hintPegs[i].touchingMouse()) {
                    pegs.add(new Peg(30 + (i * 30), 650, i + 1, Peg.PegType.HINT));
                }
            }

            if (isBreaker) {
                // grab pegs from code holes
                for (int y = 0; y < Hole.codeHoles.length; y++) {
                    for (int x = 0; x < Hole.codeHoles[y].length; x++) {
                        Hole h = Hole.codeHoles[y][x];
                        // if there is a peg
                        if (h.pegColor != -1) {
                            // if mouse is over a hole
                            if (h.mouseHovering()) {
                                // make a peg at the hole
                                pegs.add(new Peg(h.position.x, h.position.y - 10, h.pegColor,
                                        h.type == HoleType.CODE ? Peg.PegType.CODE : Peg.PegType.HINT));
                                pegs.get(pegs.size() - 1).grabbed = true;
                                Peg.globalGrabbed = true;
                                // remove peg
                                h.pegColor = -1;
                            }
                        }
                    }
                }
            } else {
                // grab pegs from hint holes
                for (int y = 0; y < Hole.hintHoles.length; y++) {
                    for (int x = 0; x < Hole.hintHoles[y].length; x++) {
                        Hole h = Hole.hintHoles[y][x];
                        // if there is a peg
                        if (h.pegColor != -1) {
                            // if mouse is over a hole
                            if (h.mouseHovering()) {
                                // make a peg at the hole
                                pegs.add(new Peg(h.position.x, h.position.y - 10, h.pegColor,
                                        h.type == HoleType.CODE ? Peg.PegType.CODE : Peg.PegType.HINT));
                                pegs.get(pegs.size() - 1).grabbed = true;
                                Peg.globalGrabbed = true;
                                // remove peg
                                h.pegColor = -1;
                            }
                        }
                    }
                }
            }
        }

        // update pegs
        for (int i = 0; i < pegs.size(); i++) {
            if (pegs.get(i).update()) {
                pegs.remove(i);
            }
        }

        if (isBreaker && !waiting && turn < 10) {
            for (int i = 0; i < 4; i++) {
                if (Hole.codeHoles[9 - turn][i].pegColor == -1) {
                    showConfirm = false;
                    break;
                } else {
                    showConfirm = true;
                }
            }
        }

        // confirm button
        if (showConfirm) {
            confirmButton.update();
        }
    }

    static void confirm() {
        if (isBreaker) {
            String code = "C";
            for (int i = 0; i < 4; i++) {
                code += Hole.codeHoles[9 - turn][i].pegColor;
            }
            MasterMind.clientMessagesOut.add(code);
            ++turn;
            waiting = true;
        }
    }

    public static void drawPlaying() {
        // background
        Draw.setColor(new Color(26, 26, 26));
        Draw.rect(0, 0, 2000, 2000);

        // current row highlight
        Draw.setColor(new Color(36, 36, 36));
        Draw.rect(290, 105 + (50 * (10 - turn)), 240, 50);

        Draw.image("board", MasterMind.gw / 2 - 2, MasterMind.gh / 2, 0, 5);

        // draw bottom pegs
        for (int i = 0; i < bottomPegs.length; i++) {
            if (bottomPegs[i] != null) {
                bottomPegs[i].draw();
            }
        }

        for (int i = 0; i < hintPegs.length; i++) {
            if (hintPegs[i] != null) {
                hintPegs[i].draw();
            }
        }

        // draw holes
        Hole.drawHoles();

        // draw pegs
        for (int i = 0; i < pegs.size(); i++) {
            pegs.get(i).draw();
        }

        // confirm button
        if (showConfirm) {
            confirmButton.draw();
        }

        if (waiting) {
            Draw.setColor(Color.WHITE);
            Draw.setFontSize(5);
            Draw.text("Waiting...", 300, 400);
        }
    }

}
