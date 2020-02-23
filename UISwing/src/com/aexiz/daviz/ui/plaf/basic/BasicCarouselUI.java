package com.aexiz.daviz.ui.plaf.basic;

import com.aexiz.daviz.ui.CarouselCellRenderer;
import com.aexiz.daviz.ui.JCarousel;
import com.aexiz.daviz.ui.plaf.CarouselUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class BasicCarouselUI extends CarouselUI {

    CellRendererPane rendererPane = new CellRendererPane();

    // Called by reflection code
    public static BasicCarouselUI createUI(JComponent c) {
        return new BasicCarouselUI();
    }

    public void installUI(JComponent c) {
        JCarousel cc = (JCarousel) c;
        cc.setOpaque(true);
        cc.setFocusable(true);
        cc.setBackground(UIManager.getColor("List.background"));
        cc.setForeground(UIManager.getColor("List.foreground"));
        cc.setSelectionBackground(UIManager.getColor("List.selectionBackground"));
        cc.setSelectionForeground(UIManager.getColor("List.selectionForeground"));
        cc.setFont(UIManager.getFont("List.font"));
        cc.add(rendererPane);
        class Handler implements KeyListener, MouseListener, MouseMotionListener, FocusListener {
            public void mouseClicked(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseMoved(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }

            public void mousePressed(MouseEvent e) {
                int i = cc.locationToIndex(e.getPoint());
                cc.requestFocusInWindow();
                cc.setSelectedIndex(i);
                e.consume();
            }

            public void mouseDragged(MouseEvent e) {
                int i = cc.locationToIndex(e.getPoint());
                cc.requestFocusInWindow();
                if (i >= 0)
                    cc.ensureIndexIsVisible(i);
                cc.setSelectedIndex(i);
                e.consume();
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void focusGained(FocusEvent e) {
                cc.repaint();
            }

            public void focusLost(FocusEvent e) {
                cc.repaint();
            }

            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    int i = cc.getAnchorSelectionIndex() - 1;
                    if (i < 0) i = 0;
                    cc.setSelectedIndex(i);
                    cc.ensureIndexIsVisible(i);
                    e.consume();
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    int i = cc.getAnchorSelectionIndex() + 1, sz = cc.getValueCount();
                    if (i >= sz) i = sz - 1;
                    cc.setSelectedIndex(i);
                    cc.ensureIndexIsVisible(i);
                    e.consume();
                }
            }

            public void keyReleased(KeyEvent e) {
            }

            public void keyTyped(KeyEvent e) {
            }
        }
        Handler h = new Handler();
        c.addMouseListener(h);
        c.addMouseMotionListener(h);
        c.addFocusListener(h);
        c.addKeyListener(h);
    }

    public void uninstallUI(JComponent c) {
        c.removeAll();
        // TODO unregister handler
    }

    public Dimension getMinimumSize(JComponent c) {
        JCarousel carousel = (JCarousel) c;
        CarouselCellRenderer r = carousel.getCellRenderer();
        if (r == null) return new Dimension(0, 0);
        int width = 0;
        int height = 0;
        for (int i = 0, size = carousel.getValueCount(); i < size; i++) {
            Object o = carousel.getValue(i);
            Component ch = r.getCarouselCellRendererComponent(carousel, o, i, false, false);
            if (ch == null) continue;
            rendererPane.add(ch);
            Dimension dim = ch.getPreferredSize();
            Dimension dim2 = ch.getMinimumSize();
            width += dim.width;
            if (dim2.height > height)
                height = dim2.height;
        }
        rendererPane.removeAll();
        return new Dimension(width, height);
    }

    public Dimension getPreferredSize(JComponent c) {
        return getMinimumSize(c);
    }

    public void paint(Graphics g, JComponent c) {
        JCarousel carousel = (JCarousel) c;
        Dimension d = carousel.getSize();
        g.setColor(carousel.getBackground());
        CarouselCellRenderer r = carousel.getCellRenderer();
        int x = 0;
        for (int i = 0, size = carousel.getValueCount(); i < size; i++) {
            Object o = carousel.getValue(i);
            boolean isSelected = carousel.isSelectedIndex(i);
            boolean cellHasFocus = carousel.hasFocus() && carousel.getAnchorSelectionIndex() == i;
            Component ch = r.getCarouselCellRendererComponent(carousel, o, i, isSelected, cellHasFocus);
            if (ch == null) continue;
            Dimension dim = ch.getPreferredSize();
            rendererPane.paintComponent(g, ch, carousel, x, 0, dim.width, d.height, true);
            x += dim.width;
        }
        rendererPane.removeAll();
    }

    public int locationToIndex(JCarousel c, Point location) {
        Dimension d = c.getSize();
        if (location.y < 0 || location.y >= d.height)
            return -1;
        CarouselCellRenderer r = c.getCellRenderer();
        int x = 0;
        for (int i = 0, size = c.getValueCount(); i < size; i++) {
            Object o = c.getValue(i);
            Component ch = r.getCarouselCellRendererComponent(c, o, i, false, false);
            Dimension dim = ch.getPreferredSize();
            if (location.x >= x && location.x < x + dim.width)
                return i;
            x += dim.width;
        }
        return -1;
    }

    public Point indexToLocation(JCarousel c, int index) {
        CarouselCellRenderer r = c.getCellRenderer();
        int x = 0;
        for (int i = 0, size = c.getValueCount(); i < size; i++) {
            Object o = c.getValue(i);
            Component ch = r.getCarouselCellRendererComponent(c, o, i, false, false);
            Dimension dim = ch.getPreferredSize();
            if (index == i) return new Point(x, 0);
            x += dim.width;
        }
        return null;
    }

    public Rectangle getCellBounds(JCarousel c, int from, int to) {
        CarouselCellRenderer r = c.getCellRenderer();
        Dimension d = c.getSize();
        Rectangle result = new Rectangle();
        result.y = 0;
        result.height = d.height;
        int x = 0;
        for (int i = 0, size = c.getValueCount(); i < size; i++) {
            Object o = c.getValue(i);
            Component ch = r.getCarouselCellRendererComponent(c, o, i, false, false);
            Dimension dim = ch.getPreferredSize();
            if (from == i) {
                result.x = x;
            }
            if (from <= i && i <= to) {
                result.width += dim.width;
            }
            x += dim.width;
        }
        return result;
    }

}
