package com.aexiz.daviz.simulation.algorithm.wave.cidon;

import com.aexiz.daviz.simulation.algorithm.information.TokenMessage;

public class CidonToken extends TokenMessage {
    public boolean equals(Object obj) {
        return obj instanceof CidonToken;
    }
}
