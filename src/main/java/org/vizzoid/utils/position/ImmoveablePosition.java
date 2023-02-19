package org.vizzoid.utils.position;

public class ImmoveablePosition implements Position {

    private double x, y, z;

    public ImmoveablePosition() {

    }

    public ImmoveablePosition(double x, double y, double z) {
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

    @Override
    public MoveablePosition moveable() {
        return new MoveablePosition(x, y, z);
    }

    @Override
    public ImmoveablePosition immoveable() {
        return this;
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
