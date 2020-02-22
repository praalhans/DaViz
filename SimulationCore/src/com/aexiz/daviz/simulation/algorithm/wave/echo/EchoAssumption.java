package com.aexiz.daviz.simulation.algorithm.wave.echo;

import com.aexiz.daviz.simulation.Assumption;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface EchoAssumption {
    @NotNull
    @Contract(" -> new")
    static Assumption makeAssumption() {
        return new Assumption() {
            {
                centralized_user = true;
            }
        };
    }
}