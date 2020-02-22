package com.aexiz.daviz.simulation.algorithm.wave.awerbuch;

import com.aexiz.daviz.simulation.algorithm.information.AckInformation;

public class AwerbuchAck extends AckInformation {
    public boolean equals(Object obj) {
        return obj instanceof AwerbuchAck;
    }
}
