package com.aexiz.daviz.simulation.algorithm.wave.treeack;

import com.aexiz.daviz.simulation.algorithm.information.message.AckMessage;

public class TreeAckAck extends AckMessage {
    public boolean equals(Object obj) {
        return obj instanceof TreeAckAck;
    }
}
