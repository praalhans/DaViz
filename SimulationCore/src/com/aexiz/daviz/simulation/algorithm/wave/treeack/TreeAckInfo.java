package com.aexiz.daviz.simulation.algorithm.wave.treeack;

import com.aexiz.daviz.simulation.algorithm.information.message.InfoMessage;

public class TreeAckInfo extends InfoMessage {
    public boolean equals(Object obj) {
        return obj instanceof TreeAckInfo;
    }
}