package com.aexiz.daviz;

import com.aexiz.daviz.simulation.*;
import com.aexiz.daviz.simulation.Configuration.StateVisitor;
import com.aexiz.daviz.simulation.Information.*;
import com.aexiz.daviz.ui.ExecutionModel;
import com.aexiz.daviz.ui.ExecutionModel.EventModel;
import com.aexiz.daviz.ui.ExecutionModel.EventType;
import com.aexiz.daviz.ui.ExecutionModel.MessageModel;
import com.aexiz.daviz.ui.ExecutionModel.PendingMessageModel;
import com.aexiz.daviz.ui.FutureEvent;
import com.aexiz.daviz.ui.GraphModel;
import com.aexiz.daviz.ui.GraphModel.EdgeModel;
import com.aexiz.daviz.ui.GraphModel.NodeModel;
import com.aexiz.daviz.ui.InfoModel;
import com.aexiz.daviz.ui.InfoModel.PropertyModel;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;

class SimulationManager {

    private static final int MAX_ROUNDS = 100;

    Controller controller;

    boolean fresh = true;
    boolean loadedNetwork = false;
    boolean linear = true;

    /**
     * From simulation
     */
    transient Execution executionRoot;

    /**
     * From simulation (via choice or predetermined)
     */
    transient ArrayList<Execution> executionPath;

    /**
     * from simulation
     */
    transient Node[] nodes;

    /**
     * from simulation (via execution root)
     */
    transient State[] nodeInitialStates;

    /**
     * from simulation (via selection)
     */
    transient State[] nodeLastStates;

    /**
     * from simulation (via selection)
     */
    transient Result[] nodeLastTermStatus;

    /**
     * from timeline GUI
     */
    transient int[] nodeProcessIds;

    /**
     * from network GUI
     */
    transient NodeModel[] nodeModels;

    /**
     * from simulation
     */
    transient Channel[] channels;

    /**
     * from network GUI
     */
    transient EdgeModel[] channelEdgeModels;

    /**
     * from simulation (via selection)
     */
    transient ArrayList<Message>[] channelStates;

    /**
     * from simulation
     */
    transient Execution[] choiceExecutions;

    /**
     * from GUI
     */
    transient FutureEvent[] choiceEvents;

    /**
     * from simulation
     */
    transient ArrayList<Event> events = new ArrayList<>();

    /**
     * from timeline GUI
     */
    transient ArrayList<EventModel> eventModels = new ArrayList<>();

    /**
     * from timeline GUI, MessageModel and PendingMessageModel
     */
    transient ArrayList<Object> messageModels = new ArrayList<>();

    /**
     * from simulation, corresponding send event
     */
    transient ArrayList<Event> messageSendEvents = new ArrayList<>();

    private Thread worker;
    private LinkedBlockingQueue<Callable<Void>> queue = new LinkedBlockingQueue<>();

    SimulationManager(Controller controller) {
        this.controller = controller;
        initWorkerThread();
    }

