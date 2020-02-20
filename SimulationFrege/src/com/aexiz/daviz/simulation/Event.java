package com.aexiz.daviz.simulation;

import com.aexiz.daviz.frege.simulation.Event.TEvent;
import com.aexiz.daviz.frege.simulation.Event.TEvent.DEInternal;
import com.aexiz.daviz.frege.simulation.Event.TEvent.DEReceive;
import com.aexiz.daviz.frege.simulation.Event.TEvent.DEResult;
import com.aexiz.daviz.frege.simulation.Event.TEvent.DESend;
import com.aexiz.daviz.simulation.Viewpoint.Node;

import java.util.HashMap;
import java.util.List;

public abstract class Event extends Locus implements Cloneable {

    // Property
    Simulation simulation;
    Execution execution;

    // Haskell dependency
    transient TEvent<Object, Object, Object> hEvent;
    transient int hId;

    // Computed properties
    transient Viewpoint.Node happensAt;

    // Computed properties, unique to instance (not cloned)
    transient Event matchingEvent;
    transient Event previousEvent;

    Event() {
    }

    static void matchAndLinkEvents(List<Event> events) {
        // First we clear the state of all events
        for (Event old : events) {
            old.matchingEvent = null;
            old.previousEvent = null;
        }
        // Match send and receive events
        for (int i = 0, size = events.size(); i < size; i++) {
            Event event = events.get(i);
            if (event instanceof Event.ReceiveEvent) {
                Event.ReceiveEvent receive = (Event.ReceiveEvent) event;
                Information.Message rMsg = receive.getMessage();
                boolean matched = false;
                for (int j = 0; j < i; j++) {
                    Event other = events.get(j);
                    if (other instanceof Event.SendEvent) {
                        Event.SendEvent sender = (Event.SendEvent) other;
                        Information.Message sMsg = sender.getMessage();
                        if (sender.getReceiver() != receive.getHappensAt()) continue;
                        if (receive.getSender() != sender.getHappensAt()) continue;
                        if (sender.hasMatchingEvent()) continue;
                        if (!rMsg.equals(sMsg)) continue;
                        sender.matchingEvent = receive;
                        receive.matchingEvent = sender;
                        matched = true;
                        break;
                    }
                }
                if (!matched)
                    throw new Error("Unmatched Haskell receive event");
            }
        }
        // Build a linked list of events and their predecessor within the same process
        HashMap<Node, Event> map = new HashMap<>();
        for (Event event : events) {
            Node happens = event.getHappensAt();
            event.previousEvent = map.get(happens);
            map.put(happens, event);
        }
    }

    static Event makeAndUnload(TEvent<Object, Object, Object> e, Execution ex) {
        Event result;
        if (e.asEReceive() != null) {
            result = new ReceiveEvent();
        } else if (e.asEInternal() != null) {
            result = new InternalEvent();
        } else if (e.asESend() != null) {
            result = new SendEvent();
        } else if (e.asEResult() != null) {
            result = new ResultEvent();
        } else {
            throw new Error("Unknown Haskell event");
        }
        result.simulation = ex.simulation;
        result.execution = ex;
        result.hEvent = e;
        result.unload();
        return result;
    }

    public Simulation getSimulation() {
        invariant();
        return simulation;
    }

    public Execution getExecution() {
        invariant();
        return execution;
    }

    public boolean hasMatchingEvent() {
        return false;
    }

    public Event getMatchingEvent() {
        throw new Error("Only defined for Send or Receive");
    }

    void invariant() {
        if (simulation == null) throw new Error("Invalid simulation");
        if (execution == null) throw new Error("Invalid execution");
        if (hEvent == null) throw new Error("Invalid Haskell event");
    }

    void unload() {
        invariant();
        hId = TEvent.proc(hEvent);
        happensAt = simulation.getNetwork().getNodeById(hId);
    }

    public abstract Event clone();

    protected Event clone(Event to) {
        to.simulation = this.simulation;
        to.execution = this.execution;
        to.hEvent = this.hEvent;
        to.hId = this.hId;
        to.happensAt = this.happensAt;
        return to;
    }

    public Event getPreviousEvent() {
        return previousEvent;
    }

    // Computed properties

    public boolean hasHappensAt() {
        return true;
    }

    public Viewpoint.Node getHappensAt() {
        return happensAt;
    }

    public boolean hasNextState() {
        return false;
    }

    public Information.State getNextState() {
        throw new Error();
    }

    public boolean hasMessage() {
        return false;
    }

    public Information.Message getMessage() {
        throw new Error();
    }

    public boolean hasResult() {
        return false;
    }

    public Information.Result getResult() {
        throw new Error();
    }

    public boolean hasSender() {
        return false;
    }

    public Viewpoint.Node getSender() {
        throw new Error();
    }

    public boolean hasReceiver() {
        return false;
    }

    public Viewpoint.Node getReceiver() {
        throw new Error();
    }

    // Subclasses

    public static class ResultEvent extends Event {

        // Haksell dependencies
        transient DEResult<Object, Object, Object> hEvent;

        // Computed properties
        transient Information.Result result;

        ResultEvent() {
        }

