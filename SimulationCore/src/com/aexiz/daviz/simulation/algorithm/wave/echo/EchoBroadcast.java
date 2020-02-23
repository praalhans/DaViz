package com.aexiz.daviz.simulation.algorithm.wave.echo;

import com.aexiz.daviz.simulation.algorithm.information.message.BroadcastMessage;

public class EchoBroadcast extends BroadcastMessage {
    public boolean equals(Object obj) {
        return obj instanceof EchoBroadcast;
    }
}
