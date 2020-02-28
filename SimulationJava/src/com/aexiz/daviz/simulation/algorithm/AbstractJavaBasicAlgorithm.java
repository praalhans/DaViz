package com.aexiz.daviz.simulation.algorithm;

import com.aexiz.daviz.simulation.Network;

public abstract class AbstractJavaBasicAlgorithm extends AbstractBasicAlgorithm implements JavaAlgorithm {
    protected Network network;

    @Override
    public void setNetwork(Network network) {
        this.network = network;
    }
}