        void unload() {
            super.unload();
            hEvent = super.hEvent.asEResult();
            SimulationHelper helper = new SimulationHelper(simulation);
            result = simulation.getAlgorithm().makeAndUnloadResult(helper, hEvent.mem$val.call());
        }

        protected ResultEvent clone(Event to) {
            super.clone(to);
            ResultEvent tor = (ResultEvent) to;
            tor.hEvent = this.hEvent;
            tor.result = this.result;
            return tor;
        }

        public boolean hasResult() {
            return true;
        }

        public Information.Result getResult() {
            return result;
        }

        public ResultEvent clone() {
            return clone(new ResultEvent());
        }

        public String toString() {
            return "Process " + happensAt.getLabel() + " results: " + result;
        }

    }

    public static class ReceiveEvent extends Event {

        // Haskell dependencies
        transient DEReceive<Object, Object, Object> hEvent;

        // Computed properties
        transient Information.Message message;
        transient Information.State nextState;
        transient Viewpoint.Node sender;

        ReceiveEvent() {
        }

        void unload() {
            super.unload();
            hEvent = super.hEvent.asEReceive();
            SimulationHelper helper = new SimulationHelper(simulation);
            message = simulation.getAlgorithm().makeAndUnloadMessage(helper, hEvent.mem$msg.call());
            nextState = simulation.getAlgorithm().makeAndUnloadState(helper, hEvent.mem$next.call());
            sender = simulation.getNetwork().getNodeById(hEvent.mem$send.call());
        }

        protected ReceiveEvent clone(Event to) {
            super.clone(to);
            ReceiveEvent tor = (ReceiveEvent) to;
            tor.hEvent = this.hEvent;
            tor.message = this.message;
            tor.nextState = this.nextState;
            tor.sender = this.sender;
            return tor;
        }

        public ReceiveEvent clone() {
            return clone(new ReceiveEvent());
        }

        public boolean hasMessage() {
            return true;
        }

        public Information.Message getMessage() {
            return message;
        }

        public boolean hasNextState() {
            return true;
        }

        public Information.State getNextState() {
            return nextState;
        }

        public boolean hasSender() {
            return true;
        }

        public Viewpoint.Node getSender() {
            return sender;
        }

        public boolean hasMatchingEvent() {
            return true;
        }

        public SendEvent getMatchingEvent() {
            if (matchingEvent == null) throw new Error("Unmatched receive event");
            return (SendEvent) matchingEvent;
        }

        public String toString() {
            return "Process " + happensAt.getLabel() + " receives " + message + " from " + sender;
        }

    }

    public static class SendEvent extends Event {

        // Haskell dependencies
        transient DESend<Object, Object, Object> hEvent;

        // Computed properties
        transient Information.Message message;
        transient Information.State nextState;
        transient Viewpoint.Node receiver;

        SendEvent() {
        }

        void unload() {
            super.unload();
            hEvent = super.hEvent.asESend();
            SimulationHelper helper = new SimulationHelper(simulation);
            message = simulation.getAlgorithm().makeAndUnloadMessage(helper, hEvent.mem$msg.call());
            nextState = simulation.getAlgorithm().makeAndUnloadState(helper, hEvent.mem$next.call());
            receiver = simulation.getNetwork().getNodeById(hEvent.mem$recv.call());
        }

        protected SendEvent clone(Event to) {
            super.clone(to);
            SendEvent tor = (SendEvent) to;
            tor.hEvent = this.hEvent;
            tor.message = this.message;
            tor.nextState = this.nextState;
            tor.receiver = this.receiver;
            return tor;
        }

        public SendEvent clone() {
            return clone(new SendEvent());
        }

        public boolean hasMessage() {
            return true;
        }

        public Information.Message getMessage() {
            return message;
        }

        public boolean hasNextState() {
            return true;
        }

        public Information.State getNextState() {
            return nextState;
        }

        public boolean hasReceiver() {
            return true;
        }

        public Viewpoint.Node getReceiver() {
            return receiver;
        }

        public boolean hasMatchingEvent() {
            return matchingEvent != null;
        }

        public ReceiveEvent getMatchingEvent() {
            return (ReceiveEvent) matchingEvent;
        }

        public String toString() {
            return "Process " + happensAt.getLabel() + " sends " + message + " to " + receiver;
        }

    }

    public static class InternalEvent extends Event {

        // Haskell dependencies
        transient DEInternal<Object, Object, Object> hEvent;

        // Computed properties
        transient Information.State nextState;

        InternalEvent() {
        }

        void unload() {
            super.unload();
            hEvent = super.hEvent.asEInternal();
            SimulationHelper helper = new SimulationHelper(simulation);
            nextState = simulation.getAlgorithm().makeAndUnloadState(helper, hEvent.mem$next.call());
        }

        protected InternalEvent clone(Event to) {
            super.clone(to);
            InternalEvent tor = (InternalEvent) to;
            tor.hEvent = this.hEvent;
            tor.nextState = this.nextState;
            return tor;
        }

        public InternalEvent clone() {
            return clone(new InternalEvent());
        }

        public boolean hasNextState() {
            return true;
        }

        public Information.State getNextState() {
            return nextState;
        }

        public String toString() {
            return "Process " + happensAt.getLabel() + " takes an internal action";
        }

    }

}
