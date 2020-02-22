package com.aexiz.daviz.simulation.algorithm.wave.tarry;

import com.aexiz.daviz.simulation.algorithm.information.message.TokenMessage;

public class TarryToken extends TokenMessage {
    public boolean equals(Object obj) {
        return obj instanceof TarryToken;
    }
}
