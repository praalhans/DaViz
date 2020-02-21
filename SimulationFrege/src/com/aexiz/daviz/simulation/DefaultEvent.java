package com.aexiz.daviz.simulation;

import com.aexiz.daviz.frege.simulation.Event.TEvent;
import com.aexiz.daviz.frege.simulation.Event.TEvent.DEInternal;
import com.aexiz.daviz.frege.simulation.Event.TEvent.DEReceive;
import com.aexiz.daviz.frege.simulation.Event.TEvent.DEResult;
import com.aexiz.daviz.frege.simulation.Event.TEvent.DESend;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

public abstract class DefaultEvent extends AbstractEvent implements Cloneable, Event {
    // Haskell dependency
    transient TEvent<Object, Object, Object> hEvent;
    transient int hId;

    DefaultEvent() {
    }

    static void matchAndLinkEvents(@NotNull List<DefaultEvent> events) {
        // First we clear the state of all events
        for (DefaultEvent old : events) {
            old.matchingEvent = null;
            old.previousEvent = null;
        }
        // Match send and receive events
        for (int i = 0, size = events.size(); i < size; i++) {
            DefaultEvent event = events.get(i);
            if (event instanceof DefaultEvent.ReceiveEvent) {
                DefaultEvent.ReceiveEvent receive = (DefaultEvent.ReceiveEvent) event;
                Information.Message rMsg = receive.getMessage();
                boolean matched = false;
                for (int j = 0; j < i; j++) {
                    DefaultEvent other = events.get(j);
                    if (other instanceof DefaultEvent.SendEvent) {
                        DefaultEvent.SendEvent sender = (DefaultEvent.SendEvent) other;
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
        HashMap<Node, DefaultEvent> map = new HashMap<>();
        for (DefaultEvent event : events) {
            Node happens = event.getHappensAt();
            event.previousEvent = map.get(happens);
            map.put(happens, event);
        }
    }

    @NotNull
    static Event makeAndUnload(TEvent<Object, Object, Object> event, @NotNull DefaultExecution execution) {
        DefaultEvent result = (DefaultEvent) mapFregeToJavaEvent(event);

        result.simulation = execution.simulation;
        result.execution = execution;
        result.hEvent = event;
        result.unload();

        return result;
    }

    @NotNull
    @Contract("_ -> new")
    private static Event mapFregeToJavaEvent(@NotNull TEvent<Object, Object, Object> event) {
        if (event.asEReceive() != null) {
            return new ReceiveEvent();
        } else if (event.asEInternal() != null) {
            return new InternalEvent();
        } else if (event.asESend() != null) {
            return new SendEvent();
        } else if (event.asEResult() != null) {
            return new ResultEvent();
        } else {
            throw new Error("Unknown Haskell event");
        }
    }

    void unload() {
        isInvariant();
        hId = TEvent.proc(hEvent);
        happensAt = simulation.getNetwork().getNodeById(hId);
    }

    @Override
    protected void isInvariant() {
        super.isInvariant();
        if (hEvent == null) throw new Error("Invalid Haskell event");
    }

    @Override
    public abstract DefaultEvent clone();

    @Override
    protected Event clone(Event to) {
        ((DefaultEvent)to).hEvent = this.hEvent;
        ((DefaultEvent)to).hId = this.hId;
        return to;
    }

    public static class ResultEvent extends DefaultEvent {

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
            result = ((DefaultAlgorithm)simulation.getAlgorithm()).makeAndUnloadResult(helper, hEvent.mem$val.call());
        }

        protected ResultEvent clone(DefaultEvent to) {
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

    public static class ReceiveEvent extends DefaultEvent {

        // Haskell dependencies
        transient DEReceive<Object, Object, Object> hEvent;

        // Computed properties
        transient Information.Message message;
        transient Information.State nextState;
        transient Node sender;

        ReceiveEvent() {
        }

        void unload() {
            super.unload();
            hEvent = super.hEvent.asEReceive();
            SimulationHelper helper = new SimulationHelper(simulation);
            message = ((DefaultAlgorithm)simulation.getAlgorithm()).makeAndUnloadMessage(helper, hEvent.mem$msg.call());
            nextState = ((DefaultAlgorithm)simulation.getAlgorithm()).makeAndUnloadState(helper, hEvent.mem$next.call());
            sender = simulation.getNetwork().getNodeById(hEvent.mem$send.call());
        }

        protected ReceiveEvent clone(DefaultEvent to) {
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

        public Node getSender() {
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

    public static class SendEvent extends DefaultEvent {

        // Haskell dependencies
        transient DESend<Object, Object, Object> hEvent;

        // Computed properties
        transient Information.Message message;
        transient Information.State nextState;
        transient Node receiver;

        SendEvent() {
        }

        void unload() {
            super.unload();
            hEvent = super.hEvent.asESend();
            SimulationHelper helper = new SimulationHelper(simulation);
            message = ((DefaultAlgorithm)simulation.getAlgorithm()).makeAndUnloadMessage(helper, hEvent.mem$msg.call());
            nextState = ((DefaultAlgorithm)simulation.getAlgorithm()).makeAndUnloadState(helper, hEvent.mem$next.call());
            receiver = simulation.getNetwork().getNodeById(hEvent.mem$recv.call());
        }

        protected SendEvent clone(DefaultEvent to) {
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

        public Node getReceiver() {
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

    public static class InternalEvent extends DefaultEvent {

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
            nextState = ((DefaultAlgorithm)simulation.getAlgorithm()).makeAndUnloadState(helper, hEvent.mem$next.call());
        }

        protected InternalEvent clone(DefaultEvent to) {
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
