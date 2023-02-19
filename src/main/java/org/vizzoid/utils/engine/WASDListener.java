package org.vizzoid.utils.engine;

public interface WASDListener {

    // returns final state of w, true if key should not change, false if key should stop triggering
    boolean onW(long missedTime);
    boolean onA(long missedTime);
    boolean onS(long missedTime);
    boolean onD(long missedTime);

}
