package com.aexiz.daviz.simulation.algorithm.wave.tree;

import com.aexiz.daviz.simulation.Assumption;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface TreeAssumption {
    @NotNull
    @Contract(" -> new")
    static Assumption makeAssumption() {
        return new Assumption() {
            {
                acyclicGraph = true;
                decentralized_computed = true;
            }
        };
    }
}
