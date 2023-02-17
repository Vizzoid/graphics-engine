package org.vizzoid.utils.engine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.function.Supplier;

public class SingleKeyListener implements KeyListener {

    private final int keycode;
    private final Supplier<Boolean> onPress;
    private boolean pressed;

    public SingleKeyListener(int keycode, Supplier<Boolean> onPress) {
        this.keycode = keycode;
        this.onPress = onPress;
    }

    public void onPaint() {
        if (pressed) pressed = onPress.get();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        attempt(e, true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        attempt(e, false);
    }

    public void attempt(KeyEvent e, boolean set) {
        attempt(e.getKeyCode(), set);
    }

    public void attempt(int keycode, boolean set) {
        if (keycode == this.keycode) {
            pressed = set;
        }
    }
}
