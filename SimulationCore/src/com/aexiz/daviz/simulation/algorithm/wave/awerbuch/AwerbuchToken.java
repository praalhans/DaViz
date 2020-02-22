package com.aexiz.daviz.simulation.algorithm.wave.awerbuch;

import com.aexiz.daviz.simulation.algorithm.information.TokenMessage;

public class AwerbuchToken extends TokenMessage {
    public boolean equals(Object obj) {
        return obj instanceof AwerbuchToken;
    }
}
