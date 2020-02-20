package com.aexiz.daviz.ui.plaf.basic;

import com.aexiz.daviz.ui.ExecutionModel.TimeEventListener;
import com.aexiz.daviz.ui.JTimeline;
import com.aexiz.daviz.ui.JTimeline.JEvent;
import com.aexiz.daviz.ui.JTimeline.JMessage;
import com.aexiz.daviz.ui.JTimeline.JTimeRuler;
import com.aexiz.daviz.ui.plaf.TimelineUI;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComponentUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.util.EventObject;
import java.util.IdentityHashMap;

public class BasicTimelineUI extends TimelineUI {

    static final String INTERNAL_STRING = "i";
    static final int PROCESS_HEIGHT = 57;
    static final int EVENT_DIAMETER = 15;
    static final int EVENT_TIMEUNIT = 22;
    static final int LABEL_LEFT = 10;
    static final int LABEL_RIGHT = 10;
    BasicEventUI basicEventUI = new BasicEventUI();
    BasicMessageUI basicMessageUI = new BasicMessageUI();
    BasicTimeRulerUI basicTimeRulerUI = new BasicTimeRulerUI();
    EventLayout layout = new EventLayout();

    // Called by reflection code
    public static BasicTimelineUI createUI(JComponent c) {
        return new BasicTimelineUI();
    }

    static int maxLabel(JTimeline t) {
        FontMetrics fm = t.getFontMetrics(t.getFont());
        int n = t.getModel().getProcessCount();
        int result = 0;
        for (int i = 0; i < n; i++) {
            String name = t.getModel().getProcessName(i);
            result = Math.max(result, fm.stringWidth(name));
        }
        return result;
    }

    public void configureEventComponent(JEvent c) {
        c.setUI(basicEventUI);
    }

    public void configureMessageComponent(JMessage c) {
        c.setUI(basicMessageUI);
    }

    public void configureTimeRulerComponent(JTimeRuler c) {
        c.setUI(basicTimeRulerUI);
    }

    public void installUI(JComponent c) {
        JTimeline t = (JTimeline) c;
        t.setLayout(layout);
        t.setBackground(UIManager.getColor("control"));
        t.setInnerBackground(UIManager.getColor("Tree.background"));
        t.setAlternateBackground(new Color(245, 245, 255));
        t.setInnerBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        t.setFont(UIManager.getFont("Tree.font"));
        Handler h = new Handler();
        h.timeline = t;
        t.addMouseListener(h);
        t.addKeyListener(h);
        // TODO: remove handler at uninstall
    }

    public void uninstallUI(JComponent c) {
    }

    public void paint(Graphics g, JComponent c) {
        GraphicsUtils.initializeGraphics(g);
        JTimeline t = (JTimeline) c;
        Dimension dim = t.getSize();
        Insets ii = t.getInnerInsets();
        Border ib = t.getInnerBorder();
        g.setColor(Color.RED);
        int n = t.getModel().getProcessCount();
        int maxLabel = maxLabel(t);
        int y = 0;
        for (int i = 0; i < n; i++) {
            String name = t.getModel().getProcessName(i);
            if (ib != null) {
                ib.paintBorder(c, g, 0, y, dim.width, PROCESS_HEIGHT);
            }
            g.setColor(t.getInnerBackground());
            int iwidth = dim.width - ii.left - ii.right,
                    iheight = PROCESS_HEIGHT - ii.top - ii.bottom;
            g.fillRect(ii.left, y + ii.top, iwidth, iheight);
            float time = t.getModel().getProcessMaxTime(i);
            int mwidth = ii.left + LABEL_LEFT + maxLabel + LABEL_RIGHT;
            mwidth += time * EVENT_TIMEUNIT;
            mwidth -= ii.right;
            if (mwidth < iwidth) iwidth = mwidth;
            int i2height = (iheight - 1) / 2;
            int ma2 = g.getFontMetrics().getAscent() - g.getFontMetrics().getHeight() / 2;
            g.setColor(t.getForeground());
            g.setFont(t.getFont());
            g.drawString(name, LABEL_LEFT, y + ii.top + i2height + ma2);
            g.drawLine(ii.left + LABEL_LEFT + maxLabel + LABEL_RIGHT, y + ii.top + i2height, iwidth + 1, y + ii.top + i2height);
            y += PROCESS_HEIGHT - ii.bottom;
        }
    }

    static class BasicEventUI extends ComponentUI {

