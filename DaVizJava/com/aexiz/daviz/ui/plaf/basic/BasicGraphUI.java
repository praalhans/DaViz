package com.aexiz.daviz.ui.plaf.basic;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.font.LineMetrics;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComponentUI;

import com.aexiz.daviz.ui.JGraph;
import com.aexiz.daviz.ui.JGraph.JEdge;
import com.aexiz.daviz.ui.JGraph.JNode;
import com.aexiz.daviz.ui.plaf.GraphUI;
import com.aexiz.daviz.ui.plaf.basic.BasicTimelineUI.BasicEventUI.Handler;

public class BasicGraphUI extends GraphUI {
	
	BasicNodeUI basicNodeUI = new BasicNodeUI();
	
	static class BasicNodeUI extends ComponentUI {
		
		public void installUI(JComponent c) {
			JNode node = (JNode) c;
			node.setOpaque(false);
			node.setRolloverBackground(new Color(220, 220, 220));
			node.setRolloverForeground(UIManager.getColor("Tree.foreground"));
			node.setBackground(UIManager.getColor("Tree.background"));
			node.setForeground(UIManager.getColor("Tree.foreground"));
			node.setSelectionBackground(UIManager.getColor("Tree.selectionBackground"));
			node.setSelectionForeground(UIManager.getColor("Tree.selectionForeground"));
			Font o = UIManager.getFont("Tree.font");
			node.setFont(o);
			class Handler implements ChangeListener, MouseListener, MouseMotionListener {
				int x, y;
				boolean noClick;
				JNode edgeTarget;
				
				public void mouseClicked(MouseEvent e) {
					JGraph g = node.getGraph();
					switch (g.getEditMode()) {
					case JGraph.MODE_SELECTION:
						if (noClick) return;
						if (e.getClickCount() == 2) {
							node.requestAllSelected();
							e.consume();
						} else if (e.getClickCount() == 1) {
							node.requestSingleSelected();
							e.consume();
						}
						break;
					}
				}
				public void mouseEntered(MouseEvent e) {
					if (node.getGraph().getEditMode() == JGraph.MODE_ERASE && node.getGraph().isErasing()) {
						node.remove();
						e.consume();
					} else {
						node.setRollover(true);
						e.consume();
					}
				}
				public void mouseExited(MouseEvent e) {
					node.setRollover(false);
					e.consume();
				}
				public void mousePressed(MouseEvent me) {
					noClick = false;
					node.requestFocusInWindow();
					JGraph g = node.getGraph();
					ComputeBounds cb = new ComputeBounds();
					cb.g = g;
					cb.compute();
					switch (g.getEditMode()) {
					case JGraph.MODE_SELECTION:
					case JGraph.MODE_ERASE:
						if (me.isControlDown()) {
							noClick = true;
							node.setSelected(!node.isSelected());
							me.consume();
							return;
						} else if (me.isShiftDown()) {
							noClick = true;
							node.setSelected(true);
							me.consume();
							return;
						}
						if (!node.isSelected())
							node.requestClearSelection();
						if (g.isReadOnly() || g.getEditMode() == JGraph.MODE_ERASE)
							break;
					case JGraph.MODE_VERTEX:
						x = me.getXOnScreen();
						y = me.getYOnScreen();
						node.setPressed(true);
						node.startMoving();
						me.consume();
						break;
					case JGraph.MODE_EDGE:
						float dx = cb.centerDeltaX(me.getX());
						float dy = cb.centerDeltaY(me.getY());
						dx += node.getModel().getX();
						dy += node.getModel().getY();
						edgeTarget = g.startCreatingEdge(node, dx, dy);
						x = me.getXOnScreen();
						y = me.getYOnScreen();
						edgeTarget.setPressed(true);
						edgeTarget.startMoving();
						me.consume();
						break;
					}
				}
				public void mouseReleased(MouseEvent me) {
					JGraph g = node.getGraph();
					switch (g.getEditMode()) {
					case JGraph.MODE_SELECTION:
					case JGraph.MODE_VERTEX:
					case JGraph.MODE_ERASE:
						node.setPressed(false);
						if (!node.commitMoving()) {
							node.getToolkit().beep();
						}
						me.consume();
						break;
					case JGraph.MODE_EDGE:
						edgeTarget.setPressed(false);
						if (!g.commitCreatingEdge(edgeTarget)) {
							g.getToolkit().beep();
						}
						edgeTarget = null;
						break;
					}
					if (g.getEditMode() == JGraph.MODE_ERASE) {
						node.remove();
						g.requestFocusInWindow();
					}
				}
				
				public void mouseMoved(MouseEvent e) {}
				public void mouseDragged(MouseEvent me) {
					int ex = me.getXOnScreen();
					int ey = me.getYOnScreen();
					JGraph g = node.getGraph();
					ComputeBounds cb = new ComputeBounds();
					cb.g = g;
					cb.compute();
					float deltaX = cb.deltaX(ex - x);
					float deltaY = cb.deltaY(ey - y);
					switch (g.getEditMode()) {
					case JGraph.MODE_SELECTION:
						if (g.isReadOnly()) break;
					case JGraph.MODE_VERTEX:
						node.setDeltaX(deltaX);
						node.setDeltaY(deltaY);
						me.consume();
						break;
					case JGraph.MODE_EDGE:
						edgeTarget.setDeltaX(deltaX);
						edgeTarget.setDeltaY(deltaY);
						me.consume();
						break;
					}
				}
				
				public void stateChanged(ChangeEvent ce) {
					node.repaint();
				}
			}
			Handler h = new Handler();
			node.addMouseListener(h);
			node.addMouseMotionListener(h);
			node.addChangeListener(h);
		}
		
