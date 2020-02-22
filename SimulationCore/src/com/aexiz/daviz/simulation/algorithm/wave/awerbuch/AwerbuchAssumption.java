package com.aexiz.daviz.simulation.algorithm.wave.awerbuch;

import com.aexiz.daviz.simulation.Assumption;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface AwerbuchAssumption {
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
