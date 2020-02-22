package com.aexiz.daviz.simulation.algorithm.wave.cidon;

import com.aexiz.daviz.simulation.Assumption;
import com.aexiz.daviz.simulation.algorithm.BasicAlgorithm;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface CidonAssumption extends BasicAlgorithm {
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
