package org.vizzoid.utils.engine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class MultiKeyListener implements KeyListener {

    private final SingleKeyListener[] listeners;

    public MultiKeyListener(SingleKeyListener... listeners) {
        this.listeners = listeners;
    }

    public void onPaint() {
        for (SingleKeyListener listener : listeners) {
            listener.onPaint();
        }
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
        for (SingleKeyListener listener : listeners) {
            listener.attempt(keycode, set);
        }
    }


}
