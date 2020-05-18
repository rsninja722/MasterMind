package MasterMind.StateManagers;

import java.awt.Color;
import java.util.ArrayList;

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

        if(MasterMind.isPractice) {
            if(MasterMind.botReceivedMessage("[WC]SendCodePlease")) {
                MasterMind.botMessagesOut.add("C" + Utils.rand(1, 6) + Utils.rand(1, 6) + Utils.rand(1, 6) + Utils.rand(1, 6));
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

            // grab pegs from code holes
            for (int y = 0; y < Hole.codeHoles.length; y++) {
                for (int x = 0; x < Hole.codeHoles[y].length; x++) {
                    Hole h = Hole.codeHoles[y][x];
                    // if there is a peg
                    if (h.pegColor != -1) {
                        // if mouse is over a hole
                        if (h.mouseHovering()) {
                            // make a peg at the hole
                            pegs.add(new Peg(h.position.x, h.position.y - 10, h.pegColor, h.type == HoleType.CODE ? Peg.PegType.CODE : Peg.PegType.HINT));
                            pegs.get(pegs.size()-1).grabbed = true;
                            Peg.globalGrabbed = true;
                            // remove peg
                            h.pegColor = -1;
                        }
                    }
                }
            }
            // grab pegs from hint holes
            for (int y = 0; y < Hole.hintHoles.length; y++) {
                for (int x = 0; x < Hole.hintHoles[y].length; x++) {
                    Hole h = Hole.hintHoles[y][x];
                    // if there is a peg
                    if (h.pegColor != -1) {
                        // if mouse is over a hole
                        if (h.mouseHovering()) {
                            // make a peg at the hole
                            pegs.add(new Peg(h.position.x, h.position.y - 10, h.pegColor, h.type == HoleType.CODE ? Peg.PegType.CODE : Peg.PegType.HINT));
                            pegs.get(pegs.size()-1).grabbed = true;
                            Peg.globalGrabbed = true;
                            // remove peg
                            h.pegColor = -1;
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
    }

    public static void drawPlaying() {
        Draw.setColor(new Color(26, 26, 26));
        Draw.rect(0, 0, 2000, 2000);

        // draw bottom pegs
        for (int i = 0; i < bottomPegs.length; i++) {
            if (bottomPegs[i] != null) {
                bottomPegs[i].draw();
            }
        }

        // draw bottom pegs
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
    }

}
