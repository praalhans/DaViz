package com.aexiz.daviz.simulation.algorithm.wave.tarry;

import com.aexiz.daviz.simulation.algorithm.information.TokenMessage;

public class TarryToken extends TokenMessage {
    public boolean equals(Object obj) {
        return obj instanceof TarryToken;
    }
}
