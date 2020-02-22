package com.aexiz.daviz.simulation;

import com.aexiz.daviz.simulation.algorithm.Algorithm;
import com.aexiz.daviz.simulation.algorithm.Assumption;
import com.aexiz.daviz.simulation.algorithm.Simulation;
import com.aexiz.daviz.simulation.viewpoint.Node;

import java.util.UUID;

public abstract class AbstractSimulation implements Simulation {
    protected Algorithm algorithm;
    protected Assumption assumption;
    protected Network network;
    protected String uuid;

    protected transient Execution execution;

    public AbstractSimulation() {
        uuid = UUID.randomUUID().toString();
    }

    public AbstractSimulation(Algorithm algorithm) {
        uuid = UUID.randomUUID().toString();
        this.algorithm = algorithm;
    }

    @Override
    public Algorithm getAlgorithm() {
        if (algorithm == null) throw new Error("Algorithm is not set");
        return algorithm;
    }

    @Override
    public void setAlgorithm(Algorithm algorithm) {
        if (!algorithm.hasAssumption()) throw new Error("Invalid algorithm assumptions");
        this.algorithm = algorithm;
        assumption = algorithm.getAssumption();
    }

    @Override
    public Assumption getAssumption() {
        if (assumption == null) throw new Error("Assumption is not set");
        return assumption;
    }

    @Override
    public Network getNetwork() {
        if (network == null) throw new Error("Network is not set");
        return network;
    }

    @Override
    public void setNetwork(Network network) {
        if (network.belongsToSimulation() && !network.belongsToSimulation(uuid)) {
            throw new Error("Network owned by other simulation");
        }
        this.network = network;
        this.network.setSimulationUUID(uuid);
    }

    @Override
    public void setInitiator(Node process) {
        if (assumption == null) throw new Error("No algorithm");
        if (!assumption.isCentralized_user()) throw new Error("Algorithm is not centralized by user-input");
        if (network == null) throw new Error("Network is not set");
        if (!network.hasNode(process)) throw new Error("Process not owned by simulation");
        assumption.setInitiator(process);
    }

    @Override
    public Execution getExecution() {
        return execution;
    }
}
