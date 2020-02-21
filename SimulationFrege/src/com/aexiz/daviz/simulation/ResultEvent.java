package com.aexiz.daviz.simulation;

import com.aexiz.daviz.frege.simulation.Event;

public class ResultEvent extends DefaultEvent {

    // Haksell dependencies
    transient Event.TEvent.DEResult<Object, Object, Object> hEvent;

    // Computed properties
    transient Information.Result result;

    ResultEvent() {
    }

    void unload() {
        super.unload();
        hEvent = super.hEvent.asEResult();
        SimulationHelper helper = new SimulationHelper(simulation);
        result = ((DefaultAlgorithm)simulation.getAlgorithm()).makeAndUnloadResult(helper, hEvent.mem$val.call());
    }

    protected ResultEvent clone(DefaultEvent to) {
        super.clone(to);
        ResultEvent tor = (ResultEvent) to;
        tor.hEvent = this.hEvent;
        tor.result = this.result;
        return tor;
    }

    public boolean hasResult() {
        return true;
    }

    public Information.Result getResult() {
        return result;
    }

    public ResultEvent clone() {
        return clone(new ResultEvent());
    }

    public String toString() {
        return "Process " + happensAt.getLabel() + " results: " + result;
    }

}
