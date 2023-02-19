package org.vizzoid.utils.engine;

import org.vizzoid.utils.position.MoveablePosition;
import org.vizzoid.utils.position.Position;

import static org.vizzoid.utils.position.Position.UP;

/**
 * Camera represents the user's view and from where they can see. It contains multiple settings such as rendering distance, zNear, and fov, but it also includes
 * useful content such as scaling factor and projection calculations which are calculated beforehand to be used repeatedly without unnecessary computation power
 */
public class Camera {

    // precached calculations
    private double scalingFactor;
    private double projectionMultiplier;
    private double projectionSubtractive;
    //
    private double zNear;

    private double renderingDistance; // zFar
    private double fov;
    private double yaw;

    private final MoveablePosition position = new MoveablePosition();
    private final MoveablePosition target = new MoveablePosition(0, 0, 1);
    private final MoveablePosition lookDirection = new MoveablePosition(0, 0, 1);

    private Position forward;
    private Position up;
    private Position right;
    private Position length;

    public Camera() {
        this.zNear = 0.1;
        this.renderingDistance = 1000;
        this.fov = 90;
    }

    public double getFov() {
        return fov;
    }

    /**
     * Sets fov of camera, also calculates scaling factor before hand and stores it to be used without computing during paint process
     */
    public void setFov(double fov) {
        this.fov = fov;
    }

    public double getScalingFactor() {
        return scalingFactor;
    }

    public double getRenderingDistance() {
        return renderingDistance;
    }

    public double getZNear() {
        return zNear;
    }

    public void setRenderingDistance(double renderingDistance) {
        this.renderingDistance = renderingDistance;
    }

    public void setZNear(double zNear) {
        this.zNear = zNear;
    }

    public double getProjectionMultiplier() {
        return projectionMultiplier;
    }

    public double getProjectionSubtractive() {
        return projectionSubtractive;
    }

    public MoveablePosition getPosition() {
        return position;
    }

    public MoveablePosition getTarget() {
        return target;
    }

    /**
     * Reloads projection calculations which are used to project 3d point onto 2d plane (screen)
     */
    public void resetProjection() {
        // scaling factor is used so as the fov increases the point moves closer to off screen and vice-versa
        this.scalingFactor = 1 / Math.tan(Math.toRadians(fov * 0.5)); // cotangent
        this.projectionMultiplier = (renderingDistance / (renderingDistance - zNear));
        this.projectionSubtractive = (zNear * -projectionMultiplier);

        // calculations of z
        // z * projectionMultiplier - projectionSubtractive
        // (z * (renderingDistance / (renderingDistance - zNear))) - (zNear * -(renderingDistance / (renderingDistance - zNear)))
        // (z * (zFar / (zFar - zNear))) - (zNear * -(zFar / (zFar - zNear)))
    }

    public void resetDirections() {
        this.forward = target.subtract(position).normalize(); // possible bug? wrong subtraction?

        double forwardDotProduct = UP.dotProduct(forward);
        Position forwardMultiplied = forward.multiply(forwardDotProduct);
        this.up = UP.subtract(forwardMultiplied).normalize();
        this.right = up.crossProduct(forward);

        // length
        this.length = new MoveablePosition(
                -(position.getX() * right.getX() +      position.getY() * right.getY() +    position.getZ() * right.getZ()),
                -(position.getX() * up.getX() +         position.getY() * up.getY() +       position.getZ() * up.getZ()),
                -(position.getX() * forward.getX() +    position.getY() * forward.getY() +  position.getZ() * forward.getZ())
        );
    }

    public void resetTarget() {
        lookDirection.set(-Math.sin(yaw), 0, Math.cos(yaw));

        Position add = position.add(lookDirection);
        target.set(add.getX(), add.getY(), add.getZ());
        //target.set(position.getX(), position.getY() - 1, position.getZ());
    }

    public void reset() {
        resetTarget();
        resetDirections();
        resetProjection();
    }

    public MoveablePosition worldToView(Position position0) {
        MoveablePosition position = position0.moveable();
        double x = position.getX();
        double y = position.getY();
        double z = position.getZ();

        position.setX(x * right.getX() +    y * right.getY() +      z * right.getZ() +      this.length.getX());
        position.setY(x * up.getX() +       y * up.getY() +         z * up.getZ() +         this.length.getY());
        position.setZ(x * forward.getX() +  y * forward.getY() +    z * forward.getZ() +    this.length.getZ());
        return position;
    }

    public MoveablePosition project(Position position0, double aspectRatio) {
        MoveablePosition position = position0.moveable();
        double x = position.getX();
        double y = position.getY();
        double z = position.getZ();

        double x2D = (scalingFactor * x * aspectRatio);
        double y2D = (scalingFactor * y);
        double z2D = (projectionMultiplier * z + projectionSubtractive);

        if (z == 0) z = 1;
        position.setX(x2D / z);
        position.setY(y2D / z);
        position.setZ(z2D / z);
        return position;
    }

    public double getYaw() {
        return yaw;
    }

    public void setYaw(double yaw) {
        this.yaw = yaw;
    }

    public MoveablePosition getLookDirection() {
        return lookDirection;
    }
}