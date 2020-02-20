package com.aexiz.daviz.ui;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

public class DefaultGraphModel implements GraphModel {

	private EventListenerList listenerList = new EventListenerList();
	
	public void addChangeListener(ChangeListener l) {
		listenerList.add(ChangeListener.class, l);
	}
	
	public void removeChangeListener(ChangeListener l) {
		listenerList.remove(ChangeListener.class, l);
	}
	
	protected transient ChangeEvent changeEvent;
	
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
	
	protected transient EventObject modeEvent;
	
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
	
	class DefaultNodeModel implements NodeModel {
		
		protected float x, y, dx, dy;
		protected boolean pressed;
		protected String label = "?";
		protected ArrayList<String> incomingNodes = new ArrayList<>();
		protected ArrayList<String> outgoingNodes = new ArrayList<>();
		
		DefaultNodeModel(float x, float y) {
			this.x = x;
			this.y = y;
		}
		
		public DefaultGraphModel getParent() {
			return DefaultGraphModel.this;
		}
		
		private EventListenerList listenerList = new EventListenerList();
		
		public void addChangeListener(ChangeListener l) {
			listenerList.add(ChangeListener.class, l);
		}
		
		public void removeChangeListener(ChangeListener l) {
			listenerList.remove(ChangeListener.class, l);
		}
		
		protected transient ChangeEvent changeEvent;
		
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

		public void setX(float x) {
			if (x != x) x = 0.0f;
			this.x = x;
			this.dx = 0.0f;
			fireStateChanged();
		}
		
		public void setDeltaX(float dx) {
			if (dx != dx) dx = 0.0f;
			this.dx = dx;
			fireStateChanged();
		}

		public float getX() {
			return x + dx;
		}
		
		public float getXWithoutDelta() {
			return x;
		}

		public void setY(float y) {
			if (y != y) y = 0.0f;
			this.y = y;
			this.dy = 0.0f;
			fireStateChanged();
		}
		
		public void setDeltaY(float dy) {
			if (dy != dy) dy = 0.0f;
			this.dy = dy;
			fireStateChanged();
		}

		public float getY() {
			return y + dy;
		}
		
		public float getYWithoutDelta() {
			return y;
		}
		
		public boolean isPressed() {
			return pressed;
		}
		
		public void setPressed(boolean p) {
			pressed = p;
			fireStateChanged();
		}
		
		public void validate() {
			if (isSnapToGrid()) {
				setX(Math.round(getX()));
				setY(Math.round(getY()));
			} else {
				setX(getX());
				setY(getY());
			}
		}

		public void setLabel(String label) {
			if (label == null) throw null;
			this.label = label;
		}

		public String getLabel() {
			return label;
		}
		
		public boolean isOverlapping() {
			for(NodeModel n : getValidNode()) {
				if (n == this) continue;
				if (n.getX() == getX() && n.getY() == getY())
					return true;
			}
			return false;
		}
		
		public void remove() {
			removeNode(this);
		}
		
		public String toString() {
			return getLabel();
		}
		
		@Override
		public void addIncomingNode(String node) {
			incomingNodes.add(node);
		}
		
		@Override
		public void removeIncomingNode(String node) {
			incomingNodes.remove(node);
		}
		
		@Override
		public ArrayList<String> getIncomingNodes() {
			return incomingNodes;
		}

		@Override
		public void addOutgoingNode(String node) {
			outgoingNodes.add(node);	
		}

		@Override
		public void removeOutgoingNode(String node) {
			outgoingNodes.remove(node);
		}

		@Override
		public ArrayList<String> getOutgoingNodes() {
			return outgoingNodes;
		}
	}
	
	class DefaultEdgeModel implements EdgeModel {
		
		protected DefaultNodeModel from;
		protected DefaultNodeModel to;
		
		private boolean directed;
		
		DefaultEdgeModel(DefaultNodeModel from, DefaultNodeModel to) {
			if (from.getParent() != DefaultGraphModel.this
					|| to.getParent() != DefaultGraphModel.this)
				throw new IllegalArgumentException();
			this.from = from;
			this.to = to;
		}
		
		public DefaultGraphModel getParent() {
			return DefaultGraphModel.this;
		}
		
		private EventListenerList listenerList = new EventListenerList();
		
		public void addChangeListener(ChangeListener l) {
			listenerList.add(ChangeListener.class, l);
		}
		
		public void removeChangeListener(ChangeListener l) {
			listenerList.remove(ChangeListener.class, l);
		}
		
		protected transient ChangeEvent changeEvent;
		
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
		
		public boolean isDirected() {
			return directed;
		}
		
		public void setDirected(boolean directed) {
			this.directed = directed;
			fireStateChanged();
		}
		
		public NodeModel getFrom() {
			return from;
		}
		
		public NodeModel getTo() {
			return to;
		}
		
		public void validate() {
		}
		
		public void remove() {
			removeEdge(this);
		}
		
	}
	
