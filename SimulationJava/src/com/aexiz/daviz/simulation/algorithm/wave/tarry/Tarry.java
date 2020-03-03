package com.aexiz.daviz.simulation.algorithm.wave.tarry;

import com.aexiz.daviz.simulation.Event;
import com.aexiz.daviz.simulation.Network;
import com.aexiz.daviz.simulation.algorithm.AbstractJavaBasicAlgorithm;
import com.aexiz.daviz.simulation.algorithm.event.ReceiveEvent;
import com.aexiz.daviz.simulation.algorithm.event.ResultEvent;
import com.aexiz.daviz.simulation.algorithm.event.SendEvent;
import com.aexiz.daviz.simulation.algorithm.event.tSendEvent;
import com.aexiz.daviz.simulation.algorithm.information.Information;
import com.aexiz.daviz.simulation.algorithm.information.result.ResultInformation;
import com.aexiz.daviz.simulation.algorithm.information.state.PropertyVisitor;
import com.aexiz.daviz.simulation.viewpoint.Channel;
import com.aexiz.daviz.simulation.viewpoint.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Tarry extends AbstractJavaBasicAlgorithm {
    // Consider moving the flag and channel information to AbstractJavaBasicAlgorithm
    transient boolean isTokenInChannel;

    transient Channel channelWithToken;

    public Tarry() {
        super();
        assumption = TarryAssumption.makeAssumption();
    }

    @Override
    public List<Event> makePossibleNextEvents() {
        List<Event> events = new ArrayList<>();
        int finishedProcessCount = 0;

        for (Map.Entry<Node, Information> entry : processesSpace.entrySet()) {
            Node node = entry.getKey();

            if (isTokenInChannel && !node.isEqualTo(channelWithToken.to)) continue;
            if (entry.getValue() instanceof ResultInformation) {
                finishedProcessCount++;
                continue;
            }

            TarryState processSpace = (TarryState) entry.getValue();

            // foundEvent is not used, but it was needed to stop verifying if an event is found
            // Initially, the ifs condition within each method was done here, but it was hurting legibility
            // The scheme below is used as a sort of if/else
            @SuppressWarnings("unused")
            boolean foundEvent = verifyAndMakeSendEventForNextNeighbor(events, processSpace)
                    || verifyAndMakeSendEventForReplyingParent(events, processSpace)
                    || verifyAndMakeResultEventToTerminate(events, processSpace, node)
                    || verifyAndMakeResultEventToDecide(events, processSpace, node)
                    || verifyAndMakeReceiveEventForNonInitiatorInUndefinedState(events, processSpace)
                    || verifyAndMakeReceiveEventForActiveProcess(events, processSpace);
        }
        if (events.isEmpty() && finishedProcessCount != processesSpace.size())
            throw new Error("Unknown step of Tarry algorithm");
        return events;
    }

    @Override
    public void updateProcessSpace(Event event) {
        setTokenInformation(event);
        super.updateProcessSpace(event);
    }

    private void setTokenInformation(Event event) {
        if (event instanceof tSendEvent) {
            channelWithToken = new Channel(event.getHappensAt(), event.getReceiver());
            isTokenInChannel = true;
        } else {
            channelWithToken = null;
            isTokenInChannel = false;
        }
    }

    /**
     * For any process holding the token that has at least on neighbor {@link Channel} not visited yet, create a {@link SendEvent}
     * to send the token to the first neighbor in the {@link Channel} list and remove the channel from the list. The process state is
     * NOT modified and should be {@link TarryInitiator} for the initiator process and {@link TarryReceived} for all other processes.
     */
    private boolean verifyAndMakeSendEventForNextNeighbor(List<Event> events, TarryState processSpace) {
        if (processSpace.hasToken && processSpace.hasNeighbors()) {
            List<Channel> neighbors = processSpace.getNeighbors();
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
    private boolean verifyAndMakeSendEventForReplyingParent(List<Event> events, TarryState processSpace) {
        if (processSpace.hasToken && !processSpace.hasNeighbors() && processSpace.getState() instanceof TarryReceived) {
            Channel parentChannel = (Channel) ((TarryReceived) processSpace.getState()).getViewpoint();
            PropertyVisitor nextState = processSpace.isInitiator() ? new TarryInitiator() : new TarryReplied(parentChannel);
            TarryState nextProcessSpace = new TarryState(false, nextState, new ArrayList<>());

            events.add(new SendEvent(new TarryToken(), nextProcessSpace, parentChannel.to, parentChannel.from));
            return true;
        }
        return false;
    }

    /**
     * For a initiator process holding the token with no non-visited neighbor, create a {@link ResultEvent} as {@link TarryTerminated}
     */
    private boolean verifyAndMakeResultEventToTerminate(List<Event> events, TarryState processSpace, Node happensAt) {
        if (processSpace.hasToken && processSpace.isInitiator() && !processSpace.hasNeighbors()) {
            events.add(new ResultEvent(new TarryTerminated(), happensAt));
            return true;
        }
        return false;
    }

    /**
     * For a non-initiator process not holding the token and with no non-visited neighbor and in the state {@link TarryUndefined} or
     * {@link TarryReplied}, create a {@link ResultEvent} as {@link TarryDecided}
     */
    private boolean verifyAndMakeResultEventToDecide(List<Event> events, TarryState processSpace, Node happensAt) {
        if (!processSpace.hasToken && !processSpace.hasNeighbors() &&
                (processSpace.getState() instanceof TarryUndefined || processSpace.getState() instanceof TarryReplied)
        ) {
            events.add(new ResultEvent(new TarryDecided(), happensAt));
            return true;
        }
        return false;
    }

    /**
     * For a non-initiator process not holding the token and with at least one non-visited neighbor and in the state {@link TarryUndefined},
     * create a {@link ReceiveEvent} to the first non-visited neighbor {@link Channel} and remove it from the list.
     */
    private boolean verifyAndMakeReceiveEventForNonInitiatorInUndefinedState(List<Event> events, TarryState processSpace) {
        if (isTokenInChannel && !processSpace.hasToken && processSpace.getState() instanceof TarryUndefined && processSpace.hasNeighbors()) {
            List<Channel> neighbors = processSpace.getNeighbors();
            Channel channel = new Channel(channelWithToken.to, channelWithToken.from);
            neighbors.remove(channel);
            PropertyVisitor nextState = new TarryReceived(channel);

            TarryState nextProcessSpace = new TarryState(true, nextState, neighbors);

            events.add(new ReceiveEvent(new TarryToken(), nextProcessSpace, channelWithToken.from, channelWithToken.to));
            return true;
        }
        return false;
    }

    /**
     * For a process not holding the token and the token is in a channel,
     * keep the same state and update the process space to hold the token
     */
    private boolean verifyAndMakeReceiveEventForActiveProcess(List<Event> events, TarryState processSpace) {
        if (isTokenInChannel && !processSpace.hasToken) {
            Node to = lastEvent.getReceiver();
            Node from = lastEvent.getHappensAt();

            TarryState nextProcessSpace = new TarryState(true, processSpace.getState(), processSpace.getNeighbors());

            events.add(new ReceiveEvent(new TarryToken(), nextProcessSpace, from, to));
            return true;
        }
        return false;
    }

    @Override
    protected void makeInitialNodeStates(Network network) {
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

            processesSpace.put(node, initialState);
            isTokenInChannel = false;
        });
    }

}