		public void paint(Graphics g, JComponent c) {
			GraphicsUtils.initializeGraphics(g);
			JNode node = (JNode) c;
			ComputeBounds cb = new ComputeBounds();
			cb.g = node.getGraph();
			cb.compute();
			boolean temp = (Boolean) node.getClientProperty(JGraph.CLIENT_PROPERTY_TEMPORARY);
			Dimension dim = node.getSize();
			if (temp) {
				g.setColor(node.getBackground());
				g.fillOval(0, 0, dim.width - 1, dim.height - 1);
				Graphics2D g2d = (Graphics2D) g;
				Stroke old = g2d.getStroke();
				float[] dash = new float[]{3.0f};
				g2d.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1.0f, dash, 0.0f));
				g.setColor(node.getForeground());
				g.drawOval(0, 0, dim.width - 1, dim.height - 1);
				g2d.setStroke(old);
			} else {
				if (node.isSelected()) {
					if (node.isRollover()) g.setColor(node.getSelectionRolloverBackground());
					else g.setColor(node.getSelectionBackground());
				} else {
					if (node.isRollover()) g.setColor(node.getRolloverBackground());
					else g.setColor(node.getBackground());
				}
				g.fillOval(0, 0, dim.width - 1, dim.height - 1);
				g.setColor(node.getForeground());
				if (g instanceof Graphics2D) {
					Stroke normalStroke = new BasicStroke(1.0f * cb.zoom);
					Graphics2D g2d = (Graphics2D) g;
					Stroke oldStroke = g2d.getStroke();
					g2d.setStroke(normalStroke);
					g2d.draw(new Ellipse2D.Float(cb.zoom / 2f, cb.zoom / 2f, dim.width - 1f - cb.zoom, dim.height - 1f - cb.zoom));
					g2d.setStroke(oldStroke);
				}
				if (cb.zoom >= 1.0f) {
					Font f = node.getFont().deriveFont(10.0f * cb.zoom);
					g.setFont(f);
					FontMetrics fm = g.getFontMetrics(f);
					Rectangle2D dm = fm.getStringBounds(node.getLabel(), g);
					LineMetrics lm = fm.getLineMetrics(node.getLabel(), g);
					int x = (int) ((dim.width - dm.getWidth()) / 2.0);
				    int y = (int) ((dim.height - dm.getHeight() - lm.getDescent()) / 2.0 - dm.getY());
					g.drawString(node.getLabel(), x, y);
				}
			}
		}
		
		public boolean contains(JComponent c, int x, int y) {
			JNode node = (JNode) c;
			JGraph g = node.getGraph();
			ComputeBounds cb = new ComputeBounds();
			cb.g = g;
			cb.compute();
			int q = cb.getNodeRadius(), r = (x - q) * (x - q) + (y - q) * (y - q);
			return r < q * q;
		}
		
		public Dimension getPreferredSize(JComponent c) {
			return getMinimumSize(c);
		}
		
		public Dimension getMinimumSize(JComponent c) {
			JNode node = (JNode) c;
			JGraph g = node.getGraph();
			ComputeBounds cb = new ComputeBounds();
			cb.g = g;
			cb.compute();
			return new Dimension(cb.getNodeWidth(), cb.getNodeHeight());
		}
		
	}
	
	BasicEdgeUI basicEdgeUI = new BasicEdgeUI();
	
	static class BasicEdgeUI extends ComponentUI {
		
		private static final int DIR_NORTH_EAST = 1;
		private static final int DIR_SOUTH_EAST = 2;
		private static final int DIR_SOUTH_WEST = 3;
		private static final int DIR_NORTH_WEST = 4;
		
		public void installUI(JComponent c) {
			JEdge edge = (JEdge) c;
			edge.setOpaque(false);
			edge.setRolloverBackground(new Color(220, 220, 220));
			edge.setRolloverForeground(UIManager.getColor("Tree.foreground"));
			edge.setForeground(UIManager.getColor("Tree.foreground"));
			edge.setSelectionBackground(UIManager.getColor("Tree.selectionBackground"));
			edge.setSelectionForeground(UIManager.getColor("Tree.foreground"));
			class Handler implements MouseListener, ChangeListener {
				boolean noClick;
				public void mouseClicked(MouseEvent e) {
					if (noClick) return;
					JGraph g = edge.getGraph();
					switch (g.getEditMode()) {
					case JGraph.MODE_SELECTION:
						if (e.getClickCount() == 2) {
							edge.requestAllSelected();
							e.consume();
						} else if (e.getClickCount() == 1) {
							edge.requestSingleSelected();
							e.consume();
						}
						break;
					}
				}
				public void mouseEntered(MouseEvent e) {
					if (edge.getGraph().getEditMode() == JGraph.MODE_ERASE && edge.getGraph().isErasing()) {
						edge.remove();
						e.consume();
					} else {
						edge.setRollover(true);
						e.consume();
					}
				}
				public void mouseExited(MouseEvent e) {
					edge.setRollover(false);
				}
				public void mousePressed(MouseEvent e) {
					edge.requestFocusInWindow();
					JGraph g = edge.getGraph();
					switch (g.getEditMode()) {
					case JGraph.MODE_SELECTION:
						if (e.isControlDown()) {
							noClick = true;
							edge.setSelected(!edge.isSelected());
							return;
						} else if (e.isShiftDown()) {
							noClick = true;
							edge.setSelected(true);
							return;
						} else {
							noClick = false;
						}
						if (!edge.isSelected())
							edge.requestClearSelection();
						edge.setPressed(true);
						e.consume();
						break;
					}
				}
				public void mouseReleased(MouseEvent me) {
					JGraph g = edge.getGraph();
					switch (g.getEditMode()) {
					case JGraph.MODE_SELECTION:
						edge.setPressed(false);
						me.consume();
						break;
					case JGraph.MODE_ERASE:
						edge.remove();
						g.requestFocusInWindow();
						me.consume();
						break;
					}
				}
				
				public void stateChanged(ChangeEvent e) {
					edge.repaint();
				}
			}
			Handler h = new Handler();
			edge.addMouseListener(h);
			edge.addChangeListener(h);
			// TODO uninstall handler
		}
		
		public void paint(Graphics g, JComponent c) {
			GraphicsUtils.initializeGraphics(g);
			JEdge edge = (JEdge) c;
			ComputeBounds cb = new ComputeBounds();
			cb.g = edge.getGraph();
			cb.compute();
			Dimension dim = c.getSize();
			int fx, fy, tx, ty, ax, ay;
			switch (findDirection(edge)) {
			case DIR_NORTH_EAST:
				fx = cb.getNodeRadius();
				fy = dim.height - cb.getNodeRadius() - 1;
				tx = dim.width - cb.getNodeRadius() - 1;
				ty = cb.getNodeRadius();
				break;
			case DIR_SOUTH_WEST:
				fx = dim.width - cb.getNodeRadius() - 1;
				fy = cb.getNodeRadius();
				tx = cb.getNodeRadius();
				ty = dim.height - cb.getNodeRadius() - 1;
				break;
			case DIR_NORTH_WEST:
				fx = dim.width - cb.getNodeRadius() - 1;
				fy = dim.height - cb.getNodeRadius() - 1;
				tx = cb.getNodeRadius();
				ty = cb.getNodeRadius();
				break;
			case DIR_SOUTH_EAST:
				fx = cb.getNodeRadius();
				fy = cb.getNodeRadius();
				tx = dim.width - cb.getNodeRadius() - 1;
				ty = dim.height - cb.getNodeRadius() - 1;
				break;
			default: throw new RuntimeException();
			}
			if (edge.isSelected() || edge.isRollover()) {
				if (edge.isSelected()) {
					if (edge.isRollover()) g.setColor(edge.getSelectionRolloverBackground());
					else g.setColor(edge.getSelectionBackground());
				} else {
					if (edge.isRollover()) g.setColor(edge.getRolloverBackground());
					else throw new Error();
				}
				if (g instanceof Graphics2D) {
					Stroke highlightStroke = new BasicStroke(7.0f * cb.zoom);
					Graphics2D g2d = (Graphics2D) g;
					Stroke oldStroke = g2d.getStroke();
					g2d.setStroke(highlightStroke);
					g.drawLine(fx, fy, tx, ty);
					g2d.setStroke(oldStroke);
				}
			}
			g.setColor(edge.getForeground());
			if (g instanceof Graphics2D) {
				Stroke normalStroke = new BasicStroke(1.0f * cb.zoom);
				Graphics2D g2d = (Graphics2D) g;
				Stroke oldStroke = g2d.getStroke();
				g2d.setStroke(normalStroke);
				g.drawLine(fx, fy, tx, ty);
				g2d.setStroke(oldStroke);
			}
			if (edge.isDirected()) {
				double rot = Math.atan2(ty - fy, tx - fx);
				ax = tx + (int) (-cb.getNodeRadius() * Math.cos(rot));
				ay = ty + (int) (-cb.getNodeRadius() * Math.sin(rot));
				GraphicsUtils.drawArrowHead(g, fx, fy, tx, ty, ax, ay);
			}
		}
		
		public boolean contains(JComponent c, int x, int y) {
			JEdge edge = (JEdge) c;
			ComputeBounds cb = new ComputeBounds();
			cb.g = edge.getGraph();
			cb.compute();
			Dimension dim = c.getSize();
			int fx, fy, tx, ty;
			switch (findDirection(edge)) {
			case DIR_NORTH_EAST:
				fx = cb.getNodeRadius();
				fy = dim.height - cb.getNodeRadius() - 1;
				tx = dim.width - cb.getNodeRadius() - 1;
				ty = cb.getNodeRadius();
				break;
			case DIR_SOUTH_WEST:
				fx = dim.width - cb.getNodeRadius() - 1;
				fy = cb.getNodeRadius();
				tx = cb.getNodeRadius();
				ty = dim.height - cb.getNodeRadius() - 1;
				break;
			case DIR_NORTH_WEST:
				fx = dim.width - cb.getNodeRadius() - 1;
				fy = dim.height - cb.getNodeRadius() - 1;
				tx = cb.getNodeRadius();
				ty = cb.getNodeRadius();
				break;
			case DIR_SOUTH_EAST:
				fx = cb.getNodeRadius();
				fy = cb.getNodeRadius();
				tx = dim.width - cb.getNodeRadius() - 1;
				ty = dim.height - cb.getNodeRadius() - 1;
				break;
			default: throw new RuntimeException();
			}
			Stroke highlightStroke = new BasicStroke(7.0f * cb.zoom);
			Shape line = new Line2D.Double(fx, fy, tx, ty);
			line = highlightStroke.createStrokedShape(line);
			return line.contains(x, y);
		}
		
		// Assume that edge has two components (from, to) with bounds already set.
		static Rectangle findEnclosingRectangle(JEdge edge) {
			Rectangle from = edge.getFrom().getBounds(), to = edge.getTo().getBounds();
			int minx = Math.min(from.x, to.x);
			int miny = Math.min(from.y, to.y);
			int maxx = Math.max(from.x + from.width, to.x + to.width);
			int maxy = Math.max(from.y + from.height, to.y + to.height);
			return new Rectangle(minx, miny, maxx - minx, maxy - miny);
		}
		
		// Assume that edge has two components (from, to) with bounds already set.
		private static int findDirection(JEdge edge) {
			Rectangle from = edge.getFrom().getBounds();
			Rectangle to = edge.getTo().getBounds();
			if (from.x > to.x) {
				if (from.y > to.y) {
					return DIR_NORTH_WEST;
				} else {
					return DIR_SOUTH_WEST;
				}
			} else {
				if (from.y > to.y) {
					return DIR_NORTH_EAST;
				} else {
					return DIR_SOUTH_EAST;
				}
			}
		}
		
	}
	
	public void configureEdgeComponent(JEdge c) {
		c.setUI(basicEdgeUI);
	}
	
	public void configureNodeComponent(JNode c) {
		c.setUI(basicNodeUI);
	}
	
	public static BasicGraphUI createUI(JComponent c) {
		return new BasicGraphUI();
	}
	
	NodeLayout layout = new NodeLayout();
	
	public void installUI(JComponent c) {
		JGraph g = (JGraph) c;
		g.setOpaque(true);
		g.setLayout(layout);
		g.setForeground(Color.GRAY);
		g.setBackground(UIManager.getColor("Tree.background"));
		g.setReadOnlyBackground(UIManager.getColor("Panel.background"));
		g.setFont(UIManager.getFont("Tree.font"));
		ComputeBounds cb = new ComputeBounds();
		cb.g = g;
		class Handler implements MouseListener, MouseMotionListener, PropertyChangeListener {
			int x, y;
			JNode creatingNode;
			JNode edgeFromNode;
			JNode edgeToNode;
			public void mouseClicked(MouseEvent me) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseMoved(MouseEvent me) {}
			public void mouseExited(MouseEvent e) {}
			public void mousePressed(MouseEvent me) {
				boolean focus = g.hasFocus();
				g.requestFocusInWindow();
				int mode = g.getEditMode();
				float mx, my;
				switch (mode) {
				case JGraph.MODE_VERTEX:
				case JGraph.MODE_EDGE:
					cb.compute();
					mx = cb.centerComponentToModelX(me.getX());
					my = cb.centerComponentToModelY(me.getY());
					break;
				case JGraph.MODE_SELECTION:
					if (focus && !me.isControlDown() && !me.isShiftDown()) {
						g.clearSelection();
					}
				default:
					mx = my = 0.0f;
				}
				switch (mode) {
				case JGraph.MODE_VERTEX:
					creatingNode = g.startCreatingNode(mx, my);
					creatingNode.setPressed(true);
					break;
				case JGraph.MODE_EDGE:
					edgeFromNode = g.startCreatingEdgeNode(mx, my);
					if (edgeFromNode != null) {
						edgeToNode = g.startCreatingEdge(edgeFromNode, mx, my);
						edgeToNode.setPressed(true);
					}
					break;
				case JGraph.MODE_ERASE:
					g.setErasing(true);
				default:
					creatingNode = null;
				}
				x = me.getX();
				y = me.getY();
				me.consume();
			}
			public void mouseDragged(MouseEvent me) {
				int ex = me.getX();
				int ey = me.getY();
				ComputeBounds cb = new ComputeBounds();
				cb.g = g;
				cb.compute();
				float deltaX = cb.deltaX(ex - x);
				float deltaY = cb.deltaY(ey - y);
				if (creatingNode != null) {
					creatingNode.setDeltaX(deltaX);
					creatingNode.setDeltaY(deltaY);
					me.consume();
				}
				if (edgeToNode != null) {
					edgeToNode.setDeltaX(deltaX);
					edgeToNode.setDeltaY(deltaY);
					me.consume();
				}
			}
			public void mouseReleased(MouseEvent me) {
				if (creatingNode != null) {
					creatingNode.setPressed(false);
					if (!g.commitCreatingNode(creatingNode)) {
						g.getToolkit().beep();
					}
					creatingNode = null;
					me.consume();
				}
				if (edgeToNode != null) {
					edgeToNode.setPressed(false);
					if (!g.commitCreatingEdge(edgeToNode)) {
						g.getToolkit().beep();
					}
					edgeToNode = null;
					me.consume();
				}
				if (g.isErasing()) {
					g.setErasing(false);
				}
			}
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals("showGrid"))
					g.repaint();
			}
		}
		Handler h = new Handler();
		g.addMouseListener(h);
		g.addMouseMotionListener(h);
		g.addPropertyChangeListener(h);
		// TODO: remove handler at uninstall
	}
	
	public void paint(Graphics gg, JComponent c) {
		JGraph g = (JGraph) c;
		if (g.isReadOnly()) {
			gg.setColor(g.getReadOnlyBackground());
			gg.fillRect(0, 0, c.getWidth(), c.getHeight());
		}
		if (!g.getShowGrid())
			return;
		ComputeBounds cb = new ComputeBounds();
		cb.g = g;
		cb.compute();
		gg.setColor(g.getForeground());
		// Draw origin
		gg.drawLine(cb.modelToCenterComponentX(0.0f), 0, cb.modelToCenterComponentX(0.0f), cb.actual.height);
		gg.drawLine(0, cb.modelToCenterComponentY(0.0f), cb.actual.width, cb.modelToCenterComponentY(0.0f));
		// Draw around (debug)
		/*int tlX = cb.modelToCenterComponentX(cb.modelMinX);
		int tlY = cb.modelToCenterComponentY(cb.modelMinY);
		int brX = cb.modelToCenterComponentX(cb.modelMaxX);
		int brY = cb.modelToCenterComponentY(cb.modelMaxY);
		gg.drawRect(tlX, tlY, brX - tlX, brY - tlY);*/
		// Draw grid (four quadrants)
		// Top-left
		float x = 0.0f;
		float y = 0.0f;
		while (true) {
			int cx = cb.modelToCenterComponentX(x);
			int cy = cb.modelToCenterComponentY(y);
			if (x == 0.0f || y == 0.0f) {
				gg.drawLine(cx - 1, cy, cx + 1, cy);
				gg.drawLine(cx, cy - 1, cx, cy + 1);
			} else {
				gg.drawLine(cx, cy, cx, cy);
			}
			if (cy < 0 || cy > cb.actual.height) {
				break;
			}
			if (cx < 0) {
				x = 0.0f;
				y -= 1.0f;
				continue;
			}  else {
				x -= 1.0f;
			}
		}
		// Top-right
		x = 1.0f;
		y = 0.0f;
		while (true) {
			int cx = cb.modelToCenterComponentX(x);
			int cy = cb.modelToCenterComponentY(y);
			if (x == 0.0f || y == 0.0f) {
				gg.drawLine(cx - 1, cy, cx + 1, cy);
				gg.drawLine(cx, cy - 1, cx, cy + 1);
			} else {
				gg.drawLine(cx, cy, cx, cy);
			}
			if (cy < 0 || cy > cb.actual.height) {
				break;
			}
			if (cx > cb.actual.width) {
				x = 1.0f;
				y -= 1.0f;
				continue;
			} else {
				x += 1.0f;
			}
		}
		// Bottom-left
		x = 0.0f;
		y = 1.0f;
		while (true) {
			int cx = cb.modelToCenterComponentX(x);
			int cy = cb.modelToCenterComponentY(y);
			if (x == 0.0f) {
				gg.drawLine(cx - 1, cy, cx + 1, cy);
				gg.drawLine(cx, cy - 1, cx, cy + 1);
			} else {
				gg.drawLine(cx, cy, cx, cy);
			}
			if (cy < 0 || cy > cb.actual.height) {
				break;
			}
			if (cx < 0) {
				x = 0.0f;
				y += 1.0f;
				continue;
			} else {
				x -= 1.0f;
			}
		}
		// Bottom-right
		x = 1.0f;
		y = 1.0f;
		while (true) {
			int cx = cb.modelToCenterComponentX(x);
			int cy = cb.modelToCenterComponentY(y);
			gg.drawLine(cx, cy, cx, cy);
			if (cy < 0 || cy > cb.actual.height) {
				break;
			}
			if (cx > cb.actual.width) {
				x = 1.0f;
				y += 1.0f;
				continue;
			} else {
				x += 1.0f;
			}
		}
	}
	
	public void uninstallUI(JComponent c) {
	}
	
	static class NodeLayout implements LayoutManager {

		public void addLayoutComponent(String arg, Component c) {
		}

		public void layoutContainer(Container c) {
			JGraph g = (JGraph) c;
			ComputeBounds cb = new ComputeBounds();
			cb.g = g;
			cb.compute();
			int n = g.getComponentCount();
			for (int i = 0; i < n; i++) {
				Component child = g.getComponent(i);
				if (child instanceof JNode) {
					JNode node = (JNode) child;
					Dimension dim = node.getPreferredSize();
					node.setBounds(
							cb.modelToComponentX(node.getModel().getX()),
							cb.modelToComponentY(node.getModel().getY()),
							dim.width, dim.height);
				}
			}
			for (int i = 0; i < n; i++) {
				Component child = g.getComponent(i);
				if (child instanceof JEdge) {
					JEdge edge = (JEdge) child;
					edge.setBounds(BasicEdgeUI.findEnclosingRectangle(edge));
				}
			}
		}

		public Dimension minimumLayoutSize(Container c) {
			JGraph g = (JGraph) c;
			ComputeBounds cb = new ComputeBounds();
			cb.g = g;
			cb.compute();
			return new Dimension(cb.diffX, cb.diffY);
		}

		public Dimension preferredLayoutSize(Container c) {
			return minimumLayoutSize(c);
		}

		public void removeLayoutComponent(Component c) {
		}
		
	}
	
}

