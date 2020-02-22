package com.aexiz.daviz.simulation.algorithm.wave.dfs;

import com.aexiz.daviz.simulation.algorithm.information.TokenMessage;

public class DFSToken extends TokenMessage {
    public boolean equals(Object obj) {
        return obj instanceof DFSToken;
    }
}