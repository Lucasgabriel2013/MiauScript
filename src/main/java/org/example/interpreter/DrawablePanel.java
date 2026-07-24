package org.example.interpreter;

import java.awt.*;

public interface DrawablePanel {
    void setPixelColor(int x, int y, Color c);
    void repaint();

    boolean pixelIsClicked(int x, int y);
}
