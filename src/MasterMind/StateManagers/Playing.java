package MasterMind.StateManagers;

/*
    this is mostly spaghetti code, it is easier to see how the game progresses in serverHandler
*/

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import MasterMind.Button;
import MasterMind.MasterMind;
import MasterMind.gameComponents.Hole;
import MasterMind.gameComponents.Particle;
import MasterMind.gameComponents.Peg;
import MasterMind.gameComponents.Hole.HoleType;
import game.Input;
import game.Utils;
import game.audio.Sounds;
import game.drawing.Draw;

public class Playing {

    static Peg[] bottomPegs = new Peg[6];
    static Peg[] hintPegs = new Peg[2];

    static ArrayList<Peg> pegs = new ArrayList<Peg>();

    static ArrayList<String> popUps = new ArrayList<String>();
    static int popUpTime = 0;

    static boolean waiting = true;

    static boolean makeCode = false;

    static int turn = 0;

    static boolean isBreaker = true;

    static int botGuessAmount;
    static int botGuesses;

    static boolean showConfirm = false;
    static Button confirmButton = new Button(500, 325, 150, 50, "Confirm", Playing::confirm);

    static int playerGuesses = 0;
    static int opponentGuesses = 0;

    static String pegsToShow = "";

    static Boolean acknowledge = false;

    static Button chatButton = new Button(750, 675, 200, 50, "send message", Playing::chat);
    static String chatMessage = "";

    static ArrayList<String> chatHistory = new ArrayList<String>();
    static int chatScroll = 0;

    static void chat() {
        chatMessage = JOptionPane.showInputDialog("Enter message");
    }

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

