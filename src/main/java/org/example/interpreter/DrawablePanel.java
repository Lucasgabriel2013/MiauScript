package org.example.interpreter;

import java.awt.*;

public interface DrawablePanel {
    void setPixel(int x, int y, Color c);
    void repaint();
}
