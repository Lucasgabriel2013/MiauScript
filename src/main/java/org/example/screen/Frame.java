package org.example.screen;

import javax.swing.*;
import java.awt.*;

public class Frame extends JFrame {
    private final JPanel colorPanel;
    private final Terminal terminal;

    public Frame(JPanel colorPanel, Terminal terminal) {
        super("MiauScreen");

        this.colorPanel = colorPanel;
        this.terminal = terminal;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(2, 1));
        setSize(800, 838);
        setResizable(false);

        add(colorPanel);
        add(terminal);

        setVisible(true);
    }
}
