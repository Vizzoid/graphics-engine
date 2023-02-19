package org.vizzoid.utils.engine;

import org.vizzoid.utils.position.Position;

import java.awt.*;

public class ColoredPolygon extends Polygon implements Cloneable, Drawable {

    public Color color = Color.WHITE;
    public double[] zpoints;
    public FillMode mode = FillMode.OUTLINE;

    public ColoredPolygon() {
        zpoints = new double[4];
    }

    public ColoredPolygon(Position... positions) {
        load(positions);
    }

    /**
     * Creates polygon with color but does not modify anything else (arrays will update each other as a result)
     */
    public ColoredPolygon withColor(Color color) {
        ColoredPolygon polygon = clone();
        polygon.color = color;
        return polygon;
    }

    /**
     * Creates polygon with mode but does not modify anything else (arrays will update each other as a result)
     */
    public ColoredPolygon withMode(FillMode mode) {
        ColoredPolygon polygon = clone();
        polygon.mode = mode;
        return polygon;
    }

    public ColoredPolygon withContrast() {
        ColoredPolygon clone = clone();
        clone.contrast();
        return clone;
    }

    public void load(Position... positions) {
        npoints = positions.length;
        xpoints = new int[npoints];
        ypoints = new int[npoints];
        zpoints = new double[npoints];

        for (int i = 0, positionsLength = positions.length; i < positionsLength; i++) {
            Position position = positions[i];
            xpoints[i] = (int) position.getX();
            ypoints[i] = (int) position.getY();
            zpoints[i] = position.getZ();
        }
    }

    @Override
    public void draw(Graphics graphics, Engine3D engine) {
        draw(graphics);
    }

    public void draw(Graphics graphics) {
        mode.draw(graphics, this);
    }

    public void outline(Graphics graphics) {
        graphics.setColor(color);
        graphics.drawPolygon(this);
    }

    public void fill(Graphics graphics) {
        graphics.setColor(color);
        graphics.fillPolygon(this);
    }

    public void contrast() {
        color = new Color(
                255 - color.getRed(),
                255 - color.getBlue(),
                255 - color.getGreen()
        );
    }

    public double midpoint() {
        double zSum = 0;
        for (double zpoint : zpoints) {
            zSum += zpoint;
        }
        zSum /= npoints;
        return zSum;
    }

    @Override
    public ColoredPolygon clone() {
        try {
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return (ColoredPolygon) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