    // Executes within worker thread
    private void clear() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            controller.clearSimulation();
        });

        // Clear transient fields
        executionRoot = null;
        executionPath = new ArrayList<>();
        nodes = null;
        nodeProcessIds = null;
        nodeModels = null;
        nodeInitialStates = null;
        nodeLastStates = null;
        nodeLastTermStatus = null;
        channels = null;
        channelEdgeModels = null;
        channelStates = null;
        choiceExecutions = null;
        choiceEvents = null;
        events = new ArrayList<>();
        eventModels = new ArrayList<>();
        messageModels = new ArrayList<>();
        messageSendEvents = new ArrayList<>();

        loadedNetwork = false;
        fresh = true;
    }

    // Executes within worker thread
    private void clearEvents() throws Exception {
        // Clear transient fields
        events = new ArrayList<>();
        eventModels = new ArrayList<>();
        messageModels = new ArrayList<>();
        messageSendEvents = new ArrayList<>();

        // Called after loadNetwork
        for (int i = 0; i < channels.length; i++) {
            channelStates[i].clear();
        }
        for (int i = 0; i < nodes.length; i++) {
            nodeLastStates[i] = nodeInitialStates[i];
            nodeLastTermStatus[i] = null;
        }

        SwingUtilities.invokeAndWait(() -> {
            controller.selectionModel.clearSelection();
            controller.timelineModel.clearEventsAndMessages();
        });
    }

    // Executes within worker thread
    private void clearEventsAfter(int time) throws Exception {
        // Called after loadNetwork
        for (int i = 0; i < channels.length; i++) {
            channelStates[i].clear();
        }
        for (int i = 0; i < nodes.length; i++) {
            nodeLastStates[i] = nodeInitialStates[i];
            nodeLastTermStatus[i] = null;
        }
        SwingUtilities.invokeAndWait(() -> {
            // Selectively delete events
            for (int i = 0, size = events.size(); i < size; i++) {
                EventModel em = eventModels.get(i);
                int at = (int) em.getTimeWithoutDelta();
                if (at >= time) {
                    // Deletion if past
                    controller.timelineModel.removeEvent(em);
                    events.remove(i);
                    eventModels.remove(i);
                    i--;
                    size--;
                }
            }
            // Selectively delete messages
            for (int i = 0, size = messageModels.size(); i < size; i++) {
                Object tr = messageModels.get(i);
                int from, to;
                if (tr instanceof MessageModel) {
                    MessageModel msg = (MessageModel) tr;
                    from = (int) msg.getFrom().getTimeWithoutDelta();
                    to = (int) msg.getTo().getTimeWithoutDelta();
                    if (from >= time && to >= time) {
                        // Deletion if past
                        controller.timelineModel.removeMessage(msg);
                        messageModels.remove(i);
                        messageSendEvents.remove(i);
                        i--;
                        size--;
                    } else if (from < time && to >= time) {
                        // Delete message, replace by pending message (keep send event)
                        PendingMessageModel pmsg = controller.timelineModel.createPendingMessage(msg.getFrom(), msg.getTo().getProcessIndex());
                        controller.timelineModel.removeMessage(msg);
                        controller.timelineModel.addPendingMessage(pmsg);
                        messageModels.set(i, pmsg);
                    }
                } else if (tr instanceof PendingMessageModel) {
                    PendingMessageModel msg = (PendingMessageModel) tr;
                    from = (int) msg.getFrom().getTimeWithoutDelta();
                    if (from >= time) {
                        // Deletion if past
                        controller.timelineModel.removePendingMessage(msg);
                        messageModels.remove(i);
                        messageSendEvents.remove(i);
                        i--;
                        size--;
                    }
                } else throw new Error();
            }
            // Synchronize UI
            controller.selectionModel.clearSelection();
        });
    }

    // Executes within AWT dispatch thread
    void changeFutureEvent(FutureEvent fe) {
        if (!loadedNetwork) return;
        if (!linear) return;

        Execution succ = null;
        for (int i = 0; i < choiceEvents.length; i++) {
            if (choiceEvents[i] == fe) {
                succ = choiceExecutions[i];
            }
        }
        if (succ == null) throw new Error();
        // Check if successor is different
        for (Execution ex : executionPath) {
            if (ex == succ) return;
        }
        // Otherwise reload execution
        Execution fsucc = succ;
        float maxOldTime = controller.timelineModel.getMaxLastTime();
        controller.timelineModel.setTemporaryMaxTime(maxOldTime);
        float oldTime = controller.timelineModel.getCurrentTimeWithoutDelta();
        controller.choiceModel.clear();
        performJob(() -> {
            loadExecution(executionRoot, fsucc.getExecutionPath());
            SwingUtilities.invokeAndWait(() -> {
                // Update time to update choice window
                controller.timelineModel.setCurrentTime(oldTime);
                controller.timelineModel.clearTemporaryMaxTime();
            });
            return null;
        });
    }

    // Executes within AWT dispatcher thread
    void changeTime() {
        performJob(() -> {
            if (!linear) return null;
            if (!loadedNetwork) return null;
            SwingUtilities.invokeAndWait(() -> {
                // TODO This method is way too coupled to the timeline model:
                // the timeline model is changed with regard to relative ordering by the
                // user, but this method assumes that the events are at exactly the same
                // time as when they were inserted.

                EventModel[] last = controller.timelineModel.getHappenedLastEvent();
                for (int i = 0; i < nodes.length; i++) {
                    nodeLastStates[i] = nodeInitialStates[i];
                    nodeLastTermStatus[i] = null;
                }
                // Find events based on time
                for (EventModel event : last) {
                    Event foundE = null;
                    for (int i = 0, size = events.size(); i < size; i++) {
                        if (eventModels.get(i) == event) {
                            foundE = events.get(i);
                            break;
                        }
                    }
                    if (foundE == null) throw new Error("Unable to find event");
                    int p = event.getProcessIndex();
                    Node foundN = null;
                    for (int i = 0; i < nodes.length; i++) {
                        if (nodeProcessIds[i] == p) {
                            if (foundE.hasNextState()) {
                                nodeLastStates[i] = foundE.getNextState();
                            } else {
                                Event previous = foundE.getPreviousEvent();
                                if (previous != null && previous.hasNextState()) {
                                    nodeLastStates[i] = previous.getNextState();
                                }
                                if (foundE instanceof Event.ResultEvent) {
                                    nodeLastTermStatus[i] = ((Event.ResultEvent) foundE).getResult();
                                } else throw new Error();
                            }
                            foundN = nodes[i];
                            break;
                        }
                    }
                    if (foundN == null) throw new Error("Unable to find node");
                }
                // Find messages (pending or delivered)
                Object[] transit = controller.timelineModel.getHappenedTransitMessage();
                for (int i = 0; i < channels.length; i++)
                    channelStates[i].clear();
                for (Object tr : transit) {
                    int from, to;
                    if (tr instanceof MessageModel) {
                        MessageModel msg = (MessageModel) tr;
                        from = msg.getFrom().getProcessIndex();
                        to = msg.getTo().getProcessIndex();
                    } else if (tr instanceof PendingMessageModel) {
                        PendingMessageModel msg = (PendingMessageModel) tr;
                        from = msg.getFrom().getProcessIndex();
                        to = msg.getTo();
                    } else throw new Error();

                    Event send = null;
                    for (int i = 0, size = messageModels.size(); i < size; i++) {
                        if (messageModels.get(i) == tr) {
                            send = messageSendEvents.get(i);
                            break;
                        }
                    }
                    if (send == null) throw new Error();

                    Node fromN = null, toN = null;
                    for (int i = 0; (fromN == null || toN == null) && i < nodes.length; i++) {
                        if (nodeProcessIds[i] == from) {
                            fromN = nodes[i];
                        }
                        if (nodeProcessIds[i] == to) {
                            toN = nodes[i];
                        }
                    }
                    if (fromN == null) throw new Error();
                    if (toN == null) throw new Error();

                    Message message = send.getMessage();
                    for (int i = 0; i < channels.length; i++) {
                        if (channels[i].from == fromN && channels[i].to == toN) {
                            channelStates[i].add(message);
                        }
                    }
                }
                // Fire selection listener, updates info
                controller.selectionModel.refreshSelection();
                // Update execution choices
                int time = (int) controller.timelineModel.getCurrentTime();
                int size = executionPath.size();
                controller.choiceModel.clear();

                if (time < size) {
                    Execution ex = executionPath.get(time);
                    Execution[] succs = ex.getSuccessors();
                    choiceExecutions = succs;
                    choiceEvents = new FutureEvent[succs.length];
                    for (int i = 0; i < succs.length; i++) {
                        Event e = succs[i].getLastEvent();
                        EventType type = getEventType(e);
                        String other = null;
                        if (e.hasReceiver()) other = e.getReceiver().getLabel();
                        if (e.hasSender()) other = e.getSender().getLabel();
                        choiceEvents[i] = new FutureEvent(e.getHappensAt().getLabel(), type, other);
                        controller.choiceModel.addElement(choiceEvents[i]);
                        // Default selection corresponds to path taken
                    }
                    boolean found = false;
                    if (time + 1 < size) {
                        Execution sel = executionPath.get(time + 1);
                        for (int i = 0; i < succs.length; i++) {
                            if (succs[i] == sel) {
                                controller.listSelectionModel.setSelectionInterval(i, i);
                                found = true;
                                break;
                            }
                        }
                    }
                    if (!found) controller.listSelectionModel.clearSelection();
                }
            });
            return null;
        });
    }

    // Executes within AWT dispatch thread
    private void resetTimeEvents() {
        for (int i = 0; i < eventModels.size(); i++) {
            float time = i;
            EventModel model = eventModels.get(i);
            model.setTime(time);
        }
    }

    // Executes within AWT dispatch thread
    void setLinear(boolean linear) {
        this.linear = linear;
        if (linear) {
            // Ensure that all events are linearly ordered according to simulation
            resetTimeEvents();
        } else {

        }
    }

    // Executes within AWT dispatch thread
    void changeNodeSelection(NodeModel node) {
        if (!loadedNetwork) return;

        State last = null;
        Result status = null;
        for (int i = 0; i < nodes.length; i++) {
            if (nodeModels[i] == node) {
                last = nodeLastStates[i];
                status = nodeLastTermStatus[i];
                break;
            }
        }
        if (last == null) throw new Error();
        PropertyModel p;
        p = controller.infoModel.createProperty("Process:", node.getLabel(), InfoModel.SIMPLE_TYPE);
        controller.infoModel.addProperty(p);
        if (status != null) {
            p = controller.infoModel.createProperty("Status:", null, InfoModel.COMPOUND_TYPE);
            loadResult(p, status);
            controller.infoModel.addProperty(p);
        }
        p = controller.infoModel.createProperty("Last state", null, InfoModel.COMPOUND_TYPE);
        loadState(p, last);
        controller.infoModel.addProperty(p);
    }

    // Executes within AWT dispatch thread
    void changeEdgeSelection(EdgeModel edge) {
        if (!loadedNetwork) return;

        ArrayList<Message> transit = new ArrayList<Message>();
        ArrayList<Channel> channel = new ArrayList<Channel>();
        for (int i = 0; i < channels.length; i++) {
            if (channelEdgeModels[i] == edge) {
                for (Message m : channelStates[i]) {
                    transit.add(m);
                    channel.add(channels[i]);
                }
            }
        }

        PropertyModel p;
        p = controller.infoModel.createProperty("Type:", edge.isDirected() ? "Directed" : "Undirected", InfoModel.SIMPLE_TYPE);
        controller.infoModel.addProperty(p);

        p = controller.infoModel.createProperty("Messages", null, InfoModel.COMPOUND_TYPE);
        PropertyModel p2 = controller.infoModel.createNestedProperty(p, "", transit.size() + " elements", InfoModel.SIMPLE_TYPE);
        controller.infoModel.addNestedProperty(p, p2);
        for (int i = 0, size = transit.size(); i < size; i++) {
            Channel c = channel.get(i);
            p2 = controller.infoModel.createNestedProperty(p, i + " dir:", c.from.getLabel() + " -> " + c.to.getLabel(), InfoModel.SIMPLE_TYPE);
            controller.infoModel.addNestedProperty(p, p2);
            p2 = controller.infoModel.createNestedProperty(p, i + " msg:", null, InfoModel.COMPOUND_TYPE);
            loadMessage(p2, transit.get(i));
            controller.infoModel.addNestedProperty(p, p2);
        }
        controller.infoModel.addProperty(p);
    }

    private Event findEventByModel(EventModel model) {
        for (int i = 0, size = events.size(); i < size; i++) {
            if (eventModels.get(i) == model)
                return events.get(i);
        }
        throw new Error();
    }

    // Executes within AWT dispatch thread
    void changeEventSelection(EventModel ev) {
        if (!loadedNetwork) throw new Error();

        Event event = findEventByModel(ev);
        PropertyModel p;
        p = controller.infoModel.createProperty("Process:", event.getHappensAt().getLabel(), InfoModel.SIMPLE_TYPE);
        controller.infoModel.addProperty(p);
        p = controller.infoModel.createProperty("Type:", ev.getEventType().toString(), InfoModel.SIMPLE_TYPE);
        controller.infoModel.addProperty(p);
        if (event.hasSender()) {
            p = controller.infoModel.createProperty("From:", event.getSender().getLabel(), InfoModel.SIMPLE_TYPE);
            controller.infoModel.addProperty(p);
        }
        if (event.hasReceiver()) {
            if (event.hasMatchingEvent()) {
                p = controller.infoModel.createProperty("Delivered to:", event.getReceiver().getLabel(), InfoModel.SIMPLE_TYPE);
                controller.infoModel.addProperty(p);
            } else {
                p = controller.infoModel.createProperty("Underway to:", event.getReceiver().getLabel(), InfoModel.SIMPLE_TYPE);
                controller.infoModel.addProperty(p);
            }
        }
        if (event.hasNextState()) {
            p = controller.infoModel.createProperty("Next state", null, InfoModel.COMPOUND_TYPE);
            loadState(p, event.getNextState());
            controller.infoModel.addProperty(p);
        }
        if (event.hasMessage()) {
            p = controller.infoModel.createProperty("Message", null, InfoModel.COMPOUND_TYPE);
            loadMessage(p, event.getMessage());
            controller.infoModel.addProperty(p);
        }
        if (event.hasResult()) {
            p = controller.infoModel.createProperty("Result", null, InfoModel.COMPOUND_TYPE);
            loadResult(p, event.getResult());
            controller.infoModel.addProperty(p);
        }
    }

    // Executes within AWT dispatch thread
    void changeMessageSelection(Object sel) {
        if (!loadedNetwork) throw new Error();

        Event send = null;
        for (int i = 0, size = messageModels.size(); i < size; i++) {
            if (messageModels.get(i) == sel) {
                send = messageSendEvents.get(i);
                break;
            }
        }
        if (send == null) throw new Error("Unable to find required send event");
        PropertyModel p;
        p = controller.infoModel.createProperty("From:", send.getHappensAt().getLabel(), InfoModel.SIMPLE_TYPE);
        controller.infoModel.addProperty(p);
        if (send.hasReceiver()) {
            if (send.hasMatchingEvent()) {
                p = controller.infoModel.createProperty("Delivered to:", send.getReceiver().getLabel(), InfoModel.SIMPLE_TYPE);
                controller.infoModel.addProperty(p);
            } else {
                p = controller.infoModel.createProperty("Underway to:", send.getReceiver().getLabel(), InfoModel.SIMPLE_TYPE);
                controller.infoModel.addProperty(p);
            }
        } else throw new Error();
        if (!send.hasMessage()) throw new Error();
        p = controller.infoModel.createProperty("Message", null, InfoModel.COMPOUND_TYPE);
        loadMessage(p, send.getMessage());
        controller.infoModel.addProperty(p);
    }

    private void loadState(PropertyModel p, State state) {
        InfoPropertyBuilder ipb = new InfoPropertyBuilder();
        ipb.property = p;
        try {
            state.buildProperties(ipb);
        } finally {
            ipb.dispose();
        }
    }

    private void loadMessage(PropertyModel p, Message message) {
        InfoPropertyBuilder ipb = new InfoPropertyBuilder();
        ipb.property = p;
        try {
            message.buildProperties(ipb);
        } finally {
            ipb.dispose();
        }
    }

    private void loadResult(PropertyModel p, Result result) {
        InfoPropertyBuilder ipb = new InfoPropertyBuilder();
        ipb.property = p;
        try {
            result.buildProperties(ipb);
        } finally {
            ipb.dispose();
        }
    }

    private EventType getEventType(Event e) {
        EventType type;
        if (e instanceof Event.SendEvent) {
            type = ExecutionModel.SEND_TYPE;
        } else if (e instanceof Event.ReceiveEvent) {
            type = ExecutionModel.RECEIVE_TYPE;
        } else if (e instanceof Event.InternalEvent) {
            type = ExecutionModel.INTERNAL_TYPE;
        } else if (e instanceof Event.ResultEvent) {
            type = ExecutionModel.TERMINATE_TYPE;
        } else throw new Error();
        return type;
    }

    // Executes within AWT dispatch thread
    private void addEventToTimeline(Event e) {
        // Convert event to event model
        Node node = e.getHappensAt();
        int proc = -1;
        for (int i = 0; proc < 0 && i < nodes.length; i++) {
            if (nodes[i] == node) proc = nodeProcessIds[i];
        }
        EventType type = getEventType(e);
        int time = events.size();
        EventModel model = controller.timelineModel.createEvent(proc, type, time);
        controller.timelineModel.addEvent(model);
        events.add(e);
        eventModels.add(model);
        // If receive event, find matching send event, add message
        if (e instanceof Event.ReceiveEvent) {
            Event other = e.getMatchingEvent();
            EventModel otherModel = null;
            for (int i = 0, size = events.size(); otherModel == null && i < size; i++) {
                if (events.get(i) == other) otherModel = eventModels.get(i);
            }
            if (otherModel == null) throw new Error("Unable to match event");
            // Find pending message and remove it
            int j = -1;
            for (int i = 0, size = messageModels.size(); i < size; i++) {
                Object msg = messageModels.get(i);
                if (msg instanceof PendingMessageModel) {
                    PendingMessageModel pmsg = (PendingMessageModel) msg;
                    if (pmsg.getFrom() == otherModel && pmsg.getTo() == proc) {
                        j = i;
                        controller.timelineModel.removePendingMessage(pmsg);
                        break;
                    }
                }
            }
            if (j < 0) throw new Error("Pending message not found");
            // Construct message
            MessageModel msg = controller.timelineModel.createMessage(otherModel, model);
            messageModels.set(j, msg);
            controller.timelineModel.addMessage(msg);
        }
        // If send event, and no matching receive event, add pending message
        if (e instanceof Event.SendEvent) {
            Event other = e.getMatchingEvent();
            if (other == null) {
                Node receiver = ((Event.SendEvent) e).getReceiver();
                int process = -1;
                for (int i = 0; i < nodes.length; i++) {
                    if (nodes[i] == receiver) {
                        process = nodeProcessIds[i];
                        break;
                    }
                }
                if (process < 0) throw new Error("Unable to match process");
                // Construct pending message
                PendingMessageModel msg = controller.timelineModel.createPendingMessage(model, process);
                messageModels.add(msg);
                messageSendEvents.add(e);
                controller.timelineModel.addPendingMessage(msg);
            }
        }
        // Update time to fire time listener
        float timeline = controller.timelineModel.getCurrentTimeWithoutDelta();
        controller.timelineModel.setCurrentTime(timeline);
    }

    // Executes within worker thread
    private void loadNetwork(Network net) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            nodes = net.getNodes();
            nodeProcessIds = new int[nodes.length];
            nodeModels = new NodeModel[nodes.length];
            nodeInitialStates = new State[nodes.length];
            nodeLastStates = new State[nodes.length];
            nodeLastTermStatus = new Result[nodes.length];
            for (int i = 0; i < nodes.length; i++) {
                nodeProcessIds[i] = controller.timelineModel.addProcess(nodes[i].getLabel());
                float x = i, y = i; // default positions
                if (nodes[i].hasClientProperty(Node.CLIENT_PROPERTY_NODEMODEL)) {
                    nodeModels[i] = (NodeModel) nodes[i].getClientProperty(Node.CLIENT_PROPERTY_NODEMODEL);
                } else {
                    if (nodes[i].hasClientProperty(Node.CLIENT_PROPERTY_POSITION_X, Float.class))
                        x = (Float) nodes[i].getClientProperty(Node.CLIENT_PROPERTY_POSITION_X);
                    if (nodes[i].hasClientProperty(Node.CLIENT_PROPERTY_POSITION_Y, Float.class))
                        y = (Float) nodes[i].getClientProperty(Node.CLIENT_PROPERTY_POSITION_Y);
                    nodeModels[i] = controller.networkModel.createNode(x, y);
                    nodeModels[i].setLabel(nodes[i].getLabel());
                    controller.networkModel.addNode(nodeModels[i]);
                }
            }
            channels = net.getChannels();
            channelEdgeModels = new EdgeModel[channels.length];
            @SuppressWarnings("unchecked")
            ArrayList<Message>[] ics = (ArrayList<Message>[]) new ArrayList<?>[channels.length];
            channelStates = ics;
            for (int i = 0; i < channels.length; i++) {
                channelStates[i] = new ArrayList<Message>();
            }
            for (int i = 0; i < channels.length; i++) {
                GraphModel.NodeModel from = null, to = null;
                for (int j = 0; (from == null || to == null) && j < nodes.length; j++) {
                    if (channels[i].from == nodes[j]) {
                        from = nodeModels[j];
                    }
                    if (channels[i].to == nodes[j]) {
                        to = nodeModels[j];
                    }
                }
                if (from == null || to == null) throw new Error("Invalid channel model");
                int twice = -1;
                for (int k = 0; twice < 0 && k < i; k++) {
                    if (channels[k].from == channels[i].from && channels[k].to == channels[i].to) twice = k;
                    else if (channels[k].from == channels[i].to && channels[k].to == channels[i].from) twice = k;
                }
                if (channels[i].hasClientProperty(Channel.CLIENT_PROPERTY_EDGEMODEL)) {
                    channelEdgeModels[i] = (EdgeModel) channels[i].getClientProperty(Channel.CLIENT_PROPERTY_EDGEMODEL);
                } else {
                    // Construct closure variables
                    if (twice < 0) {
                        // Create a directed edge
                        channelEdgeModels[i] = controller.networkModel.createEdge(from, to);
                        channelEdgeModels[i].setDirected(true);
                        controller.networkModel.addEdge(channelEdgeModels[i]);
                    } else {
                        // Convert to a non-directed edge
                        channelEdgeModels[i] = channelEdgeModels[twice];
                        channelEdgeModels[i].setDirected(false);
                    }
                }
            }
            loadedNetwork = true;
        });
    }

    // Executes within worker thread
    private void loadInitialState(Execution ex) {
        executionRoot = ex;
        class LoadInitialState implements StateVisitor {
            public void setState(Node process, State state) {
                for (int i = 0; i < nodes.length; i++) {
                    if (nodes[i] == process) {
                        nodeInitialStates[i] = state;
                        return;
                    }
                }
                throw new Error("Unable to find node");
            }
        }
        executionRoot.getConfiguration().loadProcessState(new LoadInitialState());
        for (int i = 0; i < nodes.length; i++) {
            if (nodeInitialStates[i] == null) throw new Error("No initial state");
            nodeLastStates[i] = nodeInitialStates[i];
            nodeLastTermStatus[i] = null;
        }
    }

    // Executes within worker thread
    private void loadExecution(Execution ex, List<Execution> path) throws Exception {
        // Check validity
        if (path != null && path.size() > 0 && path.get(0) != ex) throw new Error("Invalid execution root");
        // Clear path
        executionPath.clear();
        // Only simulate after the given path elements
        if (path == null || path.size() == 0 || path.size() == 1) {
            clearEvents();
            loadInitialState(ex);
        } else {
            clearEventsAfter(path.size() - 2);
        }
        ExecutionStepper st = new ExecutionStepper(ex) {
            void step(Execution next) throws Exception {
                super.step(next);
                if (!replay) {
                    SwingUtilities.invokeAndWait(() -> {
                        addEventToTimeline(next.getLastEvent());
                    });
                }
            }
        };
        st.max_rounds = MAX_ROUNDS;
        // Replay original execution
        if (path != null) {
            st.replay = true;
            for (int i = 1, size = path.size() - 1; i < size; i++) {
                st.step(path.get(i));
            }
        }
        // Otherwise, just continue
        st.replay = false;
        // First do last, chosen path element
        if (path != null && path.size() > 1)
            st.step(path.get(path.size() - 1));
        while (st.hasNext()) {
            st.step(st.getNext());
        }
        executionPath.clear();
        executionPath.addAll(st.path);
        // Update time to update choice window

    }

    void afterSimulation(Runnable r) {
        performJob(() -> {
            SwingUtilities.invokeAndWait(r);
            return null;
        });
    }

    // Executes within AWT dispatch thread
    void stopSimulation() {
        performJob(() -> {
            clear();
            SwingUtilities.invokeAndWait(() -> {
                controller.refreshActions();
            });
            return null;
        });
    }

    // Executes within AWT dispatch thread
    void loadSimulation(Callable<DefaultSimulation> method) {
        performJob(() -> {
            clear();
            fresh = false;
            SwingUtilities.invokeAndWait(() -> {
                controller.refreshActions();
            });
            DefaultSimulation sim = method.call();
            loadNetwork(sim.getNetwork());
            loadExecution(sim.getExecution(), null);
            SwingUtilities.invokeAndWait(() -> {
                // Update time to update choice window
                float t = controller.timelineModel.getCurrentTimeWithoutDelta();
                controller.timelineModel.setCurrentTime(t);
            });
            return null;
        });
    }

    private void initWorkerThread() {
        worker = new Thread(new Runnable() {
            public void run() {
                try {
                    while (true) {
                        Callable<Void> job = queue.take();
                        SwingUtilities.invokeAndWait(() -> {
                            controller.setWaiting(true);
                        });
                        try {
                            job.call();
                        } catch (Exception ex) {
                            System.err.println("Worker thread job failed with exception");
                            ex.printStackTrace();
                        } finally {
                            SwingUtilities.invokeAndWait(() -> {
                                controller.setWaiting(false);
                            });
                        }
                    }
                } catch (Exception ex) {
                    System.err.println("Worker thread died with exception");
                    ex.printStackTrace();
                }
            }
        });
        worker.setDaemon(true);
        worker.start();
    }

    // Executes within AWT dispatch thread
    public void performJob(Callable<Void> call) {
        queue.add(call);
    }

    class InfoPropertyBuilder implements PropertyBuilder {

        PropertyModel property;
        private boolean disposed;

        public void simpleProperty(String name, String value) {
            if (disposed) throw new Error();
            PropertyModel p = controller.infoModel.createNestedProperty(property, name, value, InfoModel.SIMPLE_TYPE);
            controller.infoModel.addNestedProperty(property, p);
        }

        public void compoundProperty(String name, PropertyVisitor visitor) {
            if (disposed) throw new Error();
            InfoPropertyBuilder nest = new InfoPropertyBuilder();
            nest.property = controller.infoModel.createNestedProperty(property, name, null, InfoModel.COMPOUND_TYPE);
            try {
                visitor.buildProperties(nest);
            } finally {
                nest.dispose();
            }
            controller.infoModel.addNestedProperty(property, nest.property);
        }

        void dispose() {
            disposed = true;
        }

    }

}
