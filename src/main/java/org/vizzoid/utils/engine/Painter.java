package org.vizzoid.utils.engine;

import java.awt.*;

@FunctionalInterface
public interface Painter {
    Painter DEFAULT = (g, m) -> {};

    void paint(Graphics graphics, long missedTime);
}
