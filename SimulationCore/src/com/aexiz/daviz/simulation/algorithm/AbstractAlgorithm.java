package com.aexiz.daviz.simulation.algorithm;

import com.aexiz.daviz.simulation.Assumption;
import com.aexiz.daviz.simulation.Information;
import com.aexiz.daviz.simulation.algorithm.space.MessageSpace;
import com.aexiz.daviz.simulation.algorithm.space.ProcessDescription;
import com.aexiz.daviz.simulation.algorithm.space.ResultSpace;

public abstract class AbstractAlgorithm {
    protected Assumption assumption;

    public Assumption getAssumption() {
        return assumption;
    }

    protected abstract Information.Message makeAndUnloadMessage(MessageSpace resultSpace);

    protected abstract Information.Result makeAndUnloadResult(ResultSpace resultSpace);

//    protected abstract ProcessDescription getProcessDescription();
}
