package com.aexiz.daviz.simulation.algorithm.wave.tarry;

import com.aexiz.daviz.simulation.Event;
import com.aexiz.daviz.simulation.Network;
import com.aexiz.daviz.simulation.algorithm.AbstractJavaBasicAlgorithm;
import com.aexiz.daviz.simulation.algorithm.event.ResultEvent;
import com.aexiz.daviz.simulation.algorithm.event.SendEvent;
import com.aexiz.daviz.simulation.algorithm.information.state.PropertyVisitor;
import com.aexiz.daviz.simulation.algorithm.information.state.StateInformation;
import com.aexiz.daviz.simulation.viewpoint.Channel;
import com.aexiz.daviz.simulation.viewpoint.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tarry extends AbstractJavaBasicAlgorithm {
    Map<Node, TarryState> processSpace;

    public Tarry() {
        assumption = TarryAssumption.makeAssumption();
        processSpace = new HashMap<>();
    }

    @Override
    public void makeState() {
        validateNetwork();
        if (processSpace.isEmpty()) {
            makeInitialNodeStates(network);
            return;
        }
        throw new Error("makeState is implemented only for initial state");
    }

    @Override
    public StateInformation getProcessSpace(Node node) {
        return processSpace.get(node);
    }

    //    @Override
    public Event[] makePossibleNextEvents() {
        List<Event> events = new ArrayList<>();

        processSpace.forEach((node, processSpace) -> {
            boolean foundEvent = verifyAndMakeSendEventForNextNeighbor(processSpace, events)
                    || verifyAndMakeSendEventForReplyingParent(processSpace, events)
                    || verifyAndMakeResultEventToTerminate(processSpace, events);

//            else if (processSpace.hasToken && !processSpace.hasNeighbors()) {
//                events.add(makeResultEvent(processSpace));
//            }
        });

        return events.toArray(new Event[0]);
    }

    /**
     * For any process holding the token that has at least on neighbor {@link Channel} not visited yet, create a {@link SendEvent}
     * to send the token to the first neighbor in the {@link Channel} list and remove the channel from the list. The process state is
     * NOT modified and should be {@link TarryInitiator} for the initiator process and {@link TarryReceived} for all other processes.
     */
    private boolean verifyAndMakeSendEventForNextNeighbor(TarryState processSpace, List<Event> events) {
        if (processSpace.hasToken && processSpace.hasNeighbors()) {
            List<Channel> neighbors = processSpace.neighbors;
            Channel channel = neighbors.remove(0);
            TarryState nextProcessSpace = new TarryState(false, processSpace.state, neighbors);

            events.add(new SendEvent(new TarryToken(), nextProcessSpace, channel.to, channel.from));
            return true;
        }
        return false;
    }

    /**
     * For a non initiator process, holding the token, without any neighbor not visited yet, create a new {@link SendEvent} to
     * the channel which first sent it the token. The process state is modified to {@link TarryReplied} state with the same
     * {@link Channel} set in the previously {@link TarryReceived} state.
     */
    private boolean verifyAndMakeSendEventForReplyingParent(TarryState processSpace, List<Event> events) {
        if (processSpace.hasToken && !processSpace.hasNeighbors() && processSpace.getState() instanceof TarryReceived) {
            Channel parentChannel = (Channel) ((TarryReceived) processSpace.getState()).getViewpoint();
            PropertyVisitor nextState = processSpace.isInitiator() ? new TarryInitiator() : new TarryReceived(parentChannel);
            TarryState nextProcessSpace = new TarryState(true, nextState, new ArrayList<>());

            events.add(new SendEvent(new TarryToken(), nextProcessSpace, parentChannel.to, parentChannel.from));

            return true;
        }
        return false;
    }

    /**
     * For a initiator process holding the token with no non-visited neighbor, create a {@link ResultEvent} as a {@link TarryTerminated}
     */
    private boolean verifyAndMakeResultEventToTerminate(TarryState processSpace, List<Event> events) {
        if (processSpace.hasToken && processSpace.isInitiator() && !processSpace.hasNeighbors()) {
            events.add(new ResultEvent(new TarryTerminated()));
            return true;
        }
        return false;
    }


    private void makeInitialNodeStates(Network network) {
        Node initiator = assumption.getInitiator();
        Map<Node, List<Channel>> mapOfChannelsFromNodes = network.makeMapOfChannelsFromNodes();
        mapOfChannelsFromNodes.forEach((node, channels) -> {
            boolean isNodeInitiator = node.isEqualTo(initiator);
            PropertyVisitor state = isNodeInitiator ? new TarryInitiator() : new TarryUndefined();
            TarryState initialState = new TarryState();

            initialState.setHasToken(isNodeInitiator);
            initialState.setState(state);
            initialState.setNeighbors(channels);
            initialState.makeProperties();

            processSpace.put(node, initialState);
        });
    }

    private void validateNetwork() {
        if (network == null) throw new Error("Algorithm does not know the network");
    }

}
