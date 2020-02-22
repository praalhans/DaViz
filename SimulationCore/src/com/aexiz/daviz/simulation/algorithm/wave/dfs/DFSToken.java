package com.aexiz.daviz.simulation.algorithm.wave.dfs;

import com.aexiz.daviz.simulation.algorithm.information.TokenInformation;

public class DFSToken extends TokenInformation {
    public boolean equals(Object obj) {
        return obj instanceof DFSToken;
    }
}