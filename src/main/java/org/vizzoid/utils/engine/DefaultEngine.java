package org.vizzoid.utils.engine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class DefaultEngine {

    protected final JFrame window = new JFrame();
    protected final JPanel display;
    public final Dimension dimension = new Dimension();
    public final Dimension center = new Dimension();
    protected final Toolkit toolkit = Toolkit.getDefaultToolkit();
    protected double aspectRatio;
    protected Painter painter = Painter.DEFAULT;
    protected final Sleeper sleeper = createSleeper();
    protected boolean shouldRepaint = true;

    public DefaultEngine() {
        display = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                DefaultEngine.this.paint(g);
            }
        };
        window.add(display);
        prepare();
    }

    protected Sleeper createSleeper() {
        return new Sleeper();
    }

    /**
     * Prepares screen and frame. Should initialize dimension
     */
    protected void prepare() {
        display.setFocusable(true);
        window.setUndecorated(false);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setDimension(toolkit.getScreenSize());
        window.setVisible(true);
    }

    /**
     * Sets dimension. Should set dimension field and center field, as well as aspect ratio and JFrame size.
     */
    public void setDimension(Dimension dimension) {
        this.dimension.width = dimension.width;
        this.dimension.height = dimension.height;
        this.center.width = (int) (dimension.width * 0.5);
        this.center.height = (int) (dimension.height * 0.5);
        this.aspectRatio = dimension.width / (double) dimension.height;
        this.window.setSize(dimension);
    }

    /**
     * Clears graphics image. Common ways are by wiping the screen with white, others color it in skybox color, such as blue, some can not clear screen at all, for
     * example if outside the skybox.
     */
    protected void clearScreen(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, dimension.width, dimension.height);
    }

    public Dimension getCenter() {
        return center;
    }

    public Dimension getDimension() {
        return dimension;
    }

    public double getAspectRatio() {
        return aspectRatio;
    }

    public Painter getPainter() {
        return painter;
    }

    public void setPainter(Painter painter) {
        this.painter = painter;
    }

    public void paint(Graphics graphics) {
        long missedTime = sleeper.pullMissedTime();
        clearScreen(graphics);

        painter.paint(graphics, missedTime);

        if (!shouldRepaint) return;
        sleep();
        repaint();
    }

    public void sleep() {
        sleeper.sleep();
    }

    public void repaint() {
        display.repaint();
    }

    public void setShouldRepaint(boolean shouldRepaint) {
        this.shouldRepaint = shouldRepaint;
    }

    public boolean shouldRepaint() {
        return shouldRepaint;
    }

    public void addMouseListener(MouseListener listener) {
        display.addMouseListener(listener);
    }

    public void addMotionListener(MouseMotionListener listener) {
        display.addMouseMotionListener(listener);
    }

    public void addKeyListener(KeyListener listener) {
        display.addKeyListener(listener);
    }

    public Sleeper getSleeper() {
        return sleeper;
    }
}
