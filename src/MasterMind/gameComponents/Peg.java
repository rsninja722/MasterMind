package MasterMind.gameComponents;

import game.Input;
import game.Utils;
import game.drawing.Draw;
import game.physics.Physics;
import game.physics.Point;
import game.physics.Rect;

/*
code peg colors/types :
1 - yellow
2 - green
3 - red
4 - blue
5 - black
6 - white
hint peg colors/types :
1 - white
2 - black
*/

public class Peg {

    public enum PegType {
        CODE, HINT
    }

    // default to code peg size
    int w = 40;
    int h = 35;

    double x;
    double y;

    public int color;

    public PegType type;

    public boolean grabbed = false;

    public static boolean globalGrabbed = false;

    public Peg(double x, double y, int color, PegType type) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.type = type;
        if(type == PegType.HINT) {
            this.w = 30;
            this.h = 15;
        }
    }

    public void draw() {
        // set default draw position for if there is no hole close enough
        Point drawPosition = new Point(this.x, this.y);

        // set draw position to nearest hole
        Hole nearestHole = nearestHoleTo(new Point(this.x, this.y + 50));
        if (nearestHole != null) {
            Point nearestPos = nearestHole.position;
            if (nearestPos.x != -1 && nearestPos.y != -1) {
                drawPosition = new Point(nearestPos.x, nearestPos.y - 50);
            }
        }

        // code peg
        if (this.type == PegType.CODE) {
            // shadow
            if (this.grabbed) {
                Draw.image("codePegShadow", (int) drawPosition.x, (int) drawPosition.y + 50, 0, 5);
            }
            // peg
            Draw.image("codePeg" + this.color, (int) drawPosition.x, (int) drawPosition.y + 10, 0, 5);
            // outline for when grab is possible
            if (this.touchingMouse() && !this.grabbed) {
                Draw.image("codePegHover", (int) drawPosition.x, (int) drawPosition.y + 10, 0, 5);
            }
        // hint peg
        } else {
            // shadow
            if (this.grabbed) {
                Draw.image("hintPegShadow", (int) drawPosition.x, (int) drawPosition.y + 50, 0, 5);
            }
            // peg
            Draw.image("hintPeg" + this.color, (int) drawPosition.x, (int) drawPosition.y + 10, 0, 5);
            // outline for when grab is possible
            if (this.touchingMouse() && !this.grabbed) {
                Draw.image("hintPegHover", (int) drawPosition.x, (int) drawPosition.y + 10, 0, 5);
            }
        }
    }

    // returns if the peg should be deleted or not
    public boolean update() {

        // smoothly follow mouse
        if (this.grabbed) {
            this.x = Utils.lerp(this.x, Input.mousePos.x, 0.3);
            this.y = Utils.lerp(this.y, Input.mousePos.y, 0.3);
        }

        // drop
        if (!Input.mouseDown(0)) {
            this.grabbed = false;
            globalGrabbed = false;

            Hole nearest = nearestHoleTo(new Point(this.x, this.y + 50));
            if (nearest != null) {
                nearest.pegColor = this.color;
                nearest.type = this.type == PegType.CODE ? Hole.HoleType.CODE : Hole.HoleType.HINT;
                return true;
            }

        }

        // grab
        if (this.touchingMouse() && Input.mouseClick(0) && !globalGrabbed) {
            this.grabbed = true;
            globalGrabbed = true;
        }
        return false;
    }

    // if the peg is touching the mouse
    public boolean touchingMouse() {
        if (Physics.rectpoint(new Rect(this.x, this.y, w, h), Input.mousePos) || Physics.rectpoint(new Rect(this.x, this.y + 25, 10, 25), Input.mousePos)) {
            return true;
        } else {
            return false;
        }
    }

    // returns the nearest hole if in range, otherwise returns null
    Hole nearestHoleTo(Point p) {
        // code peg
        if (this.type == PegType.CODE) {
            for (int y = 0; y < Hole.codeHoles.length; y++) {
                for (int x = 0; x < Hole.codeHoles[y].length; x++) {
                    // if hole is not occupied
                    if (Hole.codeHoles[y][x].pegColor == -1) {
                        // if hole is close enough
                        if (Physics.dist(Hole.codeHoles[y][x].position, p) < Hole.snapDist) {
                            return Hole.codeHoles[y][x];
                        }
                    }
                }
            }
            // hint peg
        } else {
            for (int y = 0; y < Hole.hintHoles.length; y++) {
                for (int x = 0; x < Hole.hintHoles[y].length; x++) {
                    // if hole is not occupied
                    if (Hole.hintHoles[y][x].pegColor == -1) {
                        // if hole is close enough
                        if (Physics.dist(Hole.hintHoles[y][x].position, p) < Hole.snapDist) {
                            return Hole.hintHoles[y][x];
                        }
                    }
                }
            }
        }
        return null;
    }
}