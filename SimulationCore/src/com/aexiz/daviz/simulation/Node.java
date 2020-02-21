package com.aexiz.daviz.simulation;

/**
 * This class should actually be "process", but because Java already has a built-in class called Process,
 * we can not name it so.
 * Next time: prefix model classes with a letter, like they do in Swing.
 */
public class Node extends Viewpoint {
    public static final String CLIENT_PROPERTY_POSITION_X = "node_pos_x";
    public static final String CLIENT_PROPERTY_POSITION_Y = "node_pos_y";
    public static final String CLIENT_PROPERTY_NODEMODEL = "nodemodel";

    /**
     * Haskell dependencies
     */
    transient int hId;

    transient boolean marked;
    private String label;

    public Node() {
    }

    public Node(String label) {
        setLabel(label);
    }

    public String getLabel() {
        return this.label == null ? "" : this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return this.label != null ? getLabel() : super.toString();
    }
}
