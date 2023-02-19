package org.vizzoid.utils.engine;

import java.awt.event.KeyEvent;

public class ArrowKeyListener extends MultiKeyListener {

    public ArrowKeyListener(ArrowListener arrowListener) {
        super(new SingleKeyListener(KeyEvent.VK_UP, arrowListener::onUp),
                new SingleKeyListener(KeyEvent.VK_LEFT, arrowListener::onLeft),
                new SingleKeyListener(KeyEvent.VK_DOWN, arrowListener::onDown),
                new SingleKeyListener(KeyEvent.VK_RIGHT, arrowListener::onRight));
    }

}