        private IdentityHashMap<JComponent, Handler> state = new IdentityHashMap<>();

        public void installUI(JComponent c) {
            JEvent e = (JEvent) c;
            e.setOpaque(false);
            e.setBackground(UIManager.getColor("Tree.background"));
            e.setRolloverBackground(new Color(220, 220, 220));
            e.setPressedBackground(new Color(200, 200, 220));
            e.setForeground(UIManager.getColor("Tree.foreground"));
            e.setRolloverForeground(UIManager.getColor("Tree.foreground"));
            e.setPressedForeground(UIManager.getColor("Tree.foreground"));
            e.setSelectionBackground(UIManager.getColor("Tree.selectionBackground"));
            e.setSelectionForeground(UIManager.getColor("Tree.foreground"));
            Font o = UIManager.getFont("Tree.font");
            e.setFont(o.deriveFont(9.0f));
            Handler h = new Handler();
            h.event = e;
            e.addMouseListener(h);
            e.addMouseMotionListener(h);
            e.addChangeListener(h);
            state.put(c, h);
        }

        @Override
        public void uninstallUI(JComponent c) {
            JEvent e = (JEvent) c;
            Handler h = state.remove(c);
            e.removeMouseListener(h);
            e.removeMouseMotionListener(h);
            e.removeChangeListener(h);
        }

        public void paint(Graphics g, JComponent c) {
            GraphicsUtils.initializeGraphics(g);
            JEvent e = (JEvent) c;
            Dimension dim = e.getSize();
            if (e.isSelected()) {
                if (e.isPressed()) g.setColor(e.getSelectionPressedBackground());
                else if (e.isRollover()) g.setColor(e.getSelectionRolloverBackground());
                else g.setColor(e.getSelectionBackground());
            } else {
                if (e.isPressed()) g.setColor(e.getPressedBackground());
                else if (e.isRollover()) g.setColor(e.getRolloverBackground());
                else g.setColor(e.getBackground());
            }
            if (e.isReceiveEvent() || e.isSendEvent() || e.isInternalEvent()) {
                g.fillOval(0, 0, dim.width - 1, dim.height - 1);
            } else if (e.isDecideEvent()) {
                g.fillRect(0, 0, dim.width - 1, dim.height - 1);
            } else if ((e.isRollover() || e.isPressed() || e.isSelected()) && e.isTerminateEvent()) {
                g.fillRect(0, 0, dim.width, dim.height);
            }
            if (e.isSelected()) {
                if (e.isPressed()) g.setColor(e.getSelectionPressedForeground());
                else if (e.isRollover()) g.setColor(e.getSelectionRolloverForeground());
                else g.setColor(e.getSelectionForeground());
            } else {
                if (e.isPressed()) g.setColor(e.getPressedForeground());
                else if (e.isRollover()) g.setColor(e.getRolloverForeground());
                else g.setColor(e.getForeground());
            }
            if (e.isTerminateEvent()) {
                g.drawLine(0, 0, 0, dim.height - 1);
            } else if (e.isDecideEvent()) {
                g.drawRect(0, 0, dim.width - 1, dim.height - 1);
            } else {
                g.drawOval(0, 0, dim.width - 1, dim.height - 1);
            }
            if (e.isInternalEvent()) {
                g.setFont(e.getFont());
                FontMetrics fm = g.getFontMetrics(e.getParent().getFont());
                int h = fm.getAscent() - fm.getHeight() / 2;
                g.drawString(INTERNAL_STRING, (dim.width - 1) / 2, (dim.height - 1) / 2 + h);
            }
        }

        public boolean contains(JComponent c, int x, int y) {
            JEvent e = (JEvent) c;
            Dimension dim = e.getSize();
            if (e.isTerminateEvent() || e.isDecideEvent()) {
                return x >= 0 && x < dim.width && y >= 0 && y < dim.height;
            } else {
                int q = dim.width / 2, r = (x - q) * (x - q) + (y - q) * (y - q);
                return r < q * q;
            }
        }

        public Dimension getPreferredSize(JComponent c) {
            return getMinimumSize(c);
        }

        public Dimension getMinimumSize(JComponent c) {
            return new Dimension(EVENT_DIAMETER, EVENT_DIAMETER);
        }

        static class Handler implements ChangeListener, MouseListener, MouseMotionListener {

            JEvent event;
            boolean noClick;
            int firstXOnScreen;

