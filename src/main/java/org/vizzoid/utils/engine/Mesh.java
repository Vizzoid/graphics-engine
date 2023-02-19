package org.vizzoid.utils.engine;

import org.vizzoid.utils.position.MoveablePosition;
import org.vizzoid.utils.position.Position;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Mesh is an object composed of several triangles that can form a cube, pyramid, etc.
 * Draws all triangles into to avoid adding to Engine3D as whole and provides mutability.
 */
public class Mesh implements Object3D {

    private final Triangle[] triangles;

    public Mesh(Triangle... triangles) {
        this.triangles = triangles;
    }

    public Triangle[] getTriangles() {
        return triangles;
    }

    @Override
    public void prepareRaster(Engine3D engine) {
        for (Triangle triangle : triangles) {
            triangle.prepareRaster(engine);
        }
    }

    /**
     * Creates mesh from Blender file
     */
    public static Mesh load(String filename) {
        try (BufferedReader stream = new BufferedReader(new FileReader(filename))) {
            List<Position> vertexes = new ArrayList<>();
            List<Triangle> triangles = new ArrayList<>();
            for (String line; (line = stream.readLine()) != null; ) {
                switch (line.charAt(0)) {
                    case 'v' -> {
                        Position vertex = positionFromString(line);
                        vertexes.add(vertex);
                    }
                    case 'f' -> {
                        Position position = positionFromString(line);
                        triangles.add(
                                new Triangle(
                                        vertexes.get((int) (position.getX() - 1)),
                                        vertexes.get((int) (position.getY() - 1)),
                                        vertexes.get((int) (position.getZ() - 1))));
                    }
                }
            }
            return new Mesh(triangles.toArray(Triangle[]::new));
        } catch (Exception e) {
            NullPointerException nullException = new NullPointerException(filename);
            nullException.initCause(e);
            throw nullException;
        }
    }

    private static Position positionFromString(String s) {
        String[] arr = s.split(" ");
        return new MoveablePosition(
                Double.parseDouble(arr[1]),
                Double.parseDouble(arr[2]),
                Double.parseDouble(arr[3])
        );
    }

}
