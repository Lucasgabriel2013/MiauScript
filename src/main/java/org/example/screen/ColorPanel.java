package org.example.screen;

import org.example.interpreter.DrawablePanel;

import javax.swing.*;
import java.awt.*;

public class ColorPanel extends JPanel implements DrawablePanel {
    private final int width = 100;
    private final int height = 50;

    private final Color[][] frameBuffer = new Color[height][width];

    public ColorPanel() {
        setPreferredSize(new Dimension(800, 400));

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                frameBuffer[y][x] = Color.black;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                g2.setColor(frameBuffer[y][x]);
                g2.fillRect(x * 8, y * 8, 8, 8);
            }
        }
    }

    @Override
    public void setPixel(int x, int y, Color c) {
        frameBuffer[y][x] = c;
    }
}
