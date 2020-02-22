package com.aexiz.daviz.simulation.viewpoint;

public abstract class Viewpoint extends Locus {
    protected String networkUUID;

    public String getNetworkUUID() {
        return networkUUID;
    }

    public void setNetworkUUID(String networkID) {
        this.networkUUID = networkID;
    }

    public boolean belongsToAnyNetwork() {
        return this.networkUUID != null;
    }

    public boolean belongsToNetwork(String networkID) {
        return belongsToAnyNetwork() && this.networkUUID.equals(networkID);
    }
}
