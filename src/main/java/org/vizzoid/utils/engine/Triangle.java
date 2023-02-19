package org.vizzoid.utils.engine;

import org.vizzoid.utils.position.ImmoveablePosition;
import org.vizzoid.utils.position.MoveablePosition;
import org.vizzoid.utils.position.Position;

import javax.sound.sampled.Clip;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;

/**
 * 3 pointed 3d object triangle
 */
public class Triangle implements Object3D, Drawable {

    private final Position pos1;
    private final Position pos2;
    private final Position pos3;

    public Triangle(double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3) {
        this(
                new ImmoveablePosition(x1, y1, z1),
                new ImmoveablePosition(x2, y2, z2),
                new ImmoveablePosition(x3, y3, z3)
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

    public Collection<Triangle> buildBounds(Engine3D engine) {
        Dimension dimension = engine.getInternalEngine().dimension;
        List<Triangle> queue = new ArrayList<>();
        queue.add(this);
        int numOfNewTriangles = 1;

        for (int i = 0; i < 4; i++) {
            while (numOfNewTriangles > 0) {
                Triangle test = queue.remove(0);
                numOfNewTriangles--;
                if (test == null) continue;

                ClipInfo clip = switch (i) {
                    case 0 -> test.clipAgainstPlane(Position.EMPTY, Position.UP);
                    case 1 -> test.clipAgainstPlane(new MoveablePosition(0, dimension.height - 1, 0), Position.DOWN);
                    case 2 -> test.clipAgainstPlane(Position.EMPTY, Position.FORWARD);
                    case 3 -> test.clipAgainstPlane(new MoveablePosition(dimension.width - 1, 0, 0), Position.BACKWARD);
                    default -> throw new IllegalStateException("Unexpected value: " + i);
                };
                clip.iterate(queue::add);
            }
            numOfNewTriangles = queue.size();
        }

        return queue;
    }

    @Override
    public double midpoint() {
        return (pos1.getZ() + pos2.getZ() + pos3.getZ()) / 3;
    }

    public void drawSingle(Graphics graphics, Engine3D engine) {
        ColoredPolygon polygon = new ColoredPolygon(pos1, pos2, pos3);
        polygon.mode = FillMode.FILL;
        polygon.color = color(pos1.normal(pos2, pos3).dotProduct(engine.getLightDirection()));
        polygon.draw(graphics, engine);
    }


    @Override
    public void draw(Graphics graphics, Engine3D engine) {
        for (Triangle triangle : buildBounds(engine)) {
            triangle.drawSingle(graphics, engine);
        }
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

    public Triangle prepareProjection(Engine3D engine) {
        return as(engine, Engine3D::prepareProjection);
    }

    public Triangle mapByCamera(Engine3D engine) {
        return as(engine, Engine3D::mapByCamera);
    }

    public Triangle completeProjection(Engine3D engine) {
        return as(engine, Engine3D::completeProjection);
    }

    public Triangle rotate(Engine3D engine) {
        return as(engine, Engine3D::rotate);
    }

    public Triangle offset(Engine3D engine) {
        return as(engine, Engine3D::offset);
    }

    public Triangle projectOntoScreen(Engine3D engine) {
        return as(engine, Engine3D::projectOntoScreen);
    }

    public Triangle invert(Engine3D engine) {
        return as(engine, Engine3D::invert);
    }

    public Triangle scale(Engine3D engine) {
        return as(engine, Engine3D::scale);
    }

    public Triangle as(Engine3D engine, BiFunction<Engine3D, Position, Position> as) {
        return new Triangle(
                as.apply(engine, pos1),
                as.apply(engine, pos2),
                as.apply(engine, pos3)
        );
    }

    @Override
    public void prepareRaster(Engine3D engine) {
        Position pos2d1 = engine.prepareProjection(pos1);
        Position pos2d2 = engine.prepareProjection(pos2);
        Position pos2d3 = engine.prepareProjection(pos3);

        Position normalVector = pos2d1.normal(pos2d2, pos2d3);
        Position cameraPosition = engine.getCamera().getPosition();
        Position cameraLine = pos2d1.subtract(cameraPosition);

        if (normalVector.dotProduct(cameraLine) >= 0) return; // do not load triangle if unseeable
        /*int index = 0;
        for (int i = 0; i < Engine3D.axis.getTriangles().length; i++) {
            if (Engine3D.axis.getTriangles()[i] == this) {
                index = i;
                break;
            }
        }
        System.out.println(index);*/

        pos2d1 = engine.mapByCamera(pos2d1);
        pos2d2 = engine.mapByCamera(pos2d2);
        pos2d3 = engine.mapByCamera(pos2d3);

        ClipInfo clip = clipAgainstPlane(new MoveablePosition(0, 0, 0.1), Position.RIGHT, pos2d1, pos2d2, pos2d3);
        clip.iterate(triangle -> {
            Position pos1 = engine.projectAndScale(triangle.pos1);
            Position pos2 = engine.projectAndScale(triangle.pos2);
            Position pos3 = engine.projectAndScale(triangle.pos3);

            /*ColoredPolygon polygon = new ColoredPolygon();
            polygon.mode = FillMode.FILL;
            polygon.load(pos1, pos2, pos3);
            polygon.color = color(lightDot);
            engine.queue(polygon);*/
            engine.queue(new Triangle(pos1, pos2, pos3));
        });
    }

    public ClipInfo clip() {
        return clipAgainstPlane(new MoveablePosition(0, 0, 0.1), Position.RIGHT, pos1, pos2, pos3);
    }

    /**
     * lighting
     */
    private Color color(double dotProduct) {return Color.WHITE;
        //int rgb = Math.max((int) (dotProduct * 80), 255);
        //return new Color(rgb, rgb, rgb);
        /*int index = 0;
        Triangle[] triangles = Engine3D.axis.getTriangles();
        for (int i = 0; i < triangles.length; i++) {
            if (triangles[i] == this) {
                index = i;
                break;
            }
        }
        return switch (index % 11) {
            case 0 -> Color.BLACK;
            case 1 -> Color.CYAN;
            case 2 -> Color.GREEN;
            case 3 -> Color.LIGHT_GRAY;
            case 4 -> Color.MAGENTA;
            case 5 -> Color.ORANGE;
            case 6 -> Color.PINK;
            case 7 -> Color.RED;
            case 8 -> Color.WHITE;
            case 9 -> Color.YELLOW;
            case 10 -> Color.BLUE;
            default -> new Color(0, 0, 0, 0);
        };*/
    }

    // javidx9 part 3 3d engine

    public MoveablePosition intersectPlane(Position plane1, Position plane2, Position start, Position end) {
        double planeDot = (plane2 = plane2.normalize()).dotProduct(plane1); // possible bug, is negative in javidx9's code but is only used as a negative??? so it becomes positive???
        double startDot = start.dotProduct(plane2);
        double endDot = end.dotProduct(plane2);
        double value = (planeDot - startDot) / (endDot - startDot);
        Position startToEnd = end.subtract(start);
        Position intersectingLine = startToEnd.multiply(value);
        return start.add(intersectingLine);
    }

    public ClipInfo clipAgainstPlane(Position plane1, Position plane2) {
        return clipAgainstPlane(plane1, plane2, pos1, pos2, pos3);
    }

    public static void main(String[] args) {
        ClipInfo info = new Triangle(1, 1, 1, 0, 0, 0, -1, -1, -1).clipAgainstPlane(Position.EMPTY, Position.UP);
        System.out.println(info.triangle1);
        System.out.println(info.triangle2);
        ClipInfo info1 = new Triangle(1, 1, 1, 0, 0, 0, -1, -1, -1).clipAgainstPlane(Position.EMPTY, Position.FORWARD);
        System.out.println(info1.triangle1);
        System.out.println(info1.triangle2);
    }

    public ClipInfo clipAgainstPlane(Position plane1, Position plane2, Position pos1, Position pos2, Position pos3) {
        //System.out.println("ClipAgainstPlane");
        plane2 = plane2.normalize();

        double distance1 = shortestDistance(plane1, plane2, pos1);
        double distance2 = shortestDistance(plane1, plane2, pos2);
        double distance3 = shortestDistance(plane1, plane2, pos3);

        //System.out.println(distance1);
        //System.out.println(distance2);
        //System.out.println(distance3);

        Position[] insides = new Position[3];
        int insideIndex = 0;
        Position[] outsides = new Position[3];
        int outsideIndex = 0;

        if (distance1 >= 0) insides[insideIndex++] = pos1;
        else outsides[outsideIndex++] = pos1;
        if (distance2 >= 0) insides[insideIndex++] = pos2;
        else outsides[outsideIndex++] = pos2;
        if (distance3 >= 0) insides[insideIndex++] = pos3;
        else outsides[outsideIndex++] = pos3;

        //System.out.println(insideIndex);
        //System.out.println(outsideIndex);

        if (insideIndex == 0) {
            return ClipInfo.empty();
        }

        if (insideIndex == 3) {
            return ClipInfo.one(this);
        }

        if (insideIndex == 1) {
            return ClipInfo.one(new Triangle(
                    insides[0],
                    intersectPlane(plane1, plane2, insides[0], outsides[0]),
                    intersectPlane(plane1, plane2, insides[0], outsides[1])
            ));
        }

        Position posZ = intersectPlane(plane1, plane2, insides[0], outsides[0]);

        return ClipInfo.two(
                new Triangle(insides[0], insides[1], posZ),
                new Triangle(insides[1], posZ,
                        intersectPlane(plane1, plane2, insides[1], outsides[0]))
        );
    }

    public double shortestDistance(Position plane1, Position plane2, Position position) {
        //position = position.normalize(); // possible bug, normalized for no reason in javidx9's code, normalized but not saved to variable, may cause issues if was meant to happen
        return plane2.getX() * position.getX() + plane2.getY() * position.getY() + plane2.getZ() * position.getZ() - plane2.dotProduct(plane1);
    }

    //


    @Override
    public String toString() {
        return "(" +
                pos1 +
                ", " + pos2 +
                ", " + pos3 +
                ')';
    }
}