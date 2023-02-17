package org.vizzoid.utils.engine;

public interface WASDListener {

    // returns final state of w, true if key should not change, false if key should stop triggering
    boolean onW();
    boolean onA();
    boolean onS();
    boolean onD();

}
