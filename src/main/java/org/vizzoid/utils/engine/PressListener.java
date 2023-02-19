package org.vizzoid.utils.engine;

@FunctionalInterface
public interface PressListener {

    boolean listen(long missedTime);

}