            chatHistory.add("Welcome to Master Mind!");
            chatHistory.add("Use chat by using the send message button below");
        }

        // chat
        if (!chatMessage.equals("")) {
            MasterMind.clientMessagesOut.add("[CHAT]" + chatMessage);
            chatMessage = "";
        }
        for (int i = 0; i < MasterMind.clientMessagesIn.size(); i++) {
            if (MasterMind.clientMessagesIn.get(i).startsWith("[CHAT]")) {
                chatHistory.add(MasterMind.clientMessagesIn.get(i).substring(6));
                chatScroll += 10;
                MasterMind.clientMessagesIn.remove(i);
            }
        }

        if (MasterMind.isPractice) {
            // start new round
            if (MasterMind.clientReceivedMessage("[NR]sendReadyPlease")) {
                MasterMind.clientMessagesOut.add("[NR]ready");
                Hole.clear();
            }
            if (MasterMind.botReceivedMessage("[NR]sendReadyPlease")) {
                MasterMind.botMessagesOut.add("[NR]ready");
            }

            // make client send a set code
            if (MasterMind.clientReceivedMessage("[WC]SendCodePlease")) {
                MasterMind.clientMessagesOut.add("C1111");
                botGuessAmount = Utils.rand(5, 9);
                turn = 0;
            }

            // make bot make code
            if (MasterMind.botReceivedMessage("[WC]SendCodePlease")) {
                MasterMind.botMessagesOut.add("C" + Utils.rand(1, 6) + Utils.rand(1, 6) + Utils.rand(1, 6) + Utils.rand(1, 6));
            }
            if (MasterMind.botReceivedMessage("[WG]SendAcknowledgementPlease")) {
                MasterMind.botMessagesOut.add("[WA]acknowledgement");
            }

            // make bot guess
            if (MasterMind.botReceivedMessage("[WA]SendGuessPlease") || MasterMind.botReceivedMessage("[WC]SendGuessPlease")) {
                if (botGuesses < botGuessAmount) {
                    ++opponentGuesses;
                    ++turn;
                    ++botGuesses;
                    String code = "C" + Utils.rand(2, 6) + Utils.rand(1, 6) + Utils.rand(1, 6) + Utils.rand(1, 6);
                    MasterMind.botMessagesOut.add(code);

                    for (int j = 1; j < 5; j++) {
                        Hole.codeHoles[10 - turn][j - 1].pegColor = Integer.parseInt(code.substring(j, j + 1));
                    }
                } else {
                    ++opponentGuesses;
                    ++turn;
                    MasterMind.botMessagesOut.add("C1111");
                    for (int j = 1; j < 5; j++) {
                        Hole.codeHoles[10 - turn][j - 1].pegColor = 1;
                    }
                    botGuesses = 0;
                    MasterMind.updateDelay = 100;
                    turn = 0;
                    return;
                }
            }

            // make client send acknowledgement
            if (MasterMind.clientReceivedMessage("[WG]SendAcknowledgementPlease")) {
                MasterMind.clientMessagesOut.add("[WA]acknowledgement");
            }
        } else {
            // start new round
            if (MasterMind.clientReceivedMessage("[NR]sendReadyPlease")) {
                MasterMind.clientMessagesOut.add("[NR]ready");
                Hole.clear();
                waiting = true;
                pegsToShow = "";
            }

            if (!isBreaker) {
                // receive codes
                for (int i = 0; i < MasterMind.clientMessagesIn.size(); i++) {
                    if (MasterMind.clientMessagesIn.get(i).startsWith("hint")) {
                        String hint = MasterMind.clientMessagesIn.get(i).substring(4);
                        MasterMind.clientMessagesIn.remove(i);
                        acknowledge = true;
                        waiting = false;
                        int wrongCount = 0;
                        for (int j = 0; j < 4; j++) {
                            if (hint.charAt(j) != '0') {
                                Hole.hintHoles[10 - turn][j].pegColor = Integer.parseInt(hint.charAt(j) + "");
                                Hole.hintHoles[10 - turn][j].ghost = true;
                            } else {
                            	wrongCount++;
                            }
                        }
                        i--;
                        if(wrongCount == 4) {
                        	MasterMind.clientMessagesOut.add("[WA]acknowledgement");
                        } else {
                        	popUps.add("Enter Hint");
                        }
                        continue;
                    }
                    if (MasterMind.clientMessagesIn.get(i).charAt(0) == 'C') {
                        String code = MasterMind.clientMessagesIn.get(i);
                        MasterMind.clientMessagesIn.remove(i);

                        MasterMind.clientMessagesOut.add("[GenHint]" + code.substring(1));

                        for (int j = 1; j < 5; j++) {
                            int pegColor = Integer.parseInt(code.substring(j, j + 1));
                            if (pegColor != 0) {
                                Hole.codeHoles[9 - turn][j - 1].pegColor = pegColor;
                                Hole h = Hole.codeHoles[9 - turn][j - 1];
                                Particle.addParticles(10, h.position.x, h.position.y, Peg.pegColors[pegColor - 1]);
                            }
                        }

                        ++turn;
                        ++opponentGuesses;
                        i--;
                        continue;
                    }
                }

                // acknowledgement
                if (turn > 0 && acknowledge) {
                    boolean ready = true;
                    for (int i = 0; i < 4; i++) {
                        if (Hole.hintHoles[10 - turn][i].ghost) {
                            ready = false;
                            break;
                        }
                    }
                    if (ready) {
                        MasterMind.clientMessagesOut.add("[WA]acknowledgement");
                        acknowledge = false;
                        waiting = true;
                    }
                }

            }

            if (!isBreaker) {
                if (!waiting && turn == 0) {

                }
            }
        }

        // round start
        if (MasterMind.clientReceivedMessage("[NR]Game Starting")) {
            Hole.clear();
        }

        // determine what the client is
        if (MasterMind.clientReceivedMessage("[WC]YouAreCodeBreaker")) {
            isBreaker = true;
            if(MasterMind.isPractice) {
            	waiting = false;
            } else {
            	waiting = true;
            }
            turn = 0;
        }
        if (MasterMind.clientReceivedMessage("[WC]SendCodePlease")) {
            popUps.add("Enter Code");
            makeCode = true;
            isBreaker = false;
            waiting = false;
            turn = 0;
        }

        // enter guess
        if (MasterMind.clientReceivedMessage("[WC]SendGuessPlease") || MasterMind.clientReceivedMessage("[WA]SendGuessPlease")) {
            popUps.add("Enter Guess");
            waiting = false;
        }

        // receive hints
        if (turn > 0) {
            for (int i = 0; i < MasterMind.clientMessagesIn.size(); i++) {
                if (MasterMind.clientMessagesIn.get(i).charAt(0) == 'H') {
                    String hint = MasterMind.clientMessagesIn.get(i);
                    MasterMind.clientMessagesIn.remove(i);
                    waiting = false;

                    for (int j = 1; j < 5; j++) {
                        int pegColor = Integer.parseInt(hint.substring(j, j + 1));
                        if (pegColor != 0) {
                            Hole.hintHoles[10 - turn][j - 1].pegColor = pegColor;
                            Hole h = Hole.hintHoles[10 - turn][j - 1];
                            Particle.addParticles(10, h.position.x, h.position.y, Peg.pegColors[2 - pegColor + 4]);
                        }
                    }
                }
            }
        }

        // round done
        if (MasterMind.clientReceivedMessage("[WA]BreakerWins")) {
            popUps.add("Breaker Wins");
            MasterMind.updateDelay = 100;
            turn = 0;
            waiting = true;
            isBreaker = !isBreaker;
            return;
        }
        if (MasterMind.clientReceivedMessage("[WA]MakerWins")) {
            popUps.add("Maker Wins");
            MasterMind.updateDelay = 100;
            turn = 0;
            waiting = true;
            isBreaker = !isBreaker;
            return;
        }

        // game done
        if (MasterMind.clientReceivedMessage("YouWin")) {
            MasterMind.won = true;
            MasterMind.state = MasterMind.GameState.RESULTS;
            return;
        }
        if (MasterMind.clientReceivedMessage("YouLose")) {
            MasterMind.won = false;
            MasterMind.state = MasterMind.GameState.RESULTS;
            return;
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
                                pegs.add(new Peg(h.position.x, h.position.y - 10, h.pegColor, h.type == HoleType.CODE ? Peg.PegType.CODE : Peg.PegType.HINT));
                                pegs.get(pegs.size() - 1).grabbed = true;
                                Peg.globalGrabbed = true;
                                Sounds.play("pickUp");
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
                        if (h.pegColor != -1 && !h.ghost) {
                            // if mouse is over a hole
                            if (h.mouseHovering()) {
                                // make a peg at the hole
                                pegs.add(new Peg(h.position.x, h.position.y - 10, h.pegColor, h.type == HoleType.CODE ? Peg.PegType.CODE : Peg.PegType.HINT));
                                pegs.get(pegs.size() - 1).grabbed = true;
                                Peg.globalGrabbed = true;
                                Sounds.play("pickUp");
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

        if ((MasterMind.isPractice ? (isBreaker) : (isBreaker ? true : makeCode)) && !waiting && turn < 10) {
            for (int i = 0; i < 4; i++) {
                if (Hole.codeHoles[9 - turn][i].pegColor == -1) {
                    showConfirm = false;
                    break;
                } else {
                    showConfirm = true;
                }
            }
        } else {
            showConfirm = false;
        }

        // update particles
        Particle.updateParticles();

        // confirm button
        if (showConfirm) {
            confirmButton.update();
        }

        chatButton.update();
    }

    static void confirm() {
        if (isBreaker) {
            String code = "C";
            for (int i = 0; i < 4; i++) {
                code += Hole.codeHoles[9 - turn][i].pegColor;
            }
            MasterMind.clientMessagesOut.add(code);
            ++turn;
            ++playerGuesses;
            waiting = true;
        } else {
            String code = "C";
            pegsToShow = "";
            for (int i = 0; i < 4; i++) {
                code += Hole.codeHoles[9][i].pegColor;
                pegsToShow += Hole.codeHoles[9][i].pegColor;
            }
            MasterMind.clientMessagesOut.add(code);
            Hole.clear();
            makeCode = false;
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

        Draw.image("board", 290, MasterMind.gh / 2, 0, 5);

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

        // draw particles
        Particle.drawParticles();

        // confirm button
        if (showConfirm) {
            confirmButton.draw();
        }

        chatButton.draw();

        Draw.setColor(Color.WHITE);
        Draw.setFontSize(2);
        Draw.text("your guesses: " + playerGuesses, 80, 30);
        Draw.text("opponent guesses: " + opponentGuesses, 30, 60);

        Draw.text((isBreaker ? "code breaker" : "code maker"), 400, 700);

        if (!pegsToShow.equals("")) {
            for (int i = 0; i < 4; i++) {
                Draw.image("codePeg" + pegsToShow.charAt(i), 400 + i * 16, 50, 0, 2);
            }
        }

        if (waiting) {
            Draw.setFontSize(5);
            Draw.text("Waiting...", 200, 400);
        }

        // handle pop ups
        if (popUps.size() > 0) {
            Draw.setFontSize(3);
            Draw.text(popUps.get(0), 300, 40 + (int) (Math.sin(popUpTime / 10.0) * 10));

            ++popUpTime;
            if (popUpTime >= 150) {
                popUpTime = 0;
                popUps.remove(0);
            }
        }

        // chat
        Draw.setFontSize(1);
        for (int i = 0; i < chatHistory.size(); i++) {
            if (Draw.getWidthOfText(chatHistory.get(i)) > 300) {
                String msg = chatHistory.get(i);
                chatHistory.remove(i);
                chatHistory.add(i, msg.substring(47));
                chatHistory.add(i, msg.substring(0, 47));
                chatScroll += 10;
            }
            Draw.text(chatHistory.get(i), 610, -chatScroll + 600 + i * 10);
        }
    }
}