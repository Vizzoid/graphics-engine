package org.vizzoid.utils.engine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public interface KeyboardListener extends KeyListener  {

    void tick(long missedTime);

    @Override
    default void keyTyped(KeyEvent e) {

    }

    @Override
    default void keyPressed(KeyEvent e) {
        attempt(e, true);
    }

    @Override
    default void keyReleased(KeyEvent e) {
        attempt(e, false);
    }

    default void attempt(KeyEvent e, boolean set) {
        attempt(e.getKeyCode(), set);
    }

    void attempt(int keycode, boolean set);

}
