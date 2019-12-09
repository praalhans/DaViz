package com.aexiz.daviz.ui;

import java.awt.Color;
import java.awt.Component;
import java.io.Serializable;
import java.util.EventObject;

import javax.swing.JComponent;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.aexiz.daviz.ui.GraphModel.EdgeModel;
import com.aexiz.daviz.ui.GraphModel.ModeEventListener;
import com.aexiz.daviz.ui.GraphModel.NodeModel;
import com.aexiz.daviz.ui.plaf.GraphUI;
import com.aexiz.daviz.ui.plaf.basic.BasicGraphUI;

/**
 * A graph component that visualizes and allows user-interactive editing of graph structures. 
 */
public class JGraph extends JComponent {

	private static final long serialVersionUID = 2243441260190095826L;
	
	public static final String CLIENT_PROPERTY_TEMPORARY = "temporary";
	
	public static final int MODE_SELECTION = GraphModel.MODE_SELECTION;
	public static final int MODE_EDGE = GraphModel.MODE_EDGE;
	public static final int MODE_VERTEX = GraphModel.MODE_VERTEX;
	public static final int MODE_ERASE = GraphModel.MODE_ERASE;
	
	static final String UICLASSID = "GraphUI";
	
	protected GraphModel model;
	protected ObjectSelectionModel selectionModel;
	
	private Handler handler;
	protected ChangeListener changeListener;
	
	protected transient ChangeEvent changeEvent;
	protected transient EventObject modeEvent;
	
	static {
		UIDefaults def = UIManager.getDefaults();
		if (def.get(UICLASSID) == null)
			def.put(UICLASSID, BasicGraphUI.class.getName());
	}
	
	public JGraph() {
		setOpaque(true);
		setModel(new DefaultGraphModel());
		setSelectionModel(new DefaultObjectSelectionModel());
		updateUI();
	}
	
	public static class JNode extends JKnob {

		private static final long serialVersionUID = -3614183062524808053L;
		
		private NodeModel model;
		
