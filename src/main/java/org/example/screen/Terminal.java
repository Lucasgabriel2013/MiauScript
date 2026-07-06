package org.example.screen;

import org.example.interpreter.Console;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Terminal extends JPanel implements Console {
    private final JTextField input = new JTextField();
    private final JTextArea out = new JTextArea();
    BlockingQueue<String> queue = new LinkedBlockingQueue<>();

    public Terminal() {
        setLayout(new BorderLayout());

        add(out);
        out.setEnabled(false);
        out.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.green));
        out.setFont(new Font("", Font.BOLD, 20));
        out.setBackground(Color.black);
        out.setForeground(Color.green);

        add(input, BorderLayout.SOUTH);
        input.setFont(new Font("", Font.BOLD, 20));

        input.addActionListener(_ -> queue.add(input.getText()));
    }

    @Override
    public void print(Object what) {
        out.setText(out.getText() + what);
    }

    @Override
    public void println(Object what) {
        out.setText(out.getText() + what + "\n");
    }

    @Override
    public void clear() {
        out.setText("");
    }

    @Override
    public String input() {
        queue.clear();

        try {
            String s = queue.take();

            out.setText(out.getText() + "\n> " + s + "\n");

            return s;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
