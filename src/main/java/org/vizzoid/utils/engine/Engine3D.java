package org.vizzoid.utils.engine;

import org.vizzoid.utils.position.MoveablePosition;
import org.vizzoid.utils.position.Position;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Engine used as screen from converting objects in a world onto the plane (screen).
 * All position altering methods must take in a position as the first input and output a position.
 * If altering xyz coordinates, must return MoveablePosition and use position.moveable() to convert
 * the position interface accepted into a mutable state. This provides it easily useable for mutation
 * into 3d but also easy use outside of inside Engine3D.
 * <p>
 * Modeled after javidx9's 3d engine
 */
@SuppressWarnings("UnusedReturnValue")
public class Engine3D {

    private static final double MOVE = 0.1;
    public static Mesh axis;

    private final Camera camera;
    private final List<Object3D> object3DS = new ArrayList<>();
    private final Position lightDirection = new MoveablePosition(0, 0, -1);//.normalize();
    private final List<Drawable> toRaster = new ArrayList<>();
    private final DefaultEngine internalEngine;
    private final WASDKeyListener wasdKeyListener = new WASDKeyListener(new WASDListener() {
        @Override
        public boolean onW(long missedTime) {
            camera.getPosition().addSet(camera.getLookDirection().multiply(MOVE * missedTime));
            return true;
        }

        @Override
        public boolean onA(long missedTime) {
            camera.getPosition().moveX(missedTime * -MOVE);
            return true;
        }

        @Override
        public boolean onS(long missedTime) {
            camera.getPosition().subtractSet(camera.getLookDirection().multiply(MOVE * missedTime));
            return true;
        }

        @Override
        public boolean onD(long missedTime) {
            camera.getPosition().moveX(missedTime * MOVE);
            return true;
        }
    });
    private final ArrowKeyListener arrowKeyListener = new ArrowKeyListener(new ArrowListener() {
        @Override
        public boolean onUp(long missedTime) {
            camera.getPosition().moveY(missedTime * MOVE);
            return true;
        }

        @Override
        public boolean onRight(long missedTime) {
            camera.setYaw(camera.getYaw() + (MOVE * missedTime));
            return true;
        }

        @Override
        public boolean onLeft(long missedTime) {
            camera.setYaw(camera.getYaw() - (MOVE * missedTime));
            return true;
        }

        @Override
        public boolean onDown(long missedTime) {
            camera.getPosition().moveY(missedTime * -MOVE);
            return true;
        }
    });
    private final Sleeper tickSleeper = new Sleeper();

