package com.aexiz.daviz.simulation.algorithm.wave.tarry;

import com.aexiz.daviz.simulation.algorithm.information.TokenInformation;

public class TarryToken extends TokenInformation {
    public boolean equals(Object obj) {
        return obj instanceof TarryToken;
    }
}
