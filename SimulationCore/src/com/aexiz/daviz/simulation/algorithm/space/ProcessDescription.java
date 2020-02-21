package com.aexiz.daviz.simulation.algorithm.space;

public class ProcessDescription {
    protected AssumptionSpace assumptionSpace;
    protected MessageSpace messageSpace;
    protected ResultSpace resultSpace;
    protected StateSpace stateSpace;

    public AssumptionSpace getAssumptionSpace() {
        return assumptionSpace;
    }

    public void setAssumptionSpace(AssumptionSpace assumptionSpace) {
        this.assumptionSpace = assumptionSpace;
    }

    public MessageSpace getMessageSpace() {
        return messageSpace;
    }

    public void setMessageSpace(MessageSpace messageSpace) {
        this.messageSpace = messageSpace;
    }

    public ResultSpace getResultSpace() {
        return resultSpace;
    }

    public void setResultSpace(ResultSpace resultSpace) {
        this.resultSpace = resultSpace;
    }

    public StateSpace getStateSpace() {
        return stateSpace;
    }

    public void setStateSpace(StateSpace stateSpace) {
        this.stateSpace = stateSpace;
    }
}
