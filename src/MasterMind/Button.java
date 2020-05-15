package MasterMind;

// buttons used in GUI

import java.awt.Color;
import java.util.ArrayList;

import game.Input;
import game.audio.Sounds;
import game.drawing.Draw;
import game.physics.Physics;
import game.physics.Rect;

public class Button {

    // colors for all buttons
    static Color backGroundColorDark = new Color(26, 26, 26);
    static Color backGroundColor = new Color(46, 46, 46);
    static Color hoverColor = new Color(66, 66, 66);
    static Color outLineColor = new Color(76, 76, 76);
    static Color textColor = new Color(255, 255, 255);

    // button size and collision box
    Rect rect;
    // what to call when clicked
    Runnable callBack;
    // text to display
    String text;

    boolean shouldPlaySound = true;

    public Button(int x, int y, int w, int h, String text, Runnable callBack) {
        this.rect = new Rect(x, y, w, h);
        this.callBack = callBack;
        this.text = text;
    }

    public void draw() {
        boolean hover = Physics.rectpoint(this.rect, Input.rawMousePos);
        boolean press = Input.mouseDown(0);
        int contentOffset = 0;
        if (hover) {
            if (press) {
                contentOffset = 2;
            } else {
                contentOffset = -2;
            }
        }

        // draw background color based on if the player is hovering over the button
        if (hover) {
            // hover sound
            if (shouldPlaySound) {
                Sounds.play("click");
                shouldPlaySound = false;
            }

            // "shadow"
            Draw.setColor(backGroundColorDark);
            Draw.rect(this.rect);

            Draw.setColor(hoverColor);
        } else {
            shouldPlaySound = true;
            Draw.setColor(backGroundColor);
        }
        Draw.rect((int) this.rect.x, (int) this.rect.y + contentOffset, this.rect.w - Math.abs(contentOffset) * 2, this.rect.h - Math.abs(contentOffset) * 2);

        // text
        Draw.setColor(textColor);
        if (this.text.length() < 8) {
            Draw.setFontSize(4);
        } else {
            Draw.setFontSize(3);
        }
        Draw.text(this.text, (int) (10 + this.rect.x - this.rect.w / 2), (int) this.rect.y + 12 + contentOffset);

        // outline
        Draw.setColor(outLineColor);
        Draw.setLineWidth(2.0f);
        Draw.rectOutline(this.rect);
    }

    public void update() {
        // when clicked, call callback
        if (Physics.rectpoint(this.rect, Input.rawMousePos) && Input.mouseClick(0)) {
            // Sounds.play("bigStep");
            this.callBack.run();
        }
    }
}
