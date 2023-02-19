package org.vizzoid.utils.engine;

public class SingleKeyListener implements KeyboardListener {

    private final int keycode;
    private final PressListener onPress;
    private boolean pressed;

    public SingleKeyListener(int keycode, PressListener onPress) {
        this.keycode = keycode;
        this.onPress = onPress;
    }

    public void tick(long missedTime) {
        if (pressed) pressed = onPress.listen(missedTime);
    }

    public void attempt(int keycode, boolean set) {
        if (keycode == this.keycode) {
            pressed = set;
        }
    }
}
