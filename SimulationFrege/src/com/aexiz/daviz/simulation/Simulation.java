package com.aexiz.daviz.simulation;

public class Simulation {

    // Properties
    private Algorithm algorithm;
    private Assumption assumption;
    private Network network;

    // Transient fields
    private transient Execution execution;

    public Simulation() {
    }

    public Simulation(Algorithm alg) {
        setAlgorithm(alg);
    }

    public Algorithm getAlgorithm() {
        if (algorithm == null) throw new Error("Algorithm is not set");
        return algorithm;
    }

    public void setAlgorithm(Algorithm alg) {
        algorithm = alg;
        assumption = alg.getAssumption();
        if (assumption == null) throw new Error("Invalid algorithm assumptions");
    }

    public Assumption getAssumption() {
        if (assumption == null) throw new Error("Assumption is not set");
        return assumption;
    }

    public Network getNetwork() {
        if (network == null) throw new Error("Network is not set");
        return network;
    }

    public void setNetwork(Network net) {
        if (net.simulation != null) throw new Error("Network owned by other simulation");
        network = net;
        network.simulation = this;
    }

    public void setInitiator(Viewpoint.Node process) {
        if (assumption == null) throw new Error("No algorithm");
        if (!assumption.centralized_user) throw new Error("Algorithm is not centralized by user-input");
        if (network == null) throw new Error("Network is not set");
        if (!network.hasNode(process)) throw new Error("Process not owned by simulation");
        assumption.initiator = process;
    }

    public void load() {
        network.load();
        Configuration.InitialConfiguration ic = new Configuration.InitialConfiguration();
        ic.simulation = this;
        ic.load();
        execution = new Execution();
        execution.simulation = this;
        execution.configuration = ic;
        execution.loadFirst();
    }

    public Execution getExecution() {
        return execution;
    }

}
