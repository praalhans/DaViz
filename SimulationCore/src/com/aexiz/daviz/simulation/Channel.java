package com.aexiz.daviz.simulation;

public class Channel extends Viewpoint {
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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof com.aexiz.daviz.simulation.Channel) {
            return equals((com.aexiz.daviz.simulation.Channel) obj);
        }
        return false;
    }

    public boolean equals(com.aexiz.daviz.simulation.Channel other) {
        if (other == null) return false;
        return other.from.equals(from) && other.to.equals(to);
    }

    @Override
    public int hashCode() {
        return 31 * from.hashCode() + to.hashCode();
    }

    @Override
    public String toString() {
        return from.getLabel() + "-" + to.getLabel();
    }

}
