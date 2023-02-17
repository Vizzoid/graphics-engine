package org.vizzoid.utils.engine;

import java.awt.*;

/**
 * How a polygon is drawn on graphics, fill the object is filled in, outline the object is transparent except for sides
 */
public enum FillMode {

    OUTLINE,
    FILL {
        @Override
        public void draw(Graphics graphics, ColoredPolygon polygon) {
            polygon.fill(graphics);
        }
    };

    public void draw(Graphics graphics, ColoredPolygon polygon) {
        polygon.outline(graphics);
    }

}
