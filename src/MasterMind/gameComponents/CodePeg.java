package MasterMind.gameComponents;

import game.Input;
import game.Utils;
import game.drawing.Draw;

/*
peg colors/types :
1 - yellow
2 - green
3 - red
4 - blue
5 - black
6 - white
*/

public class CodePeg {

    double x;
    double y;

    int type;
    
    public CodePeg (double x, double y, int type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public void draw() {
        Draw.image("codePeg" + this.type, (int)this.x, (int)this.y, 0, 3);
    }

    public void update() {
        // double angle = Utils.pointTo(this.x, this.y, Input.mousePos.x, Input.mousePos.y);

        this.x = Utils.lerp(this.x, Input.mousePos.x, 0.3);
        this.y = Utils.lerp(this.y, Input.mousePos.y, 0.3);
    }


}