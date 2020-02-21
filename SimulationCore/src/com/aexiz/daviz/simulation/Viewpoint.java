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
}
