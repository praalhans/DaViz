package com.aexiz.daviz.simulation.algorithm;

import com.aexiz.daviz.simulation.Event;
import com.aexiz.daviz.simulation.Network;
import com.aexiz.daviz.simulation.algorithm.information.state.StateInformation;
import com.aexiz.daviz.simulation.viewpoint.Node;

import java.util.List;

public interface JavaAlgorithm extends Algorithm {
    void makeState(Network network);

    StateInformation getProcessSpace(Node node);

    List<Event> makePossibleNextEvents();

    void updateProcessSpace(Event event);
}
