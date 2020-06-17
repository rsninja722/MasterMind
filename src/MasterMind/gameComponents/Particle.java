package MasterMind.gameComponents;

import java.util.ArrayList;

import MasterMind.MasterMind;
import game.Utils;
import game.audio.Sounds;
import game.drawing.Draw;

import java.awt.Color;

public class Particle {
    public static ArrayList<Particle> particles = new ArrayList<Particle>();

    public static void addParticles(int amount, double xPos, double yPos, Color color) {
        for (int i = 0; i < amount; i++) {
            particles.add(new Particle(xPos, yPos, color));
        }
        if (Utils.rand(0, 1) == 1) {
            MasterMind.cameraShakeX = Utils.rand(0, 1) == 1 ? -4 : 4;
        } else {
            MasterMind.cameraShakeY = Utils.rand(0, 1) == 1 ? -4 : 4;
        }
        Sounds.play("place");
    }

    public static void updateParticles() {
        for (int i = 0; i < particles.size(); i++) {
            if (particles.get(i).update()) {
                particles.remove(i);
                i--;
            }
        }
    }

    public static void drawParticles() {
        for (int i = 0; i < particles.size(); i++) {
            particles.get(i).draw();
        }
    }

    double x;
    double y;
    double vx;
    double vy;
    Color c;
    int size;
    int sizeDecreaseCounter;

    public Particle(double xPos, double yPos, Color color) {
        x = xPos;
        y = yPos;
        // set velocity
        double angle = Math.random() * Math.PI * 2;
        double vel = Utils.rand(1, 3);
        vx = Math.cos(angle) * vel;
        vy = Math.sin(angle) * vel;
        // chance to lighten or darken color
        c = Utils.rand(0, 1) == 1 ? color.darker() : (Utils.rand(0, 1) == 1 ? color.brighter().brighter() : color);

        size = Utils.rand(3, 6);
        sizeDecreaseCounter = 30;

        // move slightly to avoid spawning in the same spot as other particles
        x += vx * 5;
        y += vy * 5;
    }

    private boolean update() {
        // move
        x += vx;
        y += vy;

        // friction in x
        vx += vx / -25;
        // gravity in y
        vy += 0.1;

        // shrink
        if (--sizeDecreaseCounter == 0) {
            sizeDecreaseCounter = 30;
            if (--size == 0) {
                // delete from array list
                return true;
            }
        }

        return false;
    }

    private void draw() {
        Draw.setColor(c);
        Draw.rect((int) x, (int) y, size, size);
    }

}