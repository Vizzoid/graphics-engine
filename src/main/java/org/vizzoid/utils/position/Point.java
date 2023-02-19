package org.vizzoid.utils.position;

/**
 * 2D immutable position template
 */
public interface Point {

    Point UP = new ImmoveablePoint(0, 1);
    Point DOWN = new ImmoveablePoint(0, -1);
    Point RIGHT = new ImmoveablePoint(1, 0);
    Point LEFT = new ImmoveablePoint(-1, 0);

    double getX();

    double getY();

    MoveablePoint moveable();

    ImmoveablePoint immoveable();

    default MoveablePoint line(Point position) {
        MoveablePoint line = new MoveablePoint();
        line.setX(position.getX() - getX());
        line.setY(position.getY() - getY());

        return line;
    }

    default MoveablePoint normalize() {
        MoveablePoint position = moveable();
        double length = length();

        position.setX(position.getX() / length);
        position.setY(position.getY() / length);

        return position;
    }

    default double lengthSqr() {
        double x = getX();
        double y = getY();

        return (x * x + y * y);
    }

    default double length() {
        return Math.sqrt(lengthSqr());
    }

    default double distanceSqr(Point point) {
        double xDiff = point.getX() - getX();
        double yDiff = point.getY() - getY();
        return (xDiff * xDiff) + (yDiff * yDiff);
    }

    default double distance(Point point) {
        return Math.sqrt(distanceSqr(point));
    }

    default double dotProduct(Point position) {
        return getX() * position.getX() +
                getY() * position.getY();
    }

    // movement utility

    default MoveablePoint add(Point position1) {
        MoveablePoint position = new MoveablePoint();
        position.setX(getX() + position1.getX());
        position.setY(getY() + position1.getY());
        return position;
    }

    default MoveablePoint subtract(Point position1) {
        MoveablePoint position = new MoveablePoint();
        position.setX(getX() - position1.getX());
        position.setY(getY() - position1.getY());
        return position;
    }

    default MoveablePoint multiply(Point position1) {
        MoveablePoint position = new MoveablePoint();
        position.setX(getX() * position1.getX());
        position.setY(getY() * position1.getY());
        return position;
    }

    default MoveablePoint divide(Point position1) {
        MoveablePoint position = new MoveablePoint();
        position.setX(getX() / position1.getX());
        position.setY(getY() / position1.getY());
        return position;
    }

}
