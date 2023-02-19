package org.vizzoid.utils.position;

/**
 * 3D immutable position template
 */
public interface Position {

    Position UP = new ImmoveablePosition(0, 1, 0);
    Position DOWN = new ImmoveablePosition(0, -1, 0);
    Position FORWARD = new ImmoveablePosition(1, 0, 0);
    Position BACKWARD = new ImmoveablePosition(-1, 0, 0);
    Position RIGHT = new ImmoveablePosition(0, 0, 1);
    Position LEFT = new ImmoveablePosition(0, 0, -1);
    Position EMPTY = new ImmoveablePosition(0, 0, 0);

    double getX();

    double getY();

    double getZ();

    MoveablePosition moveable();

    ImmoveablePosition immoveable();

    default MoveablePosition normal(Position position1, Position position2) {
        Position line1 = position1.subtract(this);
        Position line2 = position2.subtract(this);
        MoveablePosition normal = line1.crossProduct(line2);

        return normal.normalize();
    }

    default MoveablePosition crossProduct(Position position) {
        MoveablePosition crossProduct = new MoveablePosition();
        crossProduct.setX(getY() * position.getZ() - getZ() * position.getY());
        crossProduct.setY(getZ() * position.getX() - getX() * position.getZ());
        crossProduct.setZ(getX() * position.getY() - getY() * position.getX());

        return crossProduct;
    }

    default MoveablePosition line(Position position) {
        return position.subtract(this);
    }

    default MoveablePosition normalize() {
        MoveablePosition position = moveable();
        double length = length();

        position.setX(position.getX() / length);
        position.setY(position.getY() / length);
        position.setZ(position.getZ() / length);

        return position;
    }

    default double length() {
        double x = getX();
        double y = getY();
        double z = getZ();

        return Math.sqrt(x * x + y * y + z * z);
    }

    default double dotProduct(Position position) {
        return getX() * position.getX() +
                getY() * position.getY() +
                getZ() * position.getZ();
    }

    // movement utility

    default MoveablePosition add(Position factor) {
        MoveablePosition position = new MoveablePosition(getX(), getY(), getZ());
        position.addSet(factor);
        return position;
    }

    default MoveablePosition subtract(Position factor) {
        MoveablePosition position = new MoveablePosition(getX(), getY(), getZ());
        position.subtractSet(factor);
        return position;
    }

    default MoveablePosition multiply(Position factor) {
        MoveablePosition position = new MoveablePosition(getX(), getY(), getZ());
        position.multiplySet(factor);
        return position;
    }

    default MoveablePosition divide(Position factor) {
        MoveablePosition position = new MoveablePosition(getX(), getY(), getZ());
        position.divideSet(factor);
        return position;
    }

    default MoveablePosition add(double factor) {
        MoveablePosition position = new MoveablePosition(getX(), getY(), getZ());
        position.addSet(factor);
        return position;
    }

    default MoveablePosition subtract(double factor) {
        MoveablePosition position = new MoveablePosition(getX(), getY(), getZ());
        position.subtractSet(factor);
        return position;
    }

    default MoveablePosition multiply(double factor) {
        MoveablePosition position = new MoveablePosition(getX(), getY(), getZ());
        position.multiplySet(factor);
        return position;
    }

    default MoveablePosition divide(double factor) {
        MoveablePosition position = new MoveablePosition(getX(), getY(), getZ());
        position.divideSet(factor);
        return position;
    }

}
