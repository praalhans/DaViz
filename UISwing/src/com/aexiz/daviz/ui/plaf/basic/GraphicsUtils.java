package com.aexiz.daviz.ui.plaf.basic;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;

class GraphicsUtils {

    private static final Shape ARROW_HEAD = initArrowHead();

    static void initializeGraphics(Graphics g) {
        if (g instanceof Graphics2D) {
            initializeGraphics((Graphics2D) g);
        }
    }

    static void initializeGraphics(Graphics2D g) {
        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHints(rh);
    }

    static void drawArrowHead(Graphics g, int fx, int fy, int tx, int ty, int ax, int ay) {
        if (g instanceof Graphics2D) {
            drawArrowHead((Graphics2D) g, fx, fy, tx, ty, ax, ay);
        }
    }

    static void drawArrowHead(Graphics2D g, int fx, int fy, int tx, int ty, int ax, int ay) {
        double rot = Math.atan2(ty - fy, tx - fx);
        AffineTransform at = g.getTransform();
        g.translate(ax, ay);
        g.rotate(rot);
        g.fill(ARROW_HEAD);
        g.setTransform(at);
    }

    private static Shape initArrowHead() {
        Path2D.Float path = new Path2D.Float();
        path.moveTo(0.0f, 0.0f);
        path.lineTo(-4f, 5.0f);
        path.lineTo(-4f, -4.0f);
        path.closePath();
        return path;
    }

}
