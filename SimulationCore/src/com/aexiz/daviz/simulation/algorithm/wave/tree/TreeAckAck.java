package com.aexiz.daviz.simulation.algorithm.wave.tree;

import com.aexiz.daviz.simulation.algorithm.information.AckInformation;

public class TreeAckAck extends AckInformation {
    public boolean equals(Object obj) {
        return obj instanceof TreeAckAck;
    }
}
