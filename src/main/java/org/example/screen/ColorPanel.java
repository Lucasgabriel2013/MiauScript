package org.example.screen;

import org.example.interpreter.DrawablePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ColorPanel extends JPanel implements DrawablePanel {
    private final int width = 100;
    private final int height = 50;

    private final Pixel[][] pixels = new Pixel[height][width];

    private Point lastPixel = null;

    public ColorPanel() {
        setPreferredSize(new Dimension(800, 400));

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                pixels[y][x] = new Pixel(Color.BLACK, false);
            }
        }

        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                ColorPanel.this.mousePressed(e, true);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                ColorPanel.this.mousePressed(e, false);

                pixels[lastPixel.y][lastPixel.x].setMousePressed(false);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                ColorPanel.this.mousePressed(e, true);
            }
        };

        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
    }

    private void mousePressed(MouseEvent e, boolean isPressed) {
        int x = e.getX() / 8;
        int y = e.getY() / 8;

        if (!isPressed || (lastPixel != null && (lastPixel.x != x || lastPixel.y != y))) {
            if (lastPixel != null) {
                pixels[lastPixel.y][lastPixel.x].setMousePressed(false);
            }
        }

        if (y >= 0 && y < 50 && x >= 0 && x < 100) {
            pixels[y][x].setMousePressed(true);
            lastPixel = new Point(x, y);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                g2.setColor(pixels[y][x].getColor());
                g2.fillRect(x * 8, y * 8, 8, 8);
            }
        }
    }

    @Override
    public void setPixelColor(int x, int y, Color c) {
        pixels[y][x].setColor(c);
    }

    @Override
    public boolean pixelIsClicked(int x, int y) {
        return pixels[y][x].isMousePressed();
    }
}
