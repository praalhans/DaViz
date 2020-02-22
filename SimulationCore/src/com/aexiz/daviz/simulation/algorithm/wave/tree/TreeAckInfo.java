package com.aexiz.daviz.simulation.algorithm.wave.tree;

import com.aexiz.daviz.simulation.algorithm.information.InfoMessage;

public class TreeAckInfo extends InfoMessage {
    public boolean equals(Object obj) {
        return obj instanceof TreeAckInfo;
    }
}