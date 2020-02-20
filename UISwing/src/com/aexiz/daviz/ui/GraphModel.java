package com.aexiz.daviz.ui;

import javax.swing.event.ChangeListener;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;

public interface GraphModel {

    static final int MODE_SELECTION = 0;
    static final int MODE_VERTEX = 1;
    static final int MODE_EDGE = 2;
    static final int MODE_ERASE = 3;

    public void addChangeListener(ChangeListener l);

    public void removeChangeListener(ChangeListener l);

    public void addModeEventListener(ModeEventListener l);

    public void removeModeEventListener(ModeEventListener l);

    public NodeModel createNode(float x, float y);

    public EdgeModel createEdge(NodeModel from, NodeModel to);

    public int getNodeCount();

    public NodeModel getNode(int index);

    public NodeModel[] getNode();

    public NodeModel getRandomNode();

    public void addNode(NodeModel n);

    public void removeNode(NodeModel n);

    public int getEdgeCount();

    public EdgeModel getEdge(int index);

    public void addEdge(EdgeModel e);

    public void removeEdge(EdgeModel e);

    public EdgeModel[] getEdge();

    public NodeModel[] getValidNode();

    public EdgeModel[] getValidEdge();

    public boolean isSnapToGrid();

    public void setSnapToGrid(boolean snap);

    public int getEditMode();

    public void setEditMode(int mode);

    public NodeModel getTemporaryNode();

    public void setTemporaryNode(NodeModel n);

    public NodeModel findNearestValidNodeToTemporary();

    public NodeModel findNearestValidNode(float fx, float fy);

    public void clearTemporaryNode();

    public EdgeModel getTemporaryEdge();

    public void setTemporaryEdge(EdgeModel e);

    public void clearTemporaryEdge();

    public void validate();

    public void clear();

    public float getZoomLevel();

    public void setZoomLevel(float zoom);

    public boolean isReadOnly();

    public void setReadOnly(boolean read);

    public interface NodeModel {

        public void addChangeListener(ChangeListener l);

        public void removeChangeListener(ChangeListener l);

        public void setDeltaX(float dx);

        public float getX();

        public void setX(float x);

        public float getXWithoutDelta();

        public void setDeltaY(float dy);

        public float getY();

        public void setY(float y);

        public float getYWithoutDelta();

        public GraphModel getParent();

        public boolean isPressed();

        public void setPressed(boolean pressed);

        public void validate();

        public String getLabel();

        public void setLabel(String label);

        public boolean isOverlapping();

        public void remove();

        public void addIncomingNode(String node);

        public void removeIncomingNode(String node);

        public ArrayList<String> getIncomingNodes();

        public void addOutgoingNode(String node);

        public void removeOutgoingNode(String node);

        public ArrayList<String> getOutgoingNodes();

    }

    public interface EdgeModel {

        public void addChangeListener(ChangeListener l);

        public void removeChangeListener(ChangeListener l);

        public boolean isDirected();

        public void setDirected(boolean directed);

        public NodeModel getFrom();

        public NodeModel getTo();

        public GraphModel getParent();

        public void validate();

        public void remove();

    }

    interface ModeEventListener extends EventListener {

        void modeChanged(EventObject e);

    }
}
