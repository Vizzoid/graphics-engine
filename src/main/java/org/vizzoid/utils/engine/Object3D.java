package org.vizzoid.utils.engine;

/**
 * Represents any object in 3d space that can be added to drawing engine.
 * Useful functions for drawing are:
 * <p> - engine.mutate2dTo3d(position)
 */
public interface Object3D {

    void prepareRaster(Engine3D engine);

}