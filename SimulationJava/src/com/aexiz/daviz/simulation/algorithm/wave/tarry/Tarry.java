package com.aexiz.daviz.simulation.algorithm.wave.tarry;

import com.aexiz.daviz.simulation.Network;
import com.aexiz.daviz.simulation.algorithm.AbstractJavaBasicAlgorithm;
import com.aexiz.daviz.simulation.viewpoint.Node;

public class Tarry extends AbstractJavaBasicAlgorithm {
    TarryState[] nodeStates;

    public Tarry() {
        assumption = TarryAssumption.makeAssumption();
    }

    @Override
    public void makeInitialNodeStates(Network network) {
        Node initiator = assumption.getInitiator();

        for
    }

}
