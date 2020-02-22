package com.aexiz.daviz.simulation;

import com.aexiz.daviz.simulation.algorithm.information.message.MessageInformation;
import com.aexiz.daviz.simulation.algorithm.information.state.StateInformation;

import java.io.PrintStream;
import java.util.ArrayList;

public abstract class AbstractConfiguration implements Configuration {
    protected Simulation simulation;

    protected transient Node[] processes;
    protected transient boolean[] processAlive;
    protected transient StateInformation[] processState;

    protected transient Channel[] channels;
    protected transient ArrayList<MessageInformation>[] channelState;

    @Override
    public Simulation getSimulation() {
        return simulation;
    }

    @Override
    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    @Override
    public void printSummary(PrintStream out) {
        printNodesSummary(out);
        out.println();
        printChannelsSummary(out);
        out.println();
    }

    @Override
    public void printChannelsSummary(PrintStream out) {
        if (simulation == null) throw new Error("Invalid simulation");
        for (int i = 0; i < channels.length; i++) {
            if (channelState[i].size() == 0) continue;
            out.print("Channel ");
            out.print(channels[i]);
            out.print(": ");
            for (MessageInformation m : channelState[i]) {
                out.print(m);
            }
            out.println();
        }
    }

    @Override
    public void printNodesSummary(PrintStream out) {
        if (processes == null) throw new Error("Configuration not unloaded");
        for (int i = 0; i < processes.length; i++) {
            out.print("Process ");
            out.print(processes[i].getLabel());
            out.print(": ");
            if (processAlive[i]) {
                out.print(processState[i]);
            } else {
                out.print("*terminated*");
            }
            out.println();
        }
    }

    @Override
    public void loadProcessState(StateVisitor visitor) {
        for (int i = 0; i < processes.length; i++) {
            visitor.setState(processes[i], processState[i]);
        }
    }
}