    public Engine3D(Camera camera) {
        Triangle triangle1;
        this.camera = camera;
        internalEngine = new DefaultEngine();
        internalEngine.setDimension(new Dimension(256, 240));
        internalEngine.setPainter(this::paintComponent);
        internalEngine.addKeyListener(wasdKeyListener);
        internalEngine.addKeyListener(arrowKeyListener);
        internalEngine.getSleeper().setMaxFps(60);
/*
        Mesh cube = new Mesh(
                new Triangle(
                        0, 0, 0,
                        0, 1, 0,
                        1, 1, 0),
                new Triangle(
                        0, 0, 0,
                        1, 1, 0,
                        1, 0, 0),

                new Triangle(
                        1, 0, 0,
                        1, 1, 0,
                        1, 1, 1),
                new Triangle(
                        1, 0, 0,
                        1, 1, 1,
                        1, 0, 1),

                new Triangle(
                        1, 0, 1,
                        1, 1, 1,
                        0, 1, 1),
                new Triangle(
                        1, 0, 1,
                        0, 1, 1,
                        0, 0, 1),

                new Triangle(
                        0, 0, 1,
                        0, 1, 1,
                        0, 1, 0),
                new Triangle(
                        0, 0, 1,
                        0, 1, 0,
                        0, 0, 0),

                new Triangle(
                        0, 1, 0,
                        0, 1, 1,
                        1, 1, 1),
                new Triangle(
                        0, 1, 0,
                        1, 1, 1,
                        1, 1, 0),

                new Triangle(
                        1, 0, 1,
                        0, 0, 1,
                        0, 0, 0),
                new Triangle(
                        1, 0, 1,
                        0, 0, 0,
                        1, 0, 0));*/
        // mesh is cube test

        //object3DS.add(cube);
        //object3DS.addAll(Arrays.asList(cube.getTriangles()));
        //object3DS.add(Mesh.load("D:\\Users\\vtyso\\Downloads\\New Text Document.txt"));
        Engine3D.axis = Mesh.load("D:\\Users\\vtyso\\Downloads\\New Text Document (2).txt");
        object3DS.add(axis);
        triangle1 = axis.getTriangles()[118];
        /*20, 22, 23, 26, 27, 29, 45, 49, 50, 67, 70, 71, 73, 77, 87, 93, 95, 96, 99, 100, 102, 106, 110, 111, 114, 117, 118, 120, 124, 134*/

        camera.reset();
        System.out.println(triangle1);
        System.out.println("rotate");
        System.out.println(triangle1 = triangle1.rotate(this)); // slight change? z became negative
        System.out.println("offset");
        System.out.println(triangle1 = triangle1.offset(this)); // z dropped by 0.5
        System.out.println("mapByCamera");
        System.out.println(triangle1 = triangle1.mapByCamera(this));

        ClipInfo clip = triangle1.clip();
        Triangle triangle0 = triangle1;
        clip.iterate(t -> {
            System.out.println("One of clip:");
            Triangle triangle2 = triangle0;
            System.out.println("projection");
            System.out.println(triangle2 = triangle2.projectOntoScreen(this)); // x values vary 0-0.2? y and z values vary 0-0.02
            System.out.println("invert");
            System.out.println(triangle2 = triangle2.invert(this));
            System.out.println("scale");
            System.out.println(triangle2 = triangle2.scale(this)); // variations are multiplied, problem??

            for (Triangle triangle : triangle2.buildBounds(this)) {
                System.out.println("One of bound:");
                System.out.println(triangle);
            }
        });

        object3DS.add(axis);

        ExecutorService tickThread = Executors.newCachedThreadPool();
        tickThread.submit(() -> {
            while (true) {
                tickSleeper.setMaxFps(60);
                long missedTime = tickSleeper.pullMissedTime();

                try {
                    tick(missedTime);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                tickSleeper.sleep();
            }
        });
    }

    @SuppressWarnings("ForLoopReplaceableByForEach")
    protected void paintComponent(Graphics g, long missedTime) {
        if (true) return;
        camera.reset();

        for (int i = 0, object3DSSize = object3DS.size(); i < object3DSSize; i++) {
            Object3D object3D = object3DS.get(i);
            object3D.prepareRaster(this);
        }
        toRaster.sort(Comparator.comparingDouble(Drawable::midpoint));
        for (int i = 0; i < toRaster.size(); i++) {
            Drawable polygon = toRaster.get(i);
            polygon.draw(g, this);
        }
        toRaster.clear();

        g.setColor(Color.WHITE);
        g.drawString(camera.getPosition().toString(), 0, 10);
    }

    protected void tick(long missedTime) {
        //theta += 0.001;
        wasdKeyListener.tick(missedTime);
        arrowKeyListener.tick(missedTime);
    }

    double theta = 2 * Math.PI;

    public void queue(Drawable polygon) {
        toRaster.add(polygon);
    }

    /**
     * Mutates position in 3d world into the 2d screen through projection, accounting for rotation, offset and scaled.
     */
    public MoveablePosition mutate3dTo2d(Position position0) {
        MoveablePosition position = position0.moveable();
        prepareProjection(position);
        completeProjection(position0);

        return position;
    }

    public MoveablePosition prepareProjection(Position position0) {
        MoveablePosition position = position0.moveable();
        //rotateZ(position, theta);
        //rotateX(position, theta * 0.5);
        rotate(position, theta);
        offset(position);

        return position;
    }

    public MoveablePosition mapByCamera(Position position0) {
        return camera.worldToView(position0);
    }

    public MoveablePosition completeProjection(Position position0) {
        MoveablePosition position = position0.moveable();
        mapByCamera(position);
        projectAndScale(position);

        return position;
    }

    public MoveablePosition projectAndScale(Position position0) {
        MoveablePosition position = position0.moveable();
        projectOntoScreen(position);
        invert(position);
        scale(position);

        return position;
    }

    /**
     * Rotation matrix, moves position akin to z rotation depending on theta
     */
    public MoveablePosition rotateZ(Position position0, double theta) {
        MoveablePosition position = position0.moveable();
        double x = position.getX();
        double y = position.getY();
        double sin = Math.sin(theta);
        double cos = Math.cos(theta);

        position.setX((x * cos) + (y * -sin));
        position.setY((x * sin) + (y * cos));

        return position;
    }

    /**
     * Rotation matrix, moves position akin to x rotation depending on theta
     */
    public MoveablePosition rotateY(Position position0, double theta) {
        MoveablePosition position = position0.moveable();
        double x = position.getX();
        double z = position.getZ();
        double sin = Math.sin(theta);
        double cos = Math.cos(theta);

        position.setX((x * cos) + (z * sin));
        position.setZ((x * -sin) + (z * cos));

        return position;
    }

    /**
     * Rotation matrix, moves position akin to x rotation depending on theta
     */
    public MoveablePosition rotateX(Position position0, double theta) {
        MoveablePosition position = position0.moveable();
        double y = position.getY();
        double z = position.getZ();
        double sin = Math.sin(theta);
        double cos = Math.cos(theta);

        position.setY((y * cos) + (z * -sin));
        position.setZ((y * sin) + (z * cos));

        return position;
    }

    /**
     * rotateZ and rotateX
     *
     * @param position0
     */
    public MoveablePosition rotate(Position position0) {
        return rotate(position0, theta);
    }

    /**
     * rotateZ and rotateX
     */
    public MoveablePosition rotate(Position position0, double theta) {
        MoveablePosition position = position0.moveable();
        double x = position.getX();
        double y = position.getY();
        double z = position.getZ();

        double sin = Math.sin(theta);
        double cos = Math.cos(theta);

        double sinHalf = Math.sin(theta * 0.5);
        double cosHalf = Math.cos(theta * 0.5);

        double rotY = (x * sinHalf) + (y * cosHalf);

        // invert because it somehow inverts here?? testing shows negative values positive and positive values negative????
        position.setX(((x * cosHalf) + (y * -sinHalf)) * -1);
        position.setY(((rotY * cos) + (z * -sin)) * -1);
        position.setZ(((rotY * sin) + (z * cos)));

        return position;
    }

    /**
     * Offset z onto screen
     */
    public MoveablePosition offset(Position position0) {
        MoveablePosition position = position0.moveable();
        position.moveZ(5);

        return position;
    }

    /**
     * Projection matrix, projects the 3d point onto 2d plane
     */
    public MoveablePosition projectOntoScreen(Position position0) {
        return camera.project(position0, internalEngine.getAspectRatio());
    }

    public MoveablePosition invert(Position position0) {
        MoveablePosition position = position0.moveable();
        double z = position.getZ();
        position.multiplySet(-1);
        position.setZ(z);

        return position;
    }

    /**
     * Scale x and y by center to view, normalize onto middle of screen being 0, 0
     */
    public MoveablePosition scale(Position position0) {
        MoveablePosition position = position0.moveable();
        position.setX((position.getX() + 1) * internalEngine.getCenter().width);
        position.setY((position.getY() + 1) * internalEngine.getCenter().height);

        return position;
    }

    public Camera getCamera() {
        return camera;
    }

    public Position getLightDirection() {
        return lightDirection;
    }

    public DefaultEngine getInternalEngine() {
        return internalEngine;
    }
}
