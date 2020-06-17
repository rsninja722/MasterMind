package MasterMind.gameComponents;

import game.Input;
import game.Utils;
import game.audio.Sounds;
import game.drawing.Draw;
import game.physics.Physics;
import game.physics.Point;
import game.physics.Rect;

public class Hole {

    public static final int snapDist = 15;

    public enum HoleType {
        CODE, HINT
    }

    public Point position;
    public HoleType type;

    public boolean ghost = false;

    public int pegColor = -1; // -1 means no peg assigned

    boolean hoveredLastFrame = false;

    public static Hole[][] codeHoles = new Hole[10][4];
    public static Hole[][] hintHoles = new Hole[10][4];

    public static void generateHoles() {
        for (int y = 0; y < codeHoles.length; y++) {
            for (int x = 0; x < codeHoles[y].length; x++) {
                codeHoles[y][x] = new Hole(200 + x * 60, 160 + y * 50, Hole.HoleType.CODE);
            }
        }

        for (int y = 0; y < hintHoles.length; y++) {
            for (int x = 0; x < hintHoles[y].length; x++) {
                hintHoles[y][x] = new Hole(50 + (x % 2) * 60, 140 + (x > 1 ? 1 : 0) * 25 + y * 50, Hole.HoleType.HINT);
            }
        }
    }

    public Hole(int x, int y, HoleType type) {
        this.position = new Point(x, y);
        this.type = type;
    }

    void draw() {

        // code peg
        if (this.type == HoleType.CODE) {
            Draw.image("codeHoleBack", (int) this.position.x, (int) this.position.y - 15, 0, 5);
            if (this.pegColor != -1) {
                Draw.image("codePeg" + this.pegColor + "Top", (int) this.position.x, (int) this.position.y - 15, 0, 5);
                if (mouseHovering()) {
                    Draw.image("codePegHoverTop", (int) this.position.x, (int) this.position.y - 15, 0, 5);
                    if (!hoveredLastFrame) {
                        Sounds.play("click");
                        hoveredLastFrame = true;
                    }
                } else {
                    hoveredLastFrame = false;
                }
            }
            Draw.image("codeHoleFront", (int) this.position.x, (int) this.position.y - 15, 0, 5);
            // hint peg
        } else {
            Draw.image("hintHoleBack", (int) this.position.x, (int) this.position.y - 15, 0, 5);
            if (this.pegColor != -1) {
                if (ghost) {
                    Draw.image("hintPeg" + this.pegColor + "TopGhost", (int) this.position.x, (int) this.position.y - 15, 0, 5);
                } else {
                    Draw.image("hintPeg" + this.pegColor + "Top", (int) this.position.x, (int) this.position.y - 15, 0, 5);
                    if (mouseHovering()) {
                        Draw.image("hintPegHoverTop", (int) this.position.x, (int) this.position.y - 15, 0, 5);
                        if (!hoveredLastFrame) {
                            Sounds.play("click");
                            hoveredLastFrame = true;
                        }
                    } else {
                        hoveredLastFrame = false;
                    }
                }
            }
            Draw.image("hintHoleFront", (int) this.position.x, (int) this.position.y - 15, 0, 5);
        }

    }

    // returns true if mouse is hovering over a hole's peg position
    public boolean mouseHovering() {
        if (this.type == HoleType.CODE) {
            if (Physics.rectpoint(new Rect(this.position.x, this.position.y - 20, 55, 40), Input.mousePos)) {
                return true;
            }
        } else {
            if (Physics.rectpoint(new Rect(this.position.x, this.position.y - 20, 40, 30), Input.mousePos)) {
                return true;
            }
        }
        return false;
    }

    // empties all holes
    public static void clear() {
        for (int y = 0; y < codeHoles.length; y++) {
            for (int x = 0; x < codeHoles[y].length; x++) {
                codeHoles[y][x].pegColor = -1;
            }
        }

        for (int y = 0; y < hintHoles.length; y++) {
            for (int x = 0; x < hintHoles[y].length; x++) {
                hintHoles[y][x].pegColor = -1;
                hintHoles[y][x].ghost = false;
            }
        }
    }

    public static void drawHoles() {
        if (codeHoles[9][3] != null) {
            for (int y = 0; y < codeHoles.length; y++) {
                for (int x = 0; x < codeHoles[y].length; x++) {
                    codeHoles[y][x].draw();
                }
            }
        }

        if (hintHoles[9][3] != null) {
            for (int y = 0; y < hintHoles.length; y++) {
                for (int x = 0; x < hintHoles[y].length; x++) {
                    hintHoles[y][x].draw();
                }
            }
        }
    }
}