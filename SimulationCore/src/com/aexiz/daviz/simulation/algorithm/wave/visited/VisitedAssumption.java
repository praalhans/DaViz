package com.aexiz.daviz.simulation.algorithm.wave.visited;

import com.aexiz.daviz.simulation.algorithm.Assumption;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface VisitedAssumption {
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
