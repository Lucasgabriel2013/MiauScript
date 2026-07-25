package org.example.interpreter.command.screen;

import org.example.interpreter.Context;
import org.example.interpreter.MiauScriptException;
import org.example.interpreter.command.Command;
import org.example.interpreter.expression.Expression;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class SoundCommand implements Command {
    private static final float SAMPLE_RATE = 44100f;

    Expression hzExpression;
    Expression msecsExpression;

    public SoundCommand(Expression hzExpression, Expression msecsExpression) {
        this.hzExpression = hzExpression;
        this.msecsExpression = msecsExpression;
    }

    @Override
    public void execute(Context context) {
        Object firstPart = hzExpression.evaluate(context.variableManager);
        Object secondPart = msecsExpression.evaluate(context.variableManager);

        if (firstPart instanceof Double hz && secondPart instanceof Double msecs) {
            playSound(hz, msecs.intValue());

            return;
        }

        throw new MiauScriptException("Erro no sound", firstPart + " e " + secondPart + " deveriam ser números");

    }

    public static void playSound(double hz, int msecs) {
        int numSamples = (int) (SAMPLE_RATE * msecs / 1000);
        byte[] buf = new byte[numSamples];

        for (int i = 0; i < buf.length; i++) {
            double angle = i / (SAMPLE_RATE / hz) * 2.0 * Math.PI;
            buf[i] = (byte) (Math.sin(angle) * 127.0);
        }

        AudioFormat af = new AudioFormat(SAMPLE_RATE, 8, 1, true, false);
        try (SourceDataLine sdl = AudioSystem.getSourceDataLine(af)) {
            sdl.open(af);
            sdl.start();
            sdl.write(buf, 0, buf.length);
            sdl.drain();
            sdl.stop();
        } catch (LineUnavailableException e) {
            throw new MiauScriptException("Line Unavailable");
        }
    }
}
