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

    protected com.aexiz.daviz.simulation.ResultEvent clone(DefaultEvent to) {
        super.clone(to);
        com.aexiz.daviz.simulation.ResultEvent tor = (com.aexiz.daviz.simulation.ResultEvent) to;
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

    public com.aexiz.daviz.simulation.ResultEvent clone() {
        return clone(new com.aexiz.daviz.simulation.ResultEvent());
    }

    public String toString() {
        return "Process " + happensAt.getLabel() + " results: " + result;
    }

}
