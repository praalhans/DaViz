package com.aexiz.daviz.simulation.algorithm.event;

import com.aexiz.daviz.simulation.algorithm.information.result.ResultInformation;
import com.aexiz.daviz.simulation.viewpoint.Node;

public class ResultEvent extends DefaultEvent implements tResultEvent {
    transient ResultInformation result;

    public ResultEvent(ResultInformation result, Node happensAt) {
        super();
        this.result = result;
        this.happensAt = happensAt;
    }

    public ResultEvent() {
    }

    @Override
    protected ResultEvent clone(DefaultEvent to) {
        super.clone(to);
        ResultEvent tor = (ResultEvent) to;
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