class ComputeBounds {
	
	static final int NODE_DIAMETER = 15;
	static final int NODE_GRIDUNIT = 22;
	
	JGraph g;
	float zoom;
	
	float modelMinX;
	float modelMaxX;
	float modelMinY;
	float modelMaxY;
	
	int maxX;
	int maxY;
	int minX;
	int minY;
	int diffX;
	int diffY;
	
	Dimension actual;
	int originX;
	int originY;
	int shiftX;
	int shiftY;
	
	float getXWithoutDelta(JNode node) {
		return node.getModel().getXWithoutDelta() * zoom;
	}
	
	float getYWithoutDelta(JNode node) {
		return node.getModel().getYWithoutDelta() * zoom;
	}
	
	int getScreenXWithoutDelta(JNode node) {
		return (int) (getXWithoutDelta(node) * NODE_GRIDUNIT);
	}
	
	int getScreenYWithoutDelta(JNode node) {
		return (int) (getYWithoutDelta(node) * NODE_GRIDUNIT);
	}
	
	int getNodeWidth() {
		// We need a special number, otherwise our edges are 1 pixel slanted
		int result = Math.round((float) NODE_DIAMETER * zoom * 2.0f) / 2;
		if (result - 1 - (result / 2) != (result / 2)) {
			result++;
		}
		return result;
	}
	
