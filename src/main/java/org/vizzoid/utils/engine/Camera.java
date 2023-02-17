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
    private boolean scalingFactorSet = false;
    private boolean projectionMultiplierSet = false;
    private boolean projectionSubtractiveSet = false;
    //
    private double zNear;

    private double renderingDistance; // zFar
    private double fov;
    private double yaw;

    private final MoveablePosition position = new MoveablePosition() {
        public void setX(double x) {
            super.setX(x);
            resetDirections();
        }
        public void setY(double y) {
            super.setY(y);
            resetDirections();
        }
        public void setZ(double z) {
            super.setZ(z);
            resetDirections();
        }
    };
    private final MoveablePosition target = new MoveablePosition(0, 0, 1) {
        public void setX(double x) {
            super.setX(x);
            resetDirections();
        }
        public void setY(double y) {
            super.setY(y);
            resetDirections();
        }
        public void setZ(double z) {
            super.setZ(z);
            resetDirections();
        }
    };

    private Position forward;
    private Position up;
    private Position right;
    private Position length;

    public Camera() {
        this.zNear = 0.1;
        this.renderingDistance = 1000;
        resetProjection();
        setFov(90);
        resetDirections();
    }

    public void loadTarget() {
        target.setX(Math.sin(yaw));
        target.setZ(Math.cos(yaw));

        Position add = position.add(target);
        target.set(add.getX(), add.getY(), add.getZ());
    }

    public double getFov() {
        return fov;
    }

    /**
     * Sets fov of camera, also calculates scaling factor before hand and stores it to be used without computing during paint process
     */
    public void setFov(double fov) {
        this.fov = fov;
        resetProjection();
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
        resetProjection();
    }

    public void setZNear(double zNear) {
        this.zNear = zNear;
        resetProjection();
    }

    /**
     * Reloads projection calculations which are used to project 3d point onto 2d plane (screen)
     */
    public void resetProjection() {
        scalingFactorSet = false;
        projectionMultiplierSet = false;
        projectionSubtractiveSet = false;
    }

    public void loadProjection() {
        if (scalingFactorSet && projectionMultiplierSet && projectionSubtractiveSet) return;
        loadTarget();

        // scaling factor is used so as the fov increases the point moves closer to off screen and vice-versa
        this.scalingFactor = 1 / Math.tan(Math.toRadians(fov) / 2); // cotangent
        this.projectionMultiplier = (renderingDistance / (renderingDistance - zNear));
        this.projectionSubtractive = (zNear * -projectionMultiplier);

        scalingFactorSet = true;
        projectionMultiplierSet = true;
        projectionSubtractiveSet = true;

        // calculations of z
        // z * projectionMultiplier - projectionSubtractive
        // (z * (renderingDistance / (renderingDistance - zNear))) - (zNear * -(renderingDistance / (renderingDistance - zNear)))
        // (z * (zFar / (zFar - zNear))) - (zNear * -(zFar / (zFar - zNear)))
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

    public void resetDirections() {
        this.forward = null;
        this.up = null;
        this.right = null;
        this.length = null;
    }

    public void loadDirections() {
        if (forward != null && up != null && right != null && length != null) return;

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

    public MoveablePosition worldToView(Position position0) {
        loadDirections();

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
        loadProjection();

        MoveablePosition position = position0.moveable();
        double x = position.getX();
        double y = position.getY();
        double z = position.getZ();

        double x2D = (scalingFactor * x * aspectRatio);
        double y2D = (scalingFactor * y);
        double z2D = (projectionMultiplier * z - projectionSubtractive);

        if (z == 0) z = 1;
        position.setX(x2D / z);
        position.setY(y2D / z);
        position.setZ(z2D / z);
        return position;
    }

    /*

    vec3d Matrix_MultiplyVector(mat4x4 &m, vec3d &i)
    {
        vec3d v;
        v.x = i.x * newRight.x   + i.y * newRight.y   + i.z * newRight.z   + i.w * -(camera.x * newRight.x   + camera.y * newRight.y   + camera.z * newRight.z  );
        v.y = i.x * newUp.x      + i.y * newUp.y      + i.z * newUp.z	   + i.w * -(camera.x * newUp.x      + camera.y * newUp.y      + camera.z * newUp.z	    );
        v.z = i.x * newForward.x + i.y * newForward.y + i.z * newForward.z + i.w * -(camera.x * newForward.x + camera.y * newForward.y + camera.z * newForward.z);
        v.w = i.x * 0.0f         + i.y * 0.0f         + i.z * 0.0f         + i.w * 1.0f;
        return v;
    }

    mat4x4 Matrix_PointAt(vec3d &pos, vec3d &target, vec3d &up)
    {
        // Calculate new forward direction
        vec3d newForward = Vector_Sub(target, pos);
        newForward = Vector_Normalise(newForward);

        // Calculate new Up direction
        vec3d a = Vector_Mul(newForward, Vector_DotProduct(up, newForward));
        vec3d newUp = Vector_Sub(up, a);
        newUp = Vector_Normalise(newUp);

        // New Right direction is easy, its just cross product
        vec3d newRight = Vector_CrossProduct(newUp, newForward);

        // Construct Dimensioning and Translation Matrix
        mat4x4 matrix;
        matrix.m[0][0] = newRight.x;	matrix.m[0][1] = newRight.y;	matrix.m[0][2] = newRight.z;	matrix.m[0][3] = 0.0f;
        matrix.m[1][0] = newUp.x;		matrix.m[1][1] = newUp.y;		matrix.m[1][2] = newUp.z;		matrix.m[1][3] = 0.0f;
        matrix.m[2][0] = newForward.x;	matrix.m[2][1] = newForward.y;	matrix.m[2][2] = newForward.z;	matrix.m[2][3] = 0.0f;
        matrix.m[3][0] = pos.x;			matrix.m[3][1] = pos.y;			matrix.m[3][2] = pos.z;			matrix.m[3][3] = 1.0f;
        return matrix;

    }

    mat4x4 Matrix_QuickInverse(mat4x4 &m) // Only for Rotation/Translation Matrices
    {
        mat4x4 matrix;
        matrix.m[0][0] = newRight.x;
        matrix.m[0][1] = newUp.x;
        matrix.m[0][2] = newForward.x;
        matrix.m[0][3] = 0.0f;
        matrix.m[1][0] = newRight.y;
        matrix.m[1][1] = newUp.y;
        matrix.m[1][2] = newForward.y;
        matrix.m[1][3] = 0.0f;
        matrix.m[2][0] = newRight.z;
        matrix.m[2][1] = newUp.z;
        matrix.m[2][2] = newForward.z;
        matrix.m[2][3] = 0.0f;
        matrix.m[3][0] = -(camera.x * newRight.x   + camera.y * newRight.y   + camera.z * newRight.z  );
        matrix.m[3][1] = -(camera.x * newUp.x      + camera.y * newUp.y      + camera.z * newUp.z	  );
        matrix.m[3][2] = -(camera.x * newForward.x + camera.y * newForward.y + camera.z * newForward.z);
        matrix.m[3][3] = 1.0f;
        return matrix;
    }

    */
}