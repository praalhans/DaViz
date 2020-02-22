package com.aexiz.daviz.simulation.algorithm.event;

import com.aexiz.daviz.frege.simulation.Event;
import com.aexiz.daviz.simulation.FregeHelper;
import com.aexiz.daviz.simulation.algorithm.FregeAlgorithm;
import com.aexiz.daviz.simulation.algorithm.information.result.ResultInformation;

public class ResultEvent extends DefaultEvent implements tResultEvent {

    // Haksell dependencies
    transient Event.TEvent.DEResult<Object, Object, Object> hEvent;

    // Computed properties
    transient ResultInformation result;

    ResultEvent() {
        super();
    }

    @Override
    protected void unload() {
        super.unload();
        hEvent = super.hEvent.asEResult();
        FregeHelper helper = new FregeHelper(simulation);
        result = ((FregeAlgorithm) simulation.getAlgorithm()).makeAndUnloadResult(helper, hEvent.mem$val.call());
    }

    @Override
    protected ResultEvent clone(DefaultEvent to) {
        super.clone(to);
        ResultEvent tor = (ResultEvent) to;
        tor.hEvent = this.hEvent;
        tor.result = this.result;
        return tor;
    }

    @Override
    public boolean hasResult() {
        return true;
    }

    @Override
    public ResultInformation getResult() {
        return result;
    }

    @Override
    public ResultEvent clone() {
        return clone(new ResultEvent());
    }

    @Override
    public String toString() {
        return "Process " + happensAt.getLabel() + " results: " + result;
    }

}
