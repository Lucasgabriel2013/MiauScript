package org.example.screen;

import javax.swing.*;
import java.awt.*;

public class ColorPanel extends JPanel {
    Dimension dimension = new Dimension(800, 400);

    private final int width = 100;
    private final int height = 50;

    Color[][] frameBuffer = new Color[height][width];

    public ColorPanel() {
        Color V = Color.RED;
        Color Y = Color.YELLOW;
        Color M = new Color(101, 67, 33);
        Color P = new Color(255, 218, 185);
        Color A = Color.BLUE;
        Color X = Color.BLACK;

        Color[][] marioSprite = {
                {X, X, X, V, V, V, V, V, X, X, X, X},
                {X, X, V, V, V, V, V, V, V, V, V, X},
                {X, X, M, M, M, P, P, M, P, X, X, X},
                {X, M, P, M, P, P, P, M, P, P, P, X},
                {X, M, P, M, M, P, P, P, M, P, P, M},
                {X, M, M, P, P, P, P, M, M, M, M, X},
                {X, X, X, P, P, P, P, P, P, P, X, X},
                {X, X, V, V, A, V, V, V, X, X, X, X},
                {X, V, V, V, A, V, V, A, V, V, V, X},
                {V, V, V, V, A, A, A, A, V, V, V, V},
                {P, P, V, A, A, Y, A, A, Y, A, V, P},
                {P, P, P, A, A, A, A, A, A, A, P, P},
                {X, X, A, A, A, X, X, A, A, A, X, X},
                {X, M, M, M, X, X, X, X, M, M, M, X},
                {M, M, M, M, X, X, X, X, M, M, M, M}
        };

        for (int l = 0; l < 50; l++) {
            for (int c = 0; c < 100; c++) {
                frameBuffer[l][c] = Color.BLACK;
            }
        }

        int inicioLinha = 18;
        int inicioColuna = 44;

        for (int l = 0; l < marioSprite.length; l++) {
            for (int c = 0; c < marioSprite[l].length; c++) {
                if (marioSprite[l][c] != X) {
                    frameBuffer[inicioLinha + l][inicioColuna + c] = marioSprite[l][c];
                }
            }
        }

        setPreferredSize(dimension);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (frameBuffer[y][x] == null) {
                    frameBuffer[y][x] = Color.black;
                }

                g2.setColor(frameBuffer[y][x]);

                g2.fillRect(x * 8, y * 8, 8, 8);
            }
        }
    }
}