	int getNodeHeight() {
		// We need a special number, otherwise our edges are 1 pixel slanted
		return getNodeWidth();
	}
	
	int getNodeRadius() {
		return getNodeWidth() / 2;
	}
	
	void compute() {
		zoom = g.getZoomLevel();
		computeMin();
		computeShift();
	}
	
	private void computeMin() {
		maxX = Integer.MIN_VALUE;
		maxY = Integer.MIN_VALUE;
		minX = Integer.MAX_VALUE;
		minY = Integer.MAX_VALUE;
		modelMaxX = Float.NEGATIVE_INFINITY;
		modelMinX = Float.POSITIVE_INFINITY;
		modelMaxY = Float.NEGATIVE_INFINITY;
		modelMinY = Float.POSITIVE_INFINITY;
		int n = g.getComponentCount(), en = 0;
		for (int i = 0; i < n; i++) {
			Component child = g.getComponent(i);
			if (child instanceof JNode) {
				JNode node = (JNode) child;
				if ((Boolean) node.getClientProperty(JGraph.CLIENT_PROPERTY_TEMPORARY))
					continue;
				en++;
				float mx, my;
				mx = getXWithoutDelta(node);
				my = getYWithoutDelta(node);
				if (mx > modelMaxX) modelMaxX = mx;
				if (mx < modelMinX) modelMinX = mx;
				if (my > modelMaxY) modelMaxY = my;
				if (my < modelMinY) modelMinY = my;
				int x, y;
				x = getScreenXWithoutDelta(node);
				y = getScreenYWithoutDelta(node);
				if (x > maxX) maxX = x;
				if (x < minX) minX = x;
				if (y > maxY) maxY = y;
				if (y < minY) minY = y;
			}
		}
		if (en == 0) {
			maxX = maxY = minX = minY = 0;
		}
		maxX += getNodeWidth();
		maxY += getNodeHeight();
		diffX = maxX - minX;
		diffY = maxY - minY;
	}
	
