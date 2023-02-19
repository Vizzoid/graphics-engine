package org.vizzoid.utils.engine;

import org.nd4j.linalg.api.ops.custom.Tri;

import java.util.function.Consumer;

public class ClipInfo {

    private static final ClipInfo EMPTY = new ClipInfo(null, null);

    public final Triangle triangle1;
    public final Triangle triangle2;

    private ClipInfo(Triangle triangle1, Triangle triangle2) {
        this.triangle1 = triangle1;
        this.triangle2 = triangle2;
    }

    public void iterate(Consumer<Triangle> consumer) {

    }

    public static ClipInfo empty() {
        return EMPTY;
    }

    public static ClipInfo one(Triangle triangle) {
        return new ClipInfo(triangle, null) {
            @Override
            public void iterate(Consumer<Triangle> consumer) {
                consumer.accept(triangle1);
            }
        };
    }

    public static ClipInfo two(Triangle triangle1, Triangle triangle2) {
        return new ClipInfo(triangle1, triangle2) {
            @Override
            public void iterate(Consumer<Triangle> consumer) {
                consumer.accept(triangle1);
                consumer.accept(triangle2);
            }
        };
    }

}
