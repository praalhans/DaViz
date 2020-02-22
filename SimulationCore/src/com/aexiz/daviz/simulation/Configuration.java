package com.aexiz.daviz.simulation;

import com.aexiz.daviz.simulation.algorithm.information.state.StateInformation;

import java.io.PrintStream;

public interface Configuration {
    Simulation getSimulation();

    void setSimulation(Simulation simulation);

    void printSummary(PrintStream out);

    void printNodesSummary(PrintStream out);

    void printChannelsSummary(PrintStream out);

    void loadProcessState(StateVisitor visitor);

    interface StateVisitor {
        void setState(Node process, StateInformation state);
    }

    interface InitialConfiguration {
        void load();
    }
}
