package org.vizzoid.utils.engine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class WASDKeyListener extends MultiKeyListener {

    public WASDKeyListener(WASDListener wasdListener) {
        super(new SingleKeyListener(KeyEvent.VK_W, wasdListener::onW),
            new SingleKeyListener(KeyEvent.VK_A, wasdListener::onA),
            new SingleKeyListener(KeyEvent.VK_S, wasdListener::onS),
            new SingleKeyListener(KeyEvent.VK_D, wasdListener::onD));
    }

}
