package org.vizzoid.utils.position;

/**
 * 2D position that can change without creating new position object with helper functions
 */
public class MoveablePoint implements Point {

    private double x, y;

    public MoveablePoint() {

    }

    public MoveablePoint(double x, double y) {
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

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void set(double x, double y) {
        setX(x);
        setY(y);
    }

    public void moveX(double x) {
        setX(this.x + x);
    }

    public void moveY(double y) {
        setY(this.y + y);
    }

    public void move(double x, double y) {
        moveX(x);
        moveY(y);
    }

    @Override
    public MoveablePoint moveable() {
        return this;
    }

    @Override
    public ImmoveablePoint immoveable() {
        return new ImmoveablePoint(x, y);
    }

    // movement utility (changes this object instead of creating new one

    public void addSet(Point position1) {
        setX(getX() + position1.getX());
        setY(getY() + position1.getY());
    }

    public void subtractSet(Point position1) {
        setX(getX() - position1.getX());
        setY(getY() - position1.getY());
    }

    public void multiplySet(Point position1) {
        setX(getX() * position1.getX());
        setY(getY() * position1.getY());
    }

    public void divideSet(Point position1) {
        setX(getX() / position1.getX());
        setY(getY() / position1.getY());
    }

    @Override
    public String toString() {
        return "MoveablePoint{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
