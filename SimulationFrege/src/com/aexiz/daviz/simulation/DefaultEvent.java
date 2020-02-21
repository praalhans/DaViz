package com.aexiz.daviz.simulation;

import com.aexiz.daviz.frege.simulation.Event.TEvent;
import com.aexiz.daviz.simulation.event.tReceiveEvent;
import com.aexiz.daviz.simulation.event.tSendEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

public abstract class DefaultEvent extends AbstractEvent implements Cloneable, Event {
    // Haskell dependency
    protected transient TEvent<Object, Object, Object> hEvent;
    protected transient int hId;

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
            if (event instanceof tReceiveEvent) {
                ReceiveEvent receive = (ReceiveEvent) event;
                Information.Message rMsg = receive.getMessage();
                boolean matched = false;
                for (int j = 0; j < i; j++) {
                    DefaultEvent other = events.get(j);
                    if (other instanceof tSendEvent) {
                        SendEvent sender = (SendEvent) other;
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

    protected void unload() {
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

    protected DefaultEvent clone(DefaultEvent to) {
        super.clone(to);
        to.hEvent = this.hEvent;
        to.hId = this.hId;
        return to;
    }
}