	private void computeShift() {
		actual = g.getSize();
		originX = -minX;
		originY = -minY;
		if (diffX < actual.width) {
			shiftX = (actual.width - diffX) / 2;
		} else {
			shiftX = 0;
		}
		if (diffY < actual.height) {
			shiftY = (actual.height - diffY) / 2;
		} else {
			shiftY = 0;
		}
	}
	
	int modelToComponentX(float x) {
		return shiftX + originX + (int) ((x * zoom) * (float) NODE_GRIDUNIT);
	}
	
	int modelToCenterComponentX(float x) {
		return modelToComponentX(x) + getNodeWidth() / 2;
	}
	
	int modelToComponentY(float y) {
		return shiftY + originY + (int) ((y * zoom) * (float) NODE_GRIDUNIT);
	}
	
	int modelToCenterComponentY(float y) {
		return modelToComponentY(y) + getNodeHeight() / 2;
	}
	
	float componentToModelX(int x) {
		return ((x - modelToComponentX(0.0f)) / zoom) / (float) NODE_GRIDUNIT;
	}
	
	float componentToModelY(int y) {
		return ((y - modelToComponentY(0.0f)) / zoom) / (float) NODE_GRIDUNIT;
	}
	
	float centerComponentToModelX(int x) {
		return componentToModelX(x - getNodeWidth() / 2); 
	}
	
	float centerComponentToModelY(int y) {
		return componentToModelY(y - getNodeHeight() / 2);
	}
	
	float deltaX(int dx) {
		return (dx / zoom) / (float) NODE_GRIDUNIT;
	}
	
	float deltaY(int dy) {
		return (dy / zoom) / (float) NODE_GRIDUNIT;
	}
	
	float centerDeltaX(int dx) {
		return deltaX(dx - getNodeWidth() / 2);
	}
	
	float centerDeltaY(int dy) {
		return deltaY(dy - getNodeHeight() / 2);
	}
	
}
