package com.aexiz.daviz.simulation.algorithm.event;

import com.aexiz.daviz.frege.simulation.Event.TEvent;
import com.aexiz.daviz.simulation.AbstractEvent;
import com.aexiz.daviz.simulation.DefaultExecution;
import com.aexiz.daviz.simulation.Event;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public abstract class DefaultEvent extends AbstractEvent implements Cloneable, Event {
    // Haskell dependency
    protected transient TEvent<Object, Object, Object> hEvent;
    protected transient int hId;

    DefaultEvent() {
        super();
    }

    @NotNull
    static public Event makeAndUnload(TEvent<Object, Object, Object> event, @NotNull DefaultExecution execution) {
        DefaultEvent result = (DefaultEvent) mapFregeToJavaEvent(event);

        result.simulation = execution.getSimulation();
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
