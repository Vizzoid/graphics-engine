package org.vizzoid.utils.position;

/**
 * 3D position that can change without creating new position object with helper functions
 */
public class MoveablePosition implements Position {

    private double x, y, z;

    public MoveablePosition() {
    }

    public MoveablePosition(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public double getZ() {
        return z;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void set(double x, double y, double z) {
        setX(x);
        setY(y);
        setZ(z);
    }

    public void moveX(double x) {
        setX(this.x + x);
    }

    public void moveY(double y) {
        setY(this.y + y);
    }

    public void moveZ(double z) {
        setZ(this.z + z);
    }

    public void move(double x, double y, double z) {
        moveX(x);
        moveY(y);
        moveZ(z);
    }

    @Override
    public MoveablePosition moveable() {
        return this;
    }

    @Override
    public ImmoveablePosition immoveable() {
        return new ImmoveablePosition(x, y, z);
    }

    // movement utility (changes this object instead of creating new one

    public void addSet(Position position1) {
        setX(getX() + position1.getX());
        setY(getY() + position1.getY());
        setZ(getZ() + position1.getZ());
    }

    public void subtractSet(Position position1) {
        setX(getX() - position1.getX());
        setY(getY() - position1.getY());
        setZ(getZ() - position1.getZ());
    }

    public void multiplySet(Position position1) {
        setX(getX() * position1.getX());
        setY(getY() * position1.getY());
        setZ(getZ() * position1.getZ());
    }

    public void divideSet(Position position1) {
        setX(getX() / position1.getX());
        setY(getY() / position1.getY());
        setZ(getZ() / position1.getZ());
    }

    public void addSet(double factor) {
        setX(getX() + factor);
        setY(getY() + factor);
        setZ(getZ() + factor);
    }

    public void subtractSet(double factor) {
        setX(getX() - factor);
        setY(getY() - factor);
        setZ(getZ() - factor);
    }

    public void multiplySet(double factor) {
        setX(getX() * factor);
        setY(getY() * factor);
        setZ(getZ() * factor);
    }

    public void divideSet(double factor) {
        setX(getX() / factor);
        setY(getY() / factor);
        setZ(getZ() / factor);
    }

    @Override
    public String toString() {
        return "(" + truncate(x) +
                ", " + truncate(y) +
                ", " + truncate(z) +
                ')';
    }

    private static double truncate(double d) {
        return ((int) (d * 1000)) / 1000d;
    }

}
