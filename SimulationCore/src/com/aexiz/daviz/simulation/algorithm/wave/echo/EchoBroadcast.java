package com.aexiz.daviz.simulation.algorithm.wave.echo;

import com.aexiz.daviz.simulation.algorithm.information.BroadcastInformation;

public class EchoBroadcast extends BroadcastInformation {
    public boolean equals(Object obj) {
        return obj instanceof EchoBroadcast;
    }
}