		private ChangeListener changeListener = new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				fireStateChanged();
				revalidate();
				getParent().repaint();
			}
		};
		
		protected JNode() {
		}
		
		public void setModel(NodeModel model) {
			NodeModel oldModel = this.model;
			if (oldModel != null) {
				oldModel.removeChangeListener(changeListener);
			}
			this.model = model;
			if (model != null) {
				model.addChangeListener(changeListener);
			}
			firePropertyChange("model", oldModel, model);
		}
		
		public NodeModel getModel() {
			return model;
		}
		
		public JGraph getGraph() {
			Component c = getParent();
			if (c instanceof JGraph)
				return (JGraph) c;
			return null;
		}
		
		public boolean isPressed() {
			if (model == null)
				return false;
			return model.isPressed();
		}
		
		public String getLabel() {
			if (model == null)
				return "?";
			return model.getLabel();
		}
		
		public void remove() {
			if (model == null)
				return;
			model.remove();
		}
		
		public void setPressed(boolean b) {
			if (model == null) return;
			model.setPressed(b);
		}
		
		public void setDeltaX(float dx) {
			if (model == null) return;
			model.setDeltaX(dx);
		}
		
		public void setDeltaY(float dy) {
			if (model == null) return;
			model.setDeltaY(dy);
		}
		
		public boolean isSelected() {
			JGraph g = getGraph();
			if (g == null) return false;
			ObjectSelectionModel selModel = g.getSelectionModel();
			if (selModel == null) return false;
			return selModel.isSelected(model);
		}
		
		public void setSelected(boolean sel) {
			if (isSelected() == sel) return;
			JGraph g = getGraph();
			if (g == null) return;
			ObjectSelectionModel selModel = g.getSelectionModel();
			if (selModel != null) {
				if (sel) {
					selModel.addSelection(model);
				} else {
					selModel.removeSelection(model);
				}
			}
		}
		
		public void requestSingleSelected() {
			JGraph g = getGraph();
			if (g == null)
				return;
			ObjectSelectionModel selModel = g.getSelectionModel();
			if (selModel != null) {
				selModel.clearSelection();
				selModel.addSelection(model);
			}
		}
		
		public void requestAllSelected() {
			JGraph graph = getGraph();
			if (graph != null) {
				graph.selectAllNodes();
			}
		}
		
		public void requestClearSelection() {
			JGraph graph = getGraph();
			if (graph != null) {
				graph.clearSelection();
			}
		}
		
		private float oldx, oldy;
		
		public void startMoving() {
			if (model != null) {
				oldx = model.getX();
				oldy = model.getY();
			} else {
				oldx = 0.0f;
				oldy = 0.0f;
			}
		}
		
		public boolean commitMoving() {
			if (model == null)
				return false;
			model.validate();
			if (model.isOverlapping()) {
				model.setX(oldx);
				model.setY(oldy);
				return false;
			} else {
				return true;
			}
		}
		
	}
	
	public static class JEdge extends JKnob {

		private static final long serialVersionUID = -3116847790422523979L;
		
		private EdgeModel model;
		
		private ChangeListener changeListener = new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				fireStateChanged();
				revalidate();
			}
		};
		
		protected JEdge() {
		}
		
		public void setModel(EdgeModel model) {
			EdgeModel oldModel = this.model;
			if (oldModel != null) {
				oldModel.removeChangeListener(changeListener);
			}
			this.model = model;
			if (model != null) {
				model.addChangeListener(changeListener);
			}
		}
		
		public EdgeModel getModel() {
			return model;
		}
		
		public JGraph getGraph() {
			Component c = getParent();
			if (c instanceof JGraph)
				return (JGraph) c;
			return null;
		}
		
		public JNode getFrom() {
			JGraph g = getGraph();
			if (g != null)
				return g.findNodeComponent(model.getFrom());
			return null;
		}
		
		public JNode getTo() {
			JGraph g = getGraph();
			if (g != null)
				return g.findNodeComponent(model.getTo());
			return null;
		}
		
		public void remove() {
			if (model == null)
				return;
			model.remove();
		}
		
		public boolean isDirected() {
			if (model != null)
				return model.isDirected();
			return false;
		}
		
		public boolean isSelected() {
			JGraph g = getGraph();
			if (g == null)
				return false;
			ObjectSelectionModel selModel = g.getSelectionModel();
			if (selModel != null) {
				return selModel.isSelected(model);
			} else {
				return false;
			}
		}
		
		public void setSelected(boolean sel) {
			if (isSelected() == sel)
				return;
			JGraph g = getGraph();
			if (g == null)
				return;
			ObjectSelectionModel selModel = g.getSelectionModel();
			if (selModel != null) {
				if (sel) {
					selModel.addSelection(model);
				} else {
					selModel.removeSelection(model);
				}
			}
		}
		
		public void requestSingleSelected() {
			JGraph g = getGraph();
			if (g == null)
				return;
			ObjectSelectionModel selModel = g.getSelectionModel();
			if (selModel != null) {
				selModel.clearSelection();
				selModel.addSelection(model);
			}
		}
		
		public void requestAllSelected() {
			JGraph graph = getGraph();
			if (graph != null) {
				graph.selectAllEdges();
			}
		}
		
		public void requestClearSelection() {
			JGraph graph = getGraph();
			if (graph != null) {
				graph.clearSelection();
			}
		}
		
	}
	
	public void setModel(GraphModel newModel) {
		GraphModel oldModel = getModel();
		if (oldModel != null) {
			oldModel.removeChangeListener(changeListener);
			changeListener = null;
		}
		model = newModel;
		if (newModel != null) {
			changeListener = createChangeListener();
			newModel.addChangeListener(changeListener);
		}
		firePropertyChange("model", oldModel, newModel);
		if (newModel != oldModel) {
			updateSubComponents();
		}
	}
	
	public GraphModel getModel() {
		return model;
	}
	
	public void setSelectionModel(ObjectSelectionModel selModel) {
		ObjectSelectionModel oldModel = getSelectionModel();
		if (oldModel != null) {
			oldModel.removeChangeListener(changeListener);
			changeListener = null;
		}
		selectionModel = selModel;
		if (selModel != null) {
			changeListener = createChangeListener();
			selModel.addChangeListener(changeListener);
		}
		firePropertyChange("selectionModel", oldModel, selModel);
		if (selModel != oldModel) {
			repaint();
		}
	}
	
	public ObjectSelectionModel getSelectionModel() {
		return selectionModel;
	}
	
	public void clearSelection() {
		if (selectionModel != null)
			selectionModel.clearSelection();
	}
	
	public void selectAllEdges() {
		if (selectionModel != null && model != null) {
			for (EdgeModel edge : model.getValidEdge()) {
				selectionModel.addSelection(edge);
			}
		}
	}
	
	public void selectAllNodes() {
		if (selectionModel != null && model != null) {
			for (NodeModel node : model.getValidNode()) {
				selectionModel.addSelection(node);
			}
		}
	}
	
	public void selectAll() {
		if (selectionModel != null && model != null) {
			selectionModel.clearSelection();
			selectAllNodes();
			selectAllEdges();
		}
	}
	
	public void removeSelection() {
		if (selectionModel != null && model != null) {
			for (Object sel : selectionModel.getSelection()) {
				if (sel instanceof EdgeModel) {
					EdgeModel edge = (EdgeModel) sel;
					if (edge.getParent() == model) {
						edge.remove();
					}
				}
				if (sel instanceof NodeModel) {
					NodeModel node = (NodeModel) sel;
					if (node.getParent() == model) {
						node.remove();
					}
				}
			}
		}
	}
	
	private Color readOnlyBackground;
	
	public void setReadOnlyBackground(Color color) {
		Color old = readOnlyBackground;
		readOnlyBackground = color;
		firePropertyChange("readOnlyBackground", old, color);
	}
	
	public Color getReadOnlyBackground() {
		return readOnlyBackground;
	}
	
	public void setReadOnly(boolean b) {
		if (model == null) return;
		model.setReadOnly(b);
	}
	
	public boolean isReadOnly() {
		if (model == null) return true;
		return model.isReadOnly();
	}
	
	public void setEditMode(int mode) {
		if (model == null)
			return;
		model.setEditMode(mode);
	}
	
	public void switchToSelectionMode() {
		if (model == null)
			return;
		model.setEditMode(MODE_SELECTION);
	}
	
	public void switchToVertexMode() {
		if (model == null)
			return;
		model.setEditMode(MODE_VERTEX);
	}
	
	public void switchToEdgeMode() {
		if (model == null)
			return;
		model.setEditMode(MODE_EDGE);
	}
	
	public void switchToEraseMode() {
		if (model == null)
			return;
		model.setEditMode(MODE_ERASE);
	}
	
	public int getEditMode() {
		if (model == null)
			return MODE_SELECTION;
		else
			return model.getEditMode();
	}
	
	public JNode startCreatingNode(float x, float y) {
		if (model == null)
			return null;
		NodeModel node = model.createNode(x, y);
		model.setTemporaryNode(node);
		NodeModel result = model.getTemporaryNode();
		updateSubComponents();
		revalidate();
		repaint();
		return findNodeComponent(result);
	}
	
	public boolean commitCreatingNode(JNode node) {
		boolean result = commitCreatingNodeImpl(node);
		if (!result) {
			JStatus.setTemporaryStatus(this, "A new node cannot overlap an existing one.");
		}
		return result;
	}
	
	private boolean commitCreatingNodeImpl(JNode node) {
		if (model == null)
			return false;
		if (model.getTemporaryNode() != node.getModel())
			return false;
		boolean result;
		if (!node.commitMoving()) {
			result = false;
		} else {
			result = true;
		}
		if (result) {
			model.addNode(node.getModel());
		}
		model.clearTemporaryNode();
		updateSubComponents();
		revalidate();
		repaint();
		return result;
	}
	
	public JNode findNearestNode(float x, float y) {
		if (model == null)
			return null;
		NodeModel node = model.findNearestValidNode(x, y);
		if (node == null)
			return null;
		return findNodeComponent(node);
	}
	
	public JNode startCreatingEdgeNode(float x, float y) {
		JNode node = startCreatingNode(x, y);
		JNode result;
		if (!commitCreatingNodeImpl(node)) {
			result = findNearestNode(x, y);
		} else {
			result = node;
		}
		return result;
	}
	
	public JNode startCreatingEdge(JNode from, float x, float y) {
		if (model == null)
			return null;
		NodeModel node = model.createNode(x, y);
		model.setTemporaryNode(node);
		EdgeModel edge = model.createEdge(from.getModel(), node);
		model.setTemporaryEdge(edge);
		updateSubComponents();
		revalidate();
		repaint();
		return findNodeComponent(model.getTemporaryNode());
	}
	
	public boolean commitCreatingEdge(JNode to) {
		if (model == null)
			return false;
		if (model.getTemporaryNode() != to.getModel())
			return false;
		EdgeModel edge = model.getTemporaryEdge();
		if (edge == null || edge.getTo() != to.getModel())
			return false;
		boolean result;
		float oldx = to.getModel().getX();
		float oldy = to.getModel().getY();
		if (!to.commitMoving()) {
			// Find existing nearest node to connect with
			NodeModel from = edge.getFrom();
			NodeModel node = model.findNearestValidNode(oldx, oldy);
			if (node == null || node == from) {
				JStatus.setTemporaryStatus(this, "Loops are not allowed.");
				result = false;
			} else {
				edge = model.createEdge(from, node);
				result = true;
			}
		} else {
			model.addNode(to.getModel());
			result = true;
		}
		if (result) {
			model.addEdge(edge);
		}
		model.clearTemporaryNode();
		model.clearTemporaryEdge();
		updateSubComponents();
		revalidate();
		repaint();
		return result;
	}
	
	private boolean showGrid = false;
	
	public void setShowGrid(boolean show) {
		boolean old = showGrid;
		showGrid = show;
		if (old != show) {
			firePropertyChange("showGrid", old, show);
		}
	}
	
	public boolean getShowGrid() {
		return showGrid;
	}
	
	private boolean erasing;
	
	public void setErasing(boolean erasing) {
		boolean old = this.erasing;
		this.erasing = erasing;
		if (old != erasing) {
			firePropertyChange("erasing", old, erasing);
		}
	}
	
	public boolean isErasing() {
		return erasing;
	}
	
	public void zoomIn() {
		if (model == null)
			return;
		float zoom = model.getZoomLevel();
		if (zoom > 4.5f) {
			// Too deep
			return;
		}
		zoom *= 1.25f;
		zoom = Math.round(zoom * 100f) / 100f;
		model.setZoomLevel(zoom);
	}
	
	public void zoomOut() {
		if (model == null)
			return;
		float zoom = model.getZoomLevel();
		if (zoom < 0.2f) {
			// Too deep
			return;
		}
		zoom /= 1.25f;
		zoom = Math.round(zoom * 100f) / 100f;
		model.setZoomLevel(zoom);
	}
	
	public float getZoomLevel() {
		if (model == null)
			return 1.0f;
		return model.getZoomLevel();
	}
	
	public void updateUI() {
		setUI((GraphUI) UIManager.getUI(this));
	}
	
	public void setUI(GraphUI ui) {
		removeSubComponents();
		super.setUI(ui);
		updateSubComponents();
	}
	
	public GraphUI getUI() {
		return (GraphUI) ui;
	}
	
	public String getUIClassID() {
		return UICLASSID;
	}
	
	public boolean isOptimizedDrawingEnabled() {
		return false;
	}
	
	public void addChangeListener(ChangeListener l) {
		listenerList.add(ChangeListener.class, l);
	}
	
	public void removeChangeListener(ChangeListener l) {
		listenerList.remove(ChangeListener.class, l);
	}
	
	public ChangeListener[] getChangeListeners() {
		return listenerList.getListeners(ChangeListener.class);
	}
	
	protected void fireStateChanged() {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ChangeListener.class) {
				if (changeEvent == null)
					changeEvent = new ChangeEvent(this);
				((ChangeListener) listeners[i+1]).stateChanged(changeEvent);
			}
		}
	}
	
	public void addModeEventListener(ModeEventListener l) {
		listenerList.add(ModeEventListener.class, l);
	}
	
	public void removeModeEventListener(ModeEventListener l) {
		listenerList.remove(ModeEventListener.class, l);
	}
	
	public ModeEventListener[] getModeEventListeners() {
		return listenerList.getListeners(ModeEventListener.class);
	}
	
	protected void fireModeChanged() {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ModeEventListener.class) {
				if (modeEvent == null)
					modeEvent = new EventObject(this);
				((ModeEventListener) listeners[i+1]).modeChanged(modeEvent);
			}
		}
	}
	
	private void removeSubComponents() {
		int n = getComponentCount();
		for (int i = 0; i < n; i++) {
			Component c = getComponent(i);
			if (c instanceof JNode) {
				((JNode) c).setModel(null);
				remove(i--); n--;
			} else if (c instanceof JEdge) {
				((JEdge) c).setModel(null);
				remove(i--); n--;
			}
		}
	}
	
	private void updateSubComponents() {
		// We must not just remove all children and then readd them. This interferes with
		// event handling, e.g. dragging a node and then removing it makes the component very brittle.
		// 1. Find and remove all components that have models not occurring in the graph model.
		// 2. Find all node models that do not have a component, and add a new component for them.
		// 3. Special temporary node handling.
		// This should result in the same number of components, reusing non-changed models.
		int n = getComponentCount();
		Component[] children = new Component[n];
		JNode[] nodes = new JNode[n];
		JEdge[] edges = new JEdge[n];
		boolean[] remove = new boolean[n];
		for (int i = 0; i < n; i++) {
			children[i] = getComponent(i);
			if (children[i] instanceof JNode) {
				nodes[i] = (JNode) children[i];
			} else if (children[i] instanceof JEdge) {
				edges[i] = (JEdge) children[i];
			}
		}
		GraphModel model = getModel();
		NodeModel[] nodeModels = model.getValidNode();
		boolean[] nodeMatch = new boolean[nodeModels.length];
		boolean nodeTmpMatch = false;
		EdgeModel[] edgeModels = model.getValidEdge();
		boolean[] edgeMatch = new boolean[edgeModels.length];
		boolean edgeTmpMatch = false;
outer:	for (int i = 0; i < n; i++) {
			if (nodes[i] == null) continue;
			NodeModel node = nodes[i].getModel();
			for (int j = 0; j < nodeModels.length; j++) {
				if (nodeModels[j] == node) {
					nodeMatch[j] = true;
					nodes[i].putClientProperty(CLIENT_PROPERTY_TEMPORARY, false);
					continue outer;
				}
			}
			if (node == model.getTemporaryNode()) {
				nodeTmpMatch = true;
				nodes[i].putClientProperty(CLIENT_PROPERTY_TEMPORARY, true);
				continue;
			}
			nodes[i].setModel(null);
			remove[i] = true;
		}
outer:	for (int i = 0; i < n; i++) {
			if (edges[i] == null) continue;
			EdgeModel edge = edges[i].getModel();
			for (int j = 0; j < edgeModels.length; j++) {
				if (edgeModels[j] == edge) {
					edgeMatch[j] = true;
					edges[i].putClientProperty(CLIENT_PROPERTY_TEMPORARY, false);
					continue outer;
				}
			}
			if (edge == model.getTemporaryEdge()) {
				edgeTmpMatch = true;
				edges[i].putClientProperty(CLIENT_PROPERTY_TEMPORARY, true);
				continue;
			}
			edges[i].setModel(null);
			remove[i] = true;
		}
		for (int i = 0, j = 0; i < n; i++, j++) {
			if (remove[i]) remove(j--);
		}
		GraphUI ui = getUI();
		for (int i = 0; i < nodeModels.length; i++) {
			if (nodeMatch[i]) continue;
			JNode c = createNodeComponent(nodeModels[i]);
			c.putClientProperty(CLIENT_PROPERTY_TEMPORARY, false);
			if (ui != null)
				ui.configureNodeComponent(c);
			add(c, 0);
		}
		NodeModel tn = model.getTemporaryNode();
		if (tn != null && !nodeTmpMatch) {
			JNode c = createNodeComponent(tn);
			c.putClientProperty(CLIENT_PROPERTY_TEMPORARY, true);
			if (ui != null)
				ui.configureNodeComponent(c);
			add(c, 0);
		}
		for (int i = 0; i < edgeModels.length; i++) {
			if (edgeMatch[i]) continue;
			JEdge c = createEdgeComponent(edgeModels[i]);
			c.putClientProperty(CLIENT_PROPERTY_TEMPORARY, false);
			if (ui != null)
				ui.configureEdgeComponent(c);
			add(c);
		}
		EdgeModel te = model.getTemporaryEdge();
		if (te != null && !edgeTmpMatch) {
			JEdge c = createEdgeComponent(te);
			c.putClientProperty(CLIENT_PROPERTY_TEMPORARY, true);
			if (ui != null)
				ui.configureEdgeComponent(c);
			add(c);
		}
	}
	
	public JNode findNodeComponent(NodeModel node) {
		int n = getComponentCount();
		for (int i = 0; i < n; i++) {
			Component c = getComponent(i);
			if (c instanceof JNode) {
				JNode e = (JNode) c;
				if (e.getModel() == node)
					return e;
			}
		}
		return null;
	}
	
	public JEdge findEdgeComponent(EdgeModel edge) {
		int n = getComponentCount();
		for (int i = 0; i < n; i++) {
			Component c = getComponent(i);
			if (c instanceof JEdge) {
				JEdge m = (JEdge) c;
				if (m.getModel() == edge)
					return m;
			}
		}
		return null;
	}
	
	protected JNode createNodeComponent(NodeModel node) {
		JNode result = new JNode();
		result.setModel(node);
		return result;
	}
	
	protected JEdge createEdgeComponent(EdgeModel edge) {
		JEdge result = new JEdge();
		result.setModel(edge);
		return result;
	}
	
	protected ChangeListener createChangeListener() {
		return getHandler();
	}
	
	private Handler getHandler() {
		if (handler == null) {
			handler = new Handler();
		}
		return handler;
	}
	
	class Handler implements ChangeListener, Serializable {
		
		private static final long serialVersionUID = 8926957207286243301L;

		public void stateChanged(ChangeEvent e) {
			if (e.getSource() == model) {
				updateSubComponents();
				revalidate();
				repaint();
				fireStateChanged();
			} else if (e.getSource() == selectionModel) {
				repaint();
			}
		}
		
	}
	
}
