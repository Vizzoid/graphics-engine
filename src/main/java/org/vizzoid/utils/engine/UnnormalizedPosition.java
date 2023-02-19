package org.vizzoid.utils.engine;

import org.vizzoid.utils.position.MoveablePosition;
import org.vizzoid.utils.position.Position;

public class UnnormalizedPosition extends MoveablePosition {

    private double length = 1;

    public UnnormalizedPosition() {

    }

    public UnnormalizedPosition(Position position) {
        this(position.getX(), position.getY(), position.getZ());
    }

    public UnnormalizedPosition(double x, double y, double z) {
        super(x, y, z);
    }

    public static UnnormalizedPosition unnormalized(Position position) {
        if (position instanceof UnnormalizedPosition unnormalized) return unnormalized;
        UnnormalizedPosition position1 = new UnnormalizedPosition();
        position1.length(position.length());
        return position1;
    }

    @Override
    public double length() {
        return length;
    }

    public void length(double length) {
        this.length = length;
    }

    @Override
    public MoveablePosition normalize() {
        super.normalize();
        length = 1;
        return this;
    }
}