            public void mouseClicked(MouseEvent e) {
                if (noClick) return;
                if (e.getClickCount() == 2)
                    event.requestAllSelected();
                else if (e.getClickCount() == 1)
                    event.requestSingleSelected();
            }

            public void mouseEntered(MouseEvent e) {
                event.setRollover(true);
            }

            public void mouseMoved(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
                event.setRollover(false);
            }

            public void mousePressed(MouseEvent e) {
                if (e.isControlDown()) {
                    noClick = true;
                    event.setSelected(!event.isSelected());
                    return;
                } else if (e.isShiftDown()) {
                    noClick = true;
                    event.setSelected(true);
                    return;
                } else {
                    noClick = false;
                }
                if (!event.isSelected())
                    event.requestClearSelection();
                switch (event.getTimeline().getEditMode()) {
                    case JTimeline.MODE_SELECTION:
                        break;
                    case JTimeline.MODE_SWAP:
                        firstXOnScreen = e.getXOnScreen();
                        event.setPressed(true);
                        break;
                }
            }

            public void mouseDragged(MouseEvent e) {
                if (!event.isPressed()) return;
                Rectangle visRect = new Rectangle(0, 0, event.getWidth(), event.getHeight());
                Point offset = event.scrollRectToVisibleWithEffect(visRect);
                firstXOnScreen += offset.x; // adjust original screen position by amount that was scrolled

                int ex = e.getXOnScreen();
                float delta = (ex - firstXOnScreen) / (float) EVENT_TIMEUNIT;
                event.setDelta(delta);
            }

            public void mouseReleased(MouseEvent e) {
                switch (event.getTimeline().getEditMode()) {
                    case JTimeline.MODE_SELECTION:
                        break;
                    case JTimeline.MODE_SWAP:
                        event.setPressed(false);
                }
            }

            public void stateChanged(ChangeEvent ce) {
                event.repaint();
            }
        }

    }

    static class BasicMessageUI extends ComponentUI {

        private Stroke stroke = new BasicStroke(7.0f);

        static Rectangle findEnclosingRectangle(Rectangle from, Rectangle to) {
            int minx = Math.min(from.x, to.x);
            int miny = Math.min(from.y, to.y);
            int maxx = Math.max(from.x + from.width, to.x + to.width);
            int maxy = Math.max(from.y + from.height, to.y + to.height);
            return new Rectangle(minx, miny, maxx - minx, maxy - miny);
        }

        static int findDirection(Rectangle from, Rectangle to) {
            if (from.x > to.x) {
                if (from.y > to.y) {
                    return JMessage.DIR_NORTH_WEST;
                } else {
                    return JMessage.DIR_SOUTH_WEST;
                }
            } else {
                if (from.y > to.y) {
                    return JMessage.DIR_NORTH_EAST;
                } else {
                    return JMessage.DIR_SOUTH_EAST;
                }
            }
        }

        public void installUI(JComponent c) {
            JMessage m = (JMessage) c;
            m.setRolloverBackground(new Color(220, 220, 220));
            m.setForeground(UIManager.getColor("Tree.foreground"));
            m.setRolloverForeground(UIManager.getColor("Tree.foreground"));
            m.setSelectionBackground(UIManager.getColor("Tree.selectionBackground"));
            m.setSelectionForeground(UIManager.getColor("Tree.foreground"));
            m.setErrorColor(Color.RED);
            Handler h = new Handler();
            h.message = m;
            m.addMouseListener(h);
            m.addChangeListener(h);
            // TODO uninstall handler
        }

