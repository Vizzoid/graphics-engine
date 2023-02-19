package org.vizzoid.utils.position;

public class ImmoveablePoint implements Point {

    private double x, y;

    public ImmoveablePoint() {

    }

    public ImmoveablePoint(double x, double y) {
        this.x = x;
        this.y = y;
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
    public MoveablePoint moveable() {
        return new MoveablePoint(x, y);
    }

    @Override
    public ImmoveablePoint immoveable() {
        return this;
    }

    @Override
    public String toString() {
        return "ImmoveablePoint{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
