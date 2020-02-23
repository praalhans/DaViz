package com.aexiz.daviz.ui;

import com.aexiz.daviz.ui.JTimeline.JEvent;

import javax.swing.event.ChangeListener;
import java.io.Serializable;
import java.util.EventListener;
import java.util.EventObject;

public interface ExecutionModel {

    static final SendEventType SEND_TYPE = new SendEventType();
    static final ReceiveEventType RECEIVE_TYPE = new ReceiveEventType();
    static final InternalEventType INTERNAL_TYPE = new InternalEventType();
    static final TerminateEventType TERMINATE_TYPE = new TerminateEventType();
    static final DecideEventType DECIDE_TYPE = new DecideEventType();
    static final int MODE_SELECTION = 0;
    static final int MODE_SWAP = 1;

    void addChangeListener(ChangeListener l);

    void removeChangeListener(ChangeListener l);

    void addTimeEventListener(TimeEventListener l);

    void removeTimeEventListener(TimeEventListener l);

    void addCoarseTimeEventListener(CoarseTimeEventListener l);

    void removeCoarseTimeEventListener(CoarseTimeEventListener l);

    void addReorderEventListener(ReorderEventListener l);

    void removeReorderEventListener(ReorderEventListener l);

    EventModel createEvent(int process, EventType type, float time);

    MessageModel createMessage(EventModel from, EventModel to);

    PendingMessageModel createPendingMessage(EventModel from, int to);

    int getProcessCount();

    String getProcessName(int index);

    float getProcessMaxTime(int index);

    float getProcessLastTime(int index);

    float getProcessLastTimeWithoutDelta(int index);

    float getTemporaryMaxTime();

    float getMaxLastTime();

    float getMaxLastTimeWithoutDelta();

    void setCurrentTimeDelta(float delta);

    float getCurrentTime();

    void setCurrentTime(float time);

    float getCurrentTimeWithoutDelta();

    void validateTime();

    String[] getProcessName();

    int getEventCount();

    EventModel getEvent(int index);

    EventModel[] getEvent();

    EventModel[] getValidEvent();

    int getMessageCount();

    MessageModel getMessage(int index);

    MessageModel[] getMessage();

    MessageModel[] getValidMessage();

    int getPendingMessageCount();

    PendingMessageModel getPendingMessage(int index);

    PendingMessageModel[] getPendingMessage();

    PendingMessageModel[] getValidPendingMessage();

    EventModel[] getHappenedLastEvent();

    Object[] getHappenedTransitMessage();

    public int getEditMode();

    public void setEditMode(int mode);

    interface EventModel {

        void addChangeListener(ChangeListener l);

        void removeChangeListener(ChangeListener l);

        int getProcessIndex();

        EventType getEventType();

        float getTime();

        void setTime(float time);

        float getTimeWithoutDelta();

        float getAscention();

        void setDelta(float time);

        void validate();

        boolean isRollover();

        void setRollover(boolean b);

        boolean isPressed();

        void setPressed(boolean b);

        boolean isLeader();

        ExecutionModel getParent();

    }

    interface MessageModel {

        void addChangeListener(ChangeListener l);

        void removeChangeListener(ChangeListener l);

        EventModel getFrom();

        EventModel getTo();

        boolean isConflicting();

        ExecutionModel getParent();

    }

    interface PendingMessageModel {

        EventModel getFrom();

        int getTo();

        ExecutionModel getParent();

    }

    interface ReorderEventListener extends EventListener {

        void reorderStarted(ReorderEvent e);

        void reorderUpdating(ReorderEvent e);

        void reorderEnded(ReorderEvent e);

    }

    interface TimeEventListener extends EventListener {

        void timeChanged(EventObject e);

    }

    interface CoarseTimeEventListener extends TimeEventListener {
    }

    static abstract class EventType implements Serializable {
        private static final long serialVersionUID = -7469784089240414551L;

        EventType() {
        }

        public abstract String toString();
    }

    static class SendEventType extends EventType {
        private static final long serialVersionUID = 3989437777413291201L;

        SendEventType() {
        }

        public String toString() {
            return "Send";
        }
    }

    static class ReceiveEventType extends EventType {
        private static final long serialVersionUID = -8024401864871586063L;

        ReceiveEventType() {
        }

        public String toString() {
            return "Receive";
        }
    }

    static class InternalEventType extends EventType {
        private static final long serialVersionUID = -1467758305981819533L;

        InternalEventType() {
        }

        public String toString() {
            return "Internal";
        }
    }

    static class TerminateEventType extends EventType {
        private static final long serialVersionUID = -2241813359102096812L;

        TerminateEventType() {
        }

        public String toString() {
            return "Terminate";
        }
    }

    static class DecideEventType extends EventType {
        private static final long serialVersionUID = -8806882625351510880L;

        DecideEventType() {
        }

        public String toString() {
            return "Decide";
        }
    }

    static class ReorderEvent extends EventObject {

        public static final int PROGRESS = 0;
        public static final int START = 1;
        public static final int START_INFEASIBLE = 2;
        public static final int END_COMMIT = 3;
        public static final int END_CANCEL = 4;
        private static final long serialVersionUID = -5054396395438782856L;
        EventModel leader;
        EventModel target;
        int type;
        float progress;
        JEvent leaderComponent;
        JEvent targetComponent;

        public ReorderEvent(EventModel leader, EventModel target, int type) {
            this(leader, target, type, 0.0f);
        }

        public ReorderEvent(EventModel leader, EventModel target, int type, float progress) {
            super(leader);
            if (leader == null || target == null) throw null;
            if (type < PROGRESS || type > END_CANCEL || progress != progress)
                throw new IllegalArgumentException();
            this.leader = leader;
            this.target = target;
            this.type = type;
            this.progress = progress;
        }

        public ReorderEvent(JEvent leader, JEvent target, int type, float progress) {
            super(leader);
            this.leaderComponent = leader;
            this.leader = leader.getModel();
            this.targetComponent = target;
            this.target = target.getModel();
            this.type = type;
            this.progress = progress;
        }

        public static ReorderEvent createFromComponent(ReorderEvent event, JTimeline timeline) {
            return new ReorderEvent(
                    timeline.findEventComponent(event.leader),
                    timeline.findEventComponent(event.target),
                    event.type, event.progress);
        }

        public EventModel getLeaderModel() {
            return leader;
        }

        public EventModel getTargetModel() {
            return target;
        }

        public JEvent getLeader() {
            return leaderComponent;
        }

        public JEvent getTarget() {
            return targetComponent;
        }

        public int getType() {
            return type;
        }

        public float getProgress() {
            return progress;
        }

    }

}