        public void paint(Graphics g, JComponent c) {
            GraphicsUtils.initializeGraphics(g);
            JMessage m = (JMessage) c;
            int dir = m.getDirection();
            Dimension dim = c.getSize();
            int fx, fy, tx, ty, ax, ay;
            if (dir == JMessage.DIR_NORTH_EAST) {
                fx = EVENT_DIAMETER / 2;
                fy = dim.height - 1 - EVENT_DIAMETER / 2;
                tx = dim.width - 1 - EVENT_DIAMETER / 2;
                ty = EVENT_DIAMETER / 2;
            } else if (dir == JMessage.DIR_SOUTH_WEST) {
                fx = dim.width - 1 - EVENT_DIAMETER / 2;
                fy = EVENT_DIAMETER / 2;
                tx = EVENT_DIAMETER / 2;
                ty = dim.height - 1 - EVENT_DIAMETER / 2;
            } else if (dir == JMessage.DIR_NORTH_WEST) {
                fx = dim.width - 1 - EVENT_DIAMETER / 2;
                fy = dim.height - 1 - EVENT_DIAMETER / 2;
                tx = EVENT_DIAMETER / 2;
                ty = EVENT_DIAMETER / 2;
            } else if (dir == JMessage.DIR_SOUTH_EAST) {
                fx = EVENT_DIAMETER / 2;
                fy = EVENT_DIAMETER / 2;
                tx = dim.width - 1 - EVENT_DIAMETER / 2;
                ty = dim.height - 1 - EVENT_DIAMETER / 2;
            } else throw new Error("Unexpected direction");
            if (m.isSelected() || m.isRollover()) {
                if (m.isSelected()) {
                    if (m.isRollover()) g.setColor(m.getSelectionRolloverBackground());
                    else g.setColor(m.getSelectionBackground());
                } else {
                    if (m.isRollover()) g.setColor(m.getRolloverBackground());
                    else throw new Error();
                }
                if (g instanceof Graphics2D) {
                    Graphics2D g2d = (Graphics2D) g;
                    Stroke oldStroke = g2d.getStroke();
                    g2d.setStroke(stroke);
                    g.drawLine(fx, fy, tx, ty);
                    g2d.setStroke(oldStroke);
                }
            }
            if (m.isConflicting()) g.setColor(m.getErrorColor());
            else {
                if (m.isSelected()) {
                    if (m.isRollover()) g.setColor(m.getSelectionRolloverForeground());
                    else g.setColor(m.getSelectionForeground());
                } else {
                    if (m.isRollover()) g.setColor(m.getRolloverForeground());
                    else g.setColor(m.getForeground());
                }
            }
            if (m.isPending()) {
                Graphics2D g2d = (Graphics2D) g;
                Stroke dashed = new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0f, new float[]{3f, 3f}, 0f);
                Stroke old = g2d.getStroke();
                g2d.setStroke(dashed);
                g2d.drawLine(fx, fy, tx, ty);
                g2d.setStroke(old);
            } else {
                g.drawLine(fx, fy, tx, ty);
            }
            double rot = Math.atan2(ty - fy, tx - fx);
            ax = tx + (int) (EVENT_DIAMETER / -2.0 * Math.cos(rot));
            ay = ty + (int) (EVENT_DIAMETER / -2.0 * Math.sin(rot));
            if (!m.isPending()) GraphicsUtils.drawArrowHead(g, fx, fy, tx, ty, ax, ay);
        }

        public boolean contains(JComponent c, int x, int y) {
            JMessage m = (JMessage) c;
            int dir = m.getDirection();
            Dimension dim = c.getSize();
            int fx, fy, tx, ty;
            if (dir == JMessage.DIR_NORTH_EAST) {
                fx = EVENT_DIAMETER / 2;
                fy = dim.height - 1 - EVENT_DIAMETER / 2;
                tx = dim.width - 1 - EVENT_DIAMETER / 2;
                ty = EVENT_DIAMETER / 2;
            } else if (dir == JMessage.DIR_SOUTH_WEST) {
                fx = dim.width - 1 - EVENT_DIAMETER / 2;
                fy = EVENT_DIAMETER / 2;
                tx = EVENT_DIAMETER / 2;
                ty = dim.height - 1 - EVENT_DIAMETER / 2;
            } else if (dir == JMessage.DIR_NORTH_WEST) {
                fx = dim.width - 1 - EVENT_DIAMETER / 2;
                fy = dim.height - 1 - EVENT_DIAMETER / 2;
                tx = EVENT_DIAMETER / 2;
                ty = EVENT_DIAMETER / 2;
            } else if (dir == JMessage.DIR_SOUTH_EAST) {
                fx = EVENT_DIAMETER / 2;
                fy = EVENT_DIAMETER / 2;
                tx = dim.width - 1 - EVENT_DIAMETER / 2;
                ty = dim.height - 1 - EVENT_DIAMETER / 2;
            } else {
                // Layout manager may not have processed this one yet.
                // TODO compute direction statically, as in JGraph
                return true;
            }
            Shape line = new Line2D.Double(fx, fy, tx, ty);
            line = stroke.createStrokedShape(line);
            return line.contains(x, y);
        }

        class Handler implements MouseListener, ChangeListener {
            JMessage message;
            boolean noClick;

