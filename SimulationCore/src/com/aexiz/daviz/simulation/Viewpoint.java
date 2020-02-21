package com.aexiz.daviz.simulation;

public abstract class Viewpoint extends Locus {
    protected String networkID;

    public String getNetworkID() {
        return networkID;
    }

    public void setNetworkID(String networkID) {
        this.networkID = networkID;
    }

    public boolean belongsToAnyNetwork() {
        return this.networkID != null;
    }

    public boolean belongsToNetwork(String networkID) {
        return belongsToAnyNetwork() && this.networkID.equals(networkID);
    }

    public static class Channel extends Viewpoint {
        public static final String CLIENT_PROPERTY_EDGEMODEL = "edgemodel";
        public static final String CLIENT_PROPERTY_FIRST_DIRECTED = "first_directed";

        public final Node from;
        public final Node to;

        private float weight = Float.NaN;

        public Channel(Node from, Node to) {
            if (from == null || to == null)
                throw new IllegalArgumentException("Channel cannot be created with null @Node");
            this.from = from;
            this.to = to;
        }

        public void clearWeight() {
            this.weight = Float.NaN;
        }

        public boolean hasWeight() {
            return weight == weight;
        }

        public float getWeight() {
            return weight;
        }

        public void setWeight(float weight) {
            this.weight = weight;
        }

        public boolean equals(Object obj) {
            if (obj instanceof Channel) {
                return equals((Channel) obj);
            }
            return false;
        }

        public boolean equals(Channel other) {
            if (other == null) return false;
            return other.from.equals(from) && other.to.equals(to);
        }

        public int hashCode() {
            return 31 * from.hashCode() + to.hashCode();
        }

        @Override
        public String toString() {
            return from.getLabel() + "-" + to.getLabel();
        }

    }

    /**
     * This class should actually be "process", but because Java already has a built-in class called Process,
     * we can not name it so.
     * Next time: prefix model classes with a letter, like they do in Swing.
     */
    public static class Node extends Viewpoint {
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
}
