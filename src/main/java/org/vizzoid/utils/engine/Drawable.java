package org.vizzoid.utils.engine;

import java.awt.*;

public interface Drawable {

    double midpoint();

    void draw(Graphics graphics, Engine3D engine);

}
