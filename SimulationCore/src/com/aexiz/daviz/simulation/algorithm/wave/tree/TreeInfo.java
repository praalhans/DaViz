package com.aexiz.daviz.simulation.algorithm.wave.tree;

import com.aexiz.daviz.simulation.algorithm.information.message.InfoMessage;

public class TreeInfo extends InfoMessage {
    public boolean equals(Object obj) {
        return obj instanceof TreeInfo;
    }
}
