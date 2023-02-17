package org.vizzoid.utils.engine;

import java.awt.*;

@FunctionalInterface
public interface Painter {
    Painter DEFAULT = g -> {};

    void paint(Graphics graphics);
}
