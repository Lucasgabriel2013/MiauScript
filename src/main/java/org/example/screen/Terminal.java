package org.example.screen;

import org.example.interpreter.Console;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Terminal extends JPanel implements Console {
    private final JTextField input = new JTextField();
    private final JTextArea out = new JTextArea();
    BlockingQueue<String> queue = new LinkedBlockingQueue<>();

    private final Set<Key> keysPressed = EnumSet.noneOf(Key.class);

    public Terminal() {
        setLayout(new BorderLayout());

        add(new JScrollPane(out));
        out.setEnabled(false);
        out.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.green));
        out.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));
        out.setBackground(Color.black);
        out.setForeground(Color.green);

        setFocusable(true);
        requestFocus();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                int code = e.getKeyCode();

                switch (code) {
                    case KeyEvent.VK_W -> keysPressed.add(Key.W);
                    case KeyEvent.VK_A -> keysPressed.add(Key.A);
                    case KeyEvent.VK_S -> keysPressed.add(Key.S);
                    case KeyEvent.VK_D -> keysPressed.add(Key.D);
                    case KeyEvent.VK_ENTER -> keysPressed.add(Key.ENTER);
                    case KeyEvent.VK_SPACE -> keysPressed.add(Key.SPACE);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                super.keyPressed(e);
                int code = e.getKeyCode();

                switch (code) {
                    case KeyEvent.VK_W -> keysPressed.remove(Key.W);
                    case KeyEvent.VK_A -> keysPressed.remove(Key.A);
                    case KeyEvent.VK_S -> keysPressed.remove(Key.S);
                    case KeyEvent.VK_D -> keysPressed.remove(Key.D);
                    case KeyEvent.VK_ENTER -> keysPressed.remove(Key.ENTER);
                    case KeyEvent.VK_SPACE -> keysPressed.remove(Key.SPACE);
                }
            }
        });

        add(input, BorderLayout.SOUTH);
        input.setFont(new Font("", Font.BOLD, 20));

        input.addActionListener(_ -> {
                    requestFocus();
                    input.setText("");
                    queue.add(input.getText());
                }
        );
    }

    @Override
    public void print(Object what) {
        out.append(what.toString());
    }

    @Override
    public void println(Object what) {
        out.append(what + "\n");
    }

    @Override
    public void clear() {
        out.setText("");
    }

    @Override
    public boolean isPressed(Key key) {
        return keysPressed.contains(key);
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