	private Iterable<String> generateDefaultNames() {
		return new Iterable<String>() {
			public Iterator<String> iterator() {
				return new Iterator<String>() {
					int index = 0;
					char[] set = {'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
					public boolean hasNext() {
						return index >= 0;
					}
					public String next() {
						String result = "";
						int index = this.index;
						while (index >= set.length) {
							result = String.valueOf(set[index % set.length]) + result;
							index = (index / set.length) - 1;
						}
						if (index < set.length) {
							result = String.valueOf(set[index]) + result;
						}
						this.index++;
						return result;
					}
				};
			}
		};
	}

	public DefaultNodeModel createNode(float x, float y) {
		DefaultNodeModel result = new DefaultNodeModel(x, y);
		// Find default name
		for (String name : generateDefaultNames()) {
			if (findNodeByLabel(name) == null) {
				result.setLabel(name);
				break;
			}
		}
		return result;
	}

	public DefaultEdgeModel createEdge(NodeModel from, NodeModel to) {
		return new DefaultEdgeModel((DefaultNodeModel) from, (DefaultNodeModel) to);
	}

	private ArrayList<DefaultNodeModel> nodes = new ArrayList<>();
	
	public int getNodeCount() {
		return nodes.size();
	}
	
	public NodeModel getNode(int index) {
		return nodes.get(index);
	}
	
	public NodeModel[] getNode() {
		return nodes.toArray(new NodeModel[nodes.size()]);
	}
	
	@Override
	public NodeModel getRandomNode() {
		int index = (int)(Math.random() * getNodeCount()) + 0;
		return nodes.get(index);
	}
	
	public void addNode(NodeModel n) {
		DefaultNodeModel dn = (DefaultNodeModel) n;
		if (dn.getParent() != this)
			throw new IllegalArgumentException();
		if (nodes.contains(n))
			return;
		nodes.add(dn);
		if (dn == temporaryNode) {
			temporaryNode = null;
		}
		fireStateChanged();
	}
	
	public void removeNode(NodeModel n) {
		DefaultNodeModel dn = (DefaultNodeModel) n;
		if (dn.getParent() != this)
			throw new IllegalArgumentException();
		if (!nodes.contains(n))
			throw new IllegalArgumentException();
		removeNodeFromAdjancet(dn);
		nodes.remove(n);
		fireStateChanged();
	}
	
	public DefaultNodeModel findNodeByLabel(String label) {
		for (DefaultNodeModel n : nodes) {
			if (n.getLabel().equals(label))
				return n;
		}
		return null;
	}
	
	private ArrayList<DefaultEdgeModel> edges = new ArrayList<>();

	public int getEdgeCount() {
		return edges.size();
	}

	public EdgeModel getEdge(int index) {
		return edges.get(index);
	}

	public EdgeModel[] getEdge() {
		return edges.toArray(new EdgeModel[edges.size()]);
	}
	
	public void addEdge(EdgeModel e) {
		DefaultEdgeModel de = (DefaultEdgeModel) e;
		if (de.getParent() != this)
			throw new IllegalArgumentException();
		DefaultEdgeModel removeA = null, removeB = null;
		
		setNodesAsAdjacent(de.getFrom(), de.getTo(), de.isDirected());
		/* Verifies if the new edge already exists */
		if (e.isDirected()) {
			for (DefaultEdgeModel f : edges) {
				if (f.isDirected() && f.from == de.from && f.to == de.to)
					return;
				if (!f.isDirected() && (f.from == de.from && f.to == de.to || f.from == de.to && f.to == de.from))
					return;
			}
		} else {
			for (DefaultEdgeModel f : edges) {
				/* Replace a previously unidirectional edge with the new bidirectional edge */
				if (f.isDirected() && (f.from == de.from && f.to == de.to || f.from == de.to && f.to == de.from)) {
					if (removeA == null) removeA = f;
					else removeB = f;
					break;
				}
				if (!f.isDirected() && (f.from == de.from && f.to == de.to || f.from == de.to && f.to == de.from))
					return;
			}
		}
		if (removeA != null) edges.remove(removeA);
		if (removeB != null) edges.remove(removeB);
		edges.add(de);
		if (de == temporaryEdge) {
			temporaryEdge = null;
		}
		fireStateChanged();
	}
	
	public void removeEdge(EdgeModel e) {
		DefaultEdgeModel de = (DefaultEdgeModel) e;
		if (de.getParent() != this)
			throw new IllegalArgumentException();
		if (!edges.contains(e))
			throw new IllegalArgumentException();
		edges.remove(e);
		fireStateChanged();
	}
	
	public NodeModel[] getValidNode() {
		return getNode();
	}
	
	public EdgeModel[] getValidEdge() {
		ArrayList<DefaultEdgeModel> result = new ArrayList<>();
		for (DefaultEdgeModel e : edges) {
			if (nodes.contains(e.from) && nodes.contains(e.to))
				result.add(e);
		}
		return result.toArray(new EdgeModel[result.size()]);
	}
	
	private boolean snapToGrid = false;
	
	public void setSnapToGrid(boolean snap) {
		if (snapToGrid != snap) {
			snapToGrid = snap;
			validate();
		}
	}
	
	public boolean isSnapToGrid() {
		return snapToGrid;
	}
	
	public void validate() {
		for (EdgeModel edge : getValidEdge())
			edge.validate();
		for (NodeModel node : getValidNode())
			node.validate();
	}

	public void clear() {
		edges.clear();
		nodes.clear();
		fireStateChanged();
	}
	
	private int editMode = MODE_SELECTION;
	
	public void setEditMode(int mode) {
		if (mode != MODE_SELECTION && mode != MODE_EDGE && mode != MODE_VERTEX && mode != MODE_ERASE)
			throw new IllegalArgumentException();
		int oldmode = editMode;
		editMode = mode;
		if (mode != oldmode) {
			if (oldmode == MODE_VERTEX) {
				clearTemporaryNode();
			} else if (oldmode == MODE_EDGE) {
				clearTemporaryEdge();
				clearTemporaryNode();
			}
			fireModeChanged();
		}
	}
	
	public int getEditMode() {
		return editMode;
	}
	
	private DefaultNodeModel temporaryNode;
	
	public void setTemporaryNode(NodeModel n) {
		if (editMode != MODE_EDGE && editMode != MODE_VERTEX)
			return;
		DefaultNodeModel dn = (DefaultNodeModel) n;
		if (dn.getParent() != this)
			throw new IllegalArgumentException();
		temporaryNode = dn;
	}
	
	public NodeModel getTemporaryNode() {
		return temporaryNode;
	}
	
	public static final float SNAP_DISTANCE = 1.0f;
	
	public NodeModel findNearestValidNodeToTemporary() {
		if (temporaryNode == null)
			return null;
		float fx = temporaryNode.getX();
		float fy = temporaryNode.getY();
		return findNearestValidNode(fx, fy);
	}
	
	public NodeModel findNearestValidNode(float fx, float fy) {
		float dist = Float.MAX_VALUE;
		NodeModel result = null;
		for (NodeModel n : getValidNode()) {
			float tx = n.getX();
			float ty = n.getY();
			float d = (fx - tx) * (fx - tx) + (fy - ty) * (fy - ty);
			if (d < dist) {
				dist = d;
				result = n;
			}
		}
		if (dist <= SNAP_DISTANCE) return result;
		else return null;
	}
	
	public void clearTemporaryNode() {
		temporaryNode = null;
	}
	
	private DefaultEdgeModel temporaryEdge;
	
	public void setTemporaryEdge(EdgeModel e) {
		if (editMode != MODE_EDGE)
			return;
		DefaultEdgeModel de = (DefaultEdgeModel) e;
		if (de.getParent() != this)
			throw new IllegalArgumentException();
		temporaryEdge = de;
	}
	
	public EdgeModel getTemporaryEdge() {
		return temporaryEdge;
	}
	
	public void clearTemporaryEdge() {
		temporaryEdge = null;
	}
	
	private float zoom = 1.0f;
	
	public void setZoomLevel(float zoom) {
		if (zoom < 0.0f || zoom != zoom) zoom = 1.0f;
		this.zoom = zoom;
		fireStateChanged();
	}
	
	public float getZoomLevel() {
		return zoom;
	}

	public boolean isAcyclic() {
		Map<String, Boolean> visited = createNodeVisitedMap();
		for (NodeModel node : nodes) {
			if (!visited.get(node.getLabel()) && isAcyclicHelper(node, "", visited)) {
				return false;
			}
		}
		return true;
	}
	
	private Map<String, Boolean> createNodeVisitedMap() {
		Map<String, Boolean> visited = new HashMap<String, Boolean>();

		for (NodeModel node : nodes) {
			visited.put(node.getLabel(), false);
		}

		return visited;
	}
	
	private boolean isAcyclicHelper(NodeModel node, String parent, Map<String, Boolean> visited) {
		visited.put(node.getLabel(), true);
		for (String outgoingNodeLabel : node.getOutgoingNodes()) {
			if (!visited.get(outgoingNodeLabel)) {
				DefaultNodeModel outgoingNode = findNodeByLabel(outgoingNodeLabel);
				if (isAcyclicHelper(outgoingNode, node.getLabel(), visited)) {
					return true;
				}
			} else if (outgoingNodeLabel != parent) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isEmpty() {
		return nodes.size() == 0;
	}

	private boolean readOnly;
	
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
		fireStateChanged();
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	protected void setNodesAsAdjacent(NodeModel from, NodeModel to, boolean directed) {
		NodeModel fromInNodes = findNodeByLabel(from.getLabel());
		NodeModel toInNodes = findNodeByLabel(to.getLabel());
		
		fromInNodes.addOutgoingNode(to.getLabel());
		toInNodes.addIncomingNode(from.getLabel());
		
		if (!directed) {
			fromInNodes.addIncomingNode(to.getLabel());
			toInNodes.addOutgoingNode(from.getLabel());
		}
	}
	
	protected void removeNodeFromAdjancet(NodeModel node) {
		for (String label : node.getIncomingNodes()) {
			NodeModel incomingNode = findNodeByLabel(label);
			incomingNode.removeOutgoingNode(node.getLabel());
			incomingNode.removeIncomingNode(node.getLabel());
		}
	}
}
