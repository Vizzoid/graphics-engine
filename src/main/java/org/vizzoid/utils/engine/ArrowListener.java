package org.vizzoid.utils.engine;

public interface ArrowListener {

    // returns final state of w, true if key should not change, false if key should stop triggering
    boolean onUp(long missedTime);
    boolean onRight(long missedTime);
    boolean onLeft(long missedTime);
    boolean onDown(long missedTime);

}
