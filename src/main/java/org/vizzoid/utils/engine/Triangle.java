package org.vizzoid.utils.engine;

import org.jetbrains.annotations.Nullable;
import org.vizzoid.utils.position.ImmoveablePosition;
import org.vizzoid.utils.position.Position;

import java.awt.*;

/**
 * 3 pointed 3d object triangle
 */
public class Triangle implements Object3D {

    private final Position pos1;
    private final Position pos2;
    private final Position pos3;

    public Triangle(double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3) {
        this(
                new UnnormalizedPosition(x1, y1, z1),
                new UnnormalizedPosition(x2, y2, z2),
                new UnnormalizedPosition(x3, y3, z3)
        );
    }

    public Triangle(Position pos1, Position pos2, Position pos3) {
        this.pos1 = pos1.immoveable();
        this.pos2 = pos2.immoveable();
        this.pos3 = pos3.immoveable();
        // potentially this polygon can be created earlier and used consistently?
        // perhaps a tracked polygon which will reset and reinitialize when there is a significant change in engine or camera?
        /* could be:
        this.polygon = engine.addIfAbsent(new TrackedPolygon() /* extends ColoredPolygon *//* { // engine.addIfAbsent does not add if already added
            /**
             * called on fov, rendering, position, angle change, or other reason
             */
        /*
            public void reset(Engine3D engine) {
                createPolygon(engine, this);
            }
        })
        */
    }

    public Position getPos1() {
        return pos1;
    }

    public Position getPos2() {
        return pos2;
    }

    public Position getPos3() {
        return pos3;
    }

    @Override
    public void draw(Graphics graphics, Engine3D engine) {
        ColoredPolygon fill = createPolygon(engine, new ColoredPolygon());
        // potentially this polygon can be created earlier and used consistently? see above
        if (fill == null) return;
        fill.mode = FillMode.FILL;
        //ColoredPolygon outline = fill.withContrast();
        //outline.mode = FillMode.OUTLINE;

        engine.queue(fill);
        //engine.queue(outline);
    }

    protected @Nullable ColoredPolygon createPolygon(Engine3D engine, ColoredPolygon polygon) {
        Position pos2d1 = engine.prepareProjection(pos1);
        Position pos2d2 = engine.prepareProjection(pos2);
        Position pos2d3 = engine.prepareProjection(pos3);

        Position normalVector = pos2d1.normal(pos2d2, pos2d3);
        Position cameraPosition = engine.getCamera().getPosition();
        Position cameraLine = cameraPosition.line(pos2d1);

        if (normalVector.dotProduct(cameraLine) >= 0) return null; // do not load triangle if unseeable

        pos2d1 = engine.completeProjection(pos2d1);
        pos2d2 = engine.completeProjection(pos2d2);
        pos2d3 = engine.completeProjection(pos2d3);

        polygon.load(pos2d1, pos2d2, pos2d3);

        polygon.color = color(normalVector.dotProduct(engine.getLightDirection()));
        return polygon;
    }

    /**
     * lighting
     */
    private Color color(double dotProduct) {
        return new Color(switch ((int) (13 * dotProduct)) {
            case 1, 2, 3, 4 -> Color.BLACK.getRGB() | Color.DARK_GRAY.getRGB();
            case 5, 6, 7, 8 -> Color.DARK_GRAY.getRGB() | Color.GRAY.getRGB();
            case 9, 10, 11, 12 -> Color.GRAY.getRGB() | Color.WHITE.getRGB();
            default -> Color.BLACK.getRGB() | Color.BLACK.getRGB();
        });
    }


}