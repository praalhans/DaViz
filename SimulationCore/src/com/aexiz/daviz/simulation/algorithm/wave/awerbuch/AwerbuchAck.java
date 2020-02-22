package com.aexiz.daviz.simulation.algorithm.wave.awerbuch;

import com.aexiz.daviz.simulation.algorithm.information.message.AckMessage;

public class AwerbuchAck extends AckMessage {
    public boolean equals(Object obj) {
        return obj instanceof AwerbuchAck;
    }
}
