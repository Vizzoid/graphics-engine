package org.vizzoid.utils.engine;

import static java.lang.System.currentTimeMillis;

/**
 * Sleeper is used to help account for time passage during engine, it controls elapsed time, unprocessed time, smooths missed frames (in the future), and helps repaint with FPS regard
 * <p>
 * Modeled after JavaTutorials101's 3d engine's time processing (Screen.SleepAndRefresh())
 */
public class Sleeper {

    private double msPerFrame = 1;
    private long lastSleep;

    public double getMaxFps() {
        return 1000 / msPerFrame;
    }

    public void setMaxFps(double maxFps) {
        this.msPerFrame = 1000 / maxFps;
    }

    public void sleep() {
        long unprocessedTime = currentTimeMillis() - lastSleep; // time that is missed

        if (unprocessedTime < msPerFrame) {
            try {
                Thread.sleep((long) (msPerFrame - unprocessedTime)); // sleep off the remaining time until next frame
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        this.lastSleep = currentTimeMillis();
    }

}