            public void mouseClicked(MouseEvent e) {
                if (noClick) return;
                if (e.getClickCount() == 2)
                    message.requestAllSelected();
                else if (e.getClickCount() == 1)
                    message.requestSingleSelected();
            }

            public void mouseEntered(MouseEvent e) {
                message.setRollover(true);
            }

            public void mouseExited(MouseEvent e) {
                message.setRollover(false);
            }

            public void mousePressed(MouseEvent e) {
                if (e.isControlDown()) {
                    noClick = true;
                    message.setSelected(!message.isSelected());
                    return;
                } else if (e.isShiftDown()) {
                    noClick = true;
                    message.setSelected(true);
                    return;
                } else {
                    noClick = false;
                }
                if (!message.isSelected())
                    message.requestClearSelection();
                message.setPressed(true);
            }

            public void mouseReleased(MouseEvent e) {
                message.setPressed(false);
            }

            public void stateChanged(ChangeEvent e) {
                message.repaint();
            }
        }

    }

    static class BasicTimeRulerUI extends ComponentUI {

        private IdentityHashMap<JComponent, Handler> state = new IdentityHashMap<>();

        public void installUI(JComponent c) {
            JTimeRuler r = (JTimeRuler) c;
            r.setOpaque(false);
            r.setForeground(Color.RED);
            r.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
            Handler h = new Handler();
            h.r = r;
            r.addMouseListener(h);
            r.addMouseMotionListener(h);
            r.addTimeEventListener(h);
            state.put(c, h);
        }

        public void uninstallUI(JComponent c) {
            JTimeRuler r = (JTimeRuler) c;
            Handler h = state.remove(c);
            r.removeMouseListener(h);
            r.removeMouseMotionListener(h);
            r.removeTimeEventListener(h);
        }

        public void paint(Graphics g, JComponent c) {
            GraphicsUtils.initializeGraphics(g);
            JTimeRuler r = (JTimeRuler) c;
            Dimension dim = r.getSize();
            Color col = r.getForeground();
            col = new Color(col.getRed(), col.getGreen(), col.getBlue(), 100);
            g.setColor(col);
            g.fillRect(0, 0, 1, dim.height);
        }

        @Override
        public Dimension getPreferredSize(JComponent c) {
            return new Dimension(EVENT_DIAMETER, 0);
        }

        static class Handler implements MouseListener, MouseMotionListener, TimeEventListener {
            JTimeRuler r;
            int firstXOnScreen;

            public void mouseClicked(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent me) {
            }

            public void mouseExited(MouseEvent me) {
            }

            public void mouseMoved(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent me) {
                r.setPressed(false);
            }

            public void mousePressed(MouseEvent me) {
                r.setPressed(true);
                firstXOnScreen = me.getXOnScreen();
            }

            public void mouseDragged(MouseEvent me) {
                int lastXOnScreen = me.getXOnScreen();
                float delta = (lastXOnScreen - firstXOnScreen) / (float) EVENT_TIMEUNIT;
                r.setDelta(delta);
            }

            public void timeChanged(EventObject e) {
                if (r.getModel().getTemporaryMaxTime() > 0.0) return;
                SwingUtilities.invokeLater(() -> {
                    r.invalidate();
                    r.validate();
                    Point offset = r.scrollRectToVisibleWithEffect(new Rectangle(0, 0, r.getWidth(), r.getHeight()));
                    firstXOnScreen += offset.x; // adjust original screen position by amount that was scrolled
                });
            }
        }

    }

    static class EventLayout implements LayoutManager {

        public void addLayoutComponent(String arg, Component c) {
        }

        private int getProcessY(Container c, int index) {
            JTimeline t = (JTimeline) c;
            Insets ii = t.getInnerInsets();
            int iheight = PROCESS_HEIGHT - ii.bottom - ii.top;
            int i2height = (iheight - 1) / 2;
            int y = (PROCESS_HEIGHT - ii.bottom) * index + ii.top + i2height;
            return y;
        }

        public void layoutContainer(Container c) {
            JTimeline t = (JTimeline) c;
            Insets ii = t.getInnerInsets();
            int maxLabel = maxLabel(t);
            int n = t.getComponentCount();
            for (int i = 0; i < n; i++) {
                Component child = t.getComponent(i);
                if (child instanceof JEvent) {
                    JEvent event = (JEvent) child;
                    Dimension dim = event.getPreferredSize();
                    int p = event.getProcessIndex();
                    int x, y;
                    x = ii.left + LABEL_LEFT + maxLabel + LABEL_RIGHT;
                    x += event.getTime() * EVENT_TIMEUNIT;
                    y = getProcessY(t, p);
                    y -= dim.height / 2;
                    y -= event.getAscention() * EVENT_TIMEUNIT;
                    child.setBounds(x, y, dim.width, dim.height);
                }
            }
            for (int i = 0; i < n; i++) {
                Component child = t.getComponent(i);
                if (child instanceof JMessage) {
                    JMessage message = (JMessage) child;
                    JEvent from = message.getFromEvent();
                    if (from == null) continue;
                    if (message.isPending()) {
                        int to = message.getToProcessIndex();
                        Rectangle rto = from.getBounds();
                        int ydiff = getProcessY(c, to) - (rto.y + rto.height / 2);
                        rto.translate(0, ydiff);
                        message.setBounds(BasicMessageUI.findEnclosingRectangle(from.getBounds(), rto));
                        if (ydiff <= 0) {
                            message.setDirection(JMessage.DIR_NORTH_EAST);
                        } else {
                            message.setDirection(JMessage.DIR_SOUTH_EAST);
                        }
                    } else {
                        JEvent to = message.getToEvent();
                        if (to == null) continue;
                        message.setBounds(BasicMessageUI.findEnclosingRectangle(from.getBounds(), to.getBounds()));
                        message.setDirection(BasicMessageUI.findDirection(from.getBounds(), to.getBounds()));
                    }
                }
            }
            for (int i = 0; i < n; i++) {
                Component child = t.getComponent(i);
                if (child instanceof JTimeRuler) {
                    JTimeRuler ruler = (JTimeRuler) child;
                    Dimension pref = ruler.getPreferredSize();
                    Dimension dim = t.getSize();
                    Insets oi = t.getInsets();
                    int x;
                    x = ii.left + LABEL_LEFT + maxLabel + LABEL_RIGHT;
                    x += t.getCurrentTime() * EVENT_TIMEUNIT;
                    ruler.setBounds(x, oi.top, pref.width, dim.height - oi.top - oi.bottom);
                }
            }
        }

        public Dimension minimumLayoutSize(Container c) {
            return new Dimension(100, 100);
        }

        public Dimension preferredLayoutSize(Container c) {
            JTimeline t = (JTimeline) c;
            Insets ii = t.getInnerInsets();
            int n = t.getModel().getProcessCount();
            float time = t.getModel().getMaxLastTimeWithoutDelta();
            float temp = t.getModel().getTemporaryMaxTime();
            if (temp > time) time = temp;
            int maxLabel = maxLabel(t);
            int mwidth = ii.left + LABEL_LEFT + maxLabel + LABEL_RIGHT;
            mwidth += time * EVENT_TIMEUNIT;
            mwidth += EVENT_TIMEUNIT;
            mwidth += LABEL_RIGHT;
            mwidth -= ii.right;
            return new Dimension(mwidth, (PROCESS_HEIGHT - ii.bottom) * n + ii.bottom);
        }

        public void removeLayoutComponent(Component c) {

        }

    }

    class Handler implements MouseListener, KeyListener {
        JTimeline timeline;
        Rectangle rulerKeyRect = new Rectangle(-200, 0, 400, 0);

        public void mouseClicked(MouseEvent me) {
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }

        public void mousePressed(MouseEvent e) {
            boolean focus = timeline.hasFocus();
            timeline.requestFocusInWindow();
            Dimension within = timeline.getPreferredSize();
            if (focus && !e.isControlDown() && !e.isShiftDown() && e.getY() < within.height) {
                timeline.clearSelection();
            }
            e.consume();
        }

        public void mouseReleased(MouseEvent me) {
        }

        public void keyPressed(KeyEvent e) {
            float time = timeline.getCurrentTime();
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                // Only consume if the current time is actually decreasable
                if (timeline.getCurrentTime() > 0.0f) {
                    timeline.setCurrentTime(time - 1.0f);
                    e.consume();
                }
            } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                if (timeline.getCurrentTime() < timeline.getModel().getMaxLastTime()) {
                    timeline.setCurrentTime(time + 1.0f);
                    e.consume();
                }
            }
        }

        public void keyReleased(KeyEvent e) {
        }

        public void keyTyped(KeyEvent e) {
        }
    }

}
