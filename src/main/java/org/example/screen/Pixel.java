package org.example.screen;

import java.awt.*;

public class Pixel {
    private Color color;
    private boolean mousePressed;

    public Pixel(Color color, boolean mousePressed) {
        this.color = color;
        this.mousePressed = mousePressed;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean isMousePressed() {
        return mousePressed;
    }

    public void setMousePressed(boolean mousePressed) {
        this.mousePressed = mousePressed;
    }
}
