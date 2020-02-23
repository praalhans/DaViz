package com.aexiz.daviz.ui;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EventObject;

/**
 * An execution model consists of zero or more processes, numbered $0, 1, \ldots, n - 1$.
 * <p>
 * Each event has an associated process number, $0 \leq e_p \le n$, an extensible type
 * value (disjoint: send, receive, internal) and time $0 \leq e_t$. For the same process,
 * no two events have the same time value.
 * <p>
 * Each message has a sender event and a receiver event. We assume that these events
 * occur at different processes, but they may occur at the same time.
 * <p>
 * Events are grouped together, and within one process no events may overlap. Also,
 * messages being sent may not occur at an event after the message being received. The
 * model will maintain these constraints.
 */
public class DefaultExecutionModel implements ExecutionModel {

    protected transient ChangeEvent changeEvent;
    protected float lastCoarseTime = Float.NaN;
    protected transient EventObject timeEvent;
    DefaultEventModel movingLeader;
    DefaultEventModel reorderTarget;
    float totalTime;
    float lastDelta;
    private EventListenerList listenerList = new EventListenerList();
    private ArrayList<String> processNames = new ArrayList<>();
    private float tempMaxTime = 0.0f;
    private float currentTime;
    private float delta;
    private ArrayList<DefaultEventModel> events = new ArrayList<>();
    private ArrayList<EventGroup> eventGroups = new ArrayList<>();
    private ArrayList<DefaultMessageModel> messages = new ArrayList<>();
    private ArrayList<DefaultPendingMessageModel> pendingMessages = new ArrayList<>();
    private int editMode = MODE_SELECTION;

    public void addChangeListener(ChangeListener l) {
        listenerList.add(ChangeListener.class, l);
    }

    public void removeChangeListener(ChangeListener l) {
        listenerList.remove(ChangeListener.class, l);
    }

    protected void fireStateChanged() {
        if (changeEvent == null)
            changeEvent = new ChangeEvent(this);
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2)
            if (listeners[i] == ChangeListener.class)
                ((ChangeListener) listeners[i + 1]).stateChanged(changeEvent);
    }

    public void addTimeEventListener(TimeEventListener l) {
        listenerList.add(TimeEventListener.class, l);
    }

    public void removeTimeEventListener(TimeEventListener l) {
        listenerList.remove(TimeEventListener.class, l);
    }

    public void addCoarseTimeEventListener(CoarseTimeEventListener l) {
        listenerList.add(CoarseTimeEventListener.class, l);
    }

    public void removeCoarseTimeEventListener(CoarseTimeEventListener l) {
        listenerList.remove(CoarseTimeEventListener.class, l);
    }

    protected void fireTimeChanged() {
        if (timeEvent == null)
            timeEvent = new EventObject(this);
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2)
            if (listeners[i] == TimeEventListener.class)
                ((TimeEventListener) listeners[i + 1]).timeChanged(timeEvent);
        float time = getCurrentTime();
        if (time == lastCoarseTime)
            return;
        lastCoarseTime = time;
        for (int i = listeners.length - 2; i >= 0; i -= 2)
            if (listeners[i] == CoarseTimeEventListener.class)
                ((TimeEventListener) listeners[i + 1]).timeChanged(timeEvent);
    }

    public void addReorderEventListener(ReorderEventListener l) {
        listenerList.add(ReorderEventListener.class, l);
    }

    public void removeReorderEventListener(ReorderEventListener l) {
        listenerList.remove(ReorderEventListener.class, l);
    }

    protected void fireReorderStarted(EventModel leader, EventModel target) {
        ReorderEvent reorderEvent = new ReorderEvent(leader, target, ReorderEvent.START);
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2)
            if (listeners[i] == ReorderEventListener.class)
                ((ReorderEventListener) listeners[i + 1]).reorderStarted(reorderEvent);
    }

    protected void fireReorderInfeasible(EventModel leader, EventModel target) {
        ReorderEvent reorderEvent = new ReorderEvent(leader, target, ReorderEvent.START_INFEASIBLE);
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2)
            if (listeners[i] == ReorderEventListener.class)
                ((ReorderEventListener) listeners[i + 1]).reorderStarted(reorderEvent);
    }

    protected void fireReorderCanceled(EventModel leader, EventModel target) {
        ReorderEvent reorderEvent = new ReorderEvent(leader, target, ReorderEvent.END_CANCEL);
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2)
            if (listeners[i] == ReorderEventListener.class)
                ((ReorderEventListener) listeners[i + 1]).reorderEnded(reorderEvent);
    }

    protected void fireReorderCommitted(EventModel leader, EventModel target) {
        ReorderEvent reorderEvent = new ReorderEvent(leader, target, ReorderEvent.END_COMMIT);
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2)
            if (listeners[i] == ReorderEventListener.class)
                ((ReorderEventListener) listeners[i + 1]).reorderEnded(reorderEvent);
    }

    protected void fireReorderUpdating(EventModel leader, EventModel target, float progress) {
        ReorderEvent reorderEvent = new ReorderEvent(leader, target, ReorderEvent.PROGRESS, progress);
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2)
            if (listeners[i] == ReorderEventListener.class)
                ((ReorderEventListener) listeners[i + 1]).reorderUpdating(reorderEvent);
    }

    float getReorderingAscention(DefaultEventModel e) {
        if (movingLeader == null || reorderTarget == null)
            return 0.0f;
        if (movingLeader.group.events.contains(e))
            return (float) (Math.sin(lastDelta / totalTime * Math.PI) / 2.0);
        if (reorderTarget.group.events.contains(e))
            return (float) (-Math.sin(lastDelta / totalTime * Math.PI) / 2.0);
        return 0.0f;
    }

    float getReorderingDelta(DefaultEventModel e) {
        if (movingLeader == null || reorderTarget == null)
            return 0.0f;
        EventGroup m = movingLeader.group, r = reorderTarget.group;
        float ratio = m.getTimeLength() / r.getTimeLength();
        if (m.events.contains(e))
            return lastDelta;
        if (r.events.contains(e))
            return -lastDelta * ratio;
        return 0.0f;
    }

    boolean startOrUpdateReordering(EventGroup group, float delta) {
        if (movingLeader == null) return false;
        DefaultEventModel target = group.getLeader();
        if (!target.isAllowedToReorderWith(movingLeader)) return false;
        if (reorderTarget != null && reorderTarget != target) stopReordering();
        if (reorderTarget != target) {
            reorderTarget = target;
            totalTime = reorderTarget.group.getTimeLength();
            fireReorderStarted(movingLeader, reorderTarget);
        } else {
            fireReorderUpdating(movingLeader, reorderTarget, delta);
        }
        if (delta > totalTime) delta = totalTime;
        if (delta < -totalTime) delta = -totalTime;
        lastDelta = delta;
        return true;
    }

    void stopReordering() {
        if (movingLeader == null || reorderTarget == null) return;
        fireReorderCanceled(movingLeader, reorderTarget);
        reorderTarget = null;
    }

    void startMoving(DefaultEventModel leader) {
        if (movingLeader != null) stopMoving();
        movingLeader = leader;
    }

    void stopMoving() {
        if (reorderTarget != null) stopReordering();
        if (movingLeader != null) {
            movingLeader = null;
        }
    }

    public DefaultEventModel createEvent(int process, EventType type, float time) {
        return new DefaultEventModel(process, type, time);
    }

    public DefaultMessageModel createMessage(EventModel from, EventModel to) {
        return new DefaultMessageModel((DefaultEventModel) from, (DefaultEventModel) to);
    }

    public PendingMessageModel createPendingMessage(EventModel from, int to) {
        return new DefaultPendingMessageModel((DefaultEventModel) from, to);
    }

    public int getProcessCount() {
        return processNames.size();
    }

    public void setProcessCount(int size) {
        int n = processNames.size();
        for (int i = n; i < size; i++)
            processNames.add("P" + i);
        n = processNames.size();
        for (int i = size; i < n; i++)
            processNames.remove(size);
        fireStateChanged();
    }

    public void setProcessName(int index, String p) {
        if (p.length() == 0)
            throw new IllegalArgumentException();
        processNames.set(index, p);
        fireStateChanged();
    }

    public String getProcessName(int index) {
        return processNames.get(index);
    }

    public String[] getProcessName() {
        return processNames.toArray(new String[processNames.size()]);
    }

    public void setProcessName(String[] ps) {
        processNames.clear();
        for (String p : ps) {
            if (p.length() == 0)
                throw new IllegalArgumentException();
            processNames.add(p);
        }
        adjustCurrentTime();
        fireStateChanged();
    }

    public float getProcessMaxTime(int index) {
        float result = Float.MAX_VALUE;
        for (DefaultEventModel event : events) {
            if (event.process != index) continue;
            if (event.type instanceof ExecutionModel.DecideEventType ||
                    event.type instanceof ExecutionModel.TerminateEventType) {
                float time = event.getTime();
                if (time < result) result = time;
            }
        }
        return result;
    }

    public float getProcessLastTime(int index) {
        float result = 0.0f;
        for (DefaultEventModel event : events) {
            if (event.process != index) continue;
            float time = event.getTime();
            if (time > result) result = time;
        }
        return result + 1.0f; // compute end time
    }

    public float getProcessLastTimeWithoutDelta(int index) {
        float result = 0.0f;
        for (DefaultEventModel event : events) {
            if (event.process != index) continue;
            float time = event.getTimeWithoutDelta();
            if (time > result) result = time;
        }
        return result + 1.0f; // compute end time
    }

    public void clearTemporaryMaxTime() {
        tempMaxTime = 0.0f;
    }

    public float getTemporaryMaxTime() {
        return tempMaxTime;
    }

    public void setTemporaryMaxTime(float maxTime) {
        tempMaxTime = maxTime;
    }

    public float getMaxLastTime() {
        float result = 0.0f;
        for (int i = 0, size = processNames.size(); i < size; i++) {
            float time = getProcessLastTime(i);
            if (time > result) result = time;
        }
        return result;
    }

    public float getMaxLastTimeWithoutDelta() {
        float result = 0.0f;
        for (int i = 0, size = processNames.size(); i < size; i++) {
            float time = getProcessLastTimeWithoutDelta(i);
            if (time > result) result = time;
        }
        return result;
    }

    private void adjustCurrentTime() {
        float maxTime = getMaxLastTime();
        if (currentTime > maxTime) {
            currentTime = maxTime;
            fireTimeChanged();
        }
    }

    public void setCurrentTimeDelta(float delta) {
        this.delta = delta;
        fireTimeChanged();
    }

    public float getCurrentTime() {
        float result = currentTime + delta;
        float maxTime = getMaxLastTime();
        if (result > maxTime) {
            result = maxTime;
        }
        if (result < 0.0f) result = 0.0f;
        return result;
    }

    public void setCurrentTime(float time) {
        if (time < 0.0f) time = 0.0f;
        currentTime = Math.round(time);
        delta = 0.0f;
        float maxTime = getMaxLastTime();
        if (currentTime > maxTime) {
            currentTime = maxTime;
        }
        lastCoarseTime = Float.NaN; // Force event
        fireTimeChanged();
    }

    public float getCurrentTimeWithoutDelta() {
        return currentTime;
    }

    public void validateTime() {
        setCurrentTime(getCurrentTime());
    }

    public int addProcess(String p) {
        if (p.length() == 0)
            throw new IllegalArgumentException();
        int n = processNames.size();
        processNames.add(p);
        fireStateChanged();
        return n;
    }

    public int getEventCount() {
        return events.size();
    }

    public void setEvent(int index, EventModel e) {
        addEvent0(index, e);
        fireStateChanged();
    }

    public EventModel getEvent(int index) {
        return events.get(index);
    }

    public EventModel[] getEvent() {
        return events.toArray(new EventModel[events.size()]);
    }

    public void setEvent(EventModel[] es) {
        events.clear();
        for (EventModel e : es)
            addEvent0(-1, e);
        adjustCurrentTime();
        fireStateChanged();
    }

    public void addEvent(EventModel e) {
        addEvent0(-1, e);
        fireStateChanged();
    }

    public boolean removeEvent(EventModel e) {
        if (e == null) throw null;
        DefaultEventModel de = (DefaultEventModel) e;
        if (de.getParent() != this) throw new IllegalArgumentException();
        boolean result = false;
        for (int i = 0, size = events.size(); i < size; i++) {
            if (events.get(i) == e) {
                events.remove(i);
                result = true;
                break;
            }
        }
        if (result) {
            clearEventGroups();
            updateEventGroups();
            fireStateChanged();
        }
        return result;
    }

    private void addEvent0(int index, EventModel e) {
        if (e == null) throw null;
        DefaultEventModel de = (DefaultEventModel) e;
        if (de.getParent() != this) throw new IllegalArgumentException();
        EventModel old = index < 0 ? null : events.get(index);
        for (DefaultEventModel f : events) {
            if (f == old) continue;
            if (f.overlaps(de)) throw new IllegalArgumentException();
        }
        if (index < 0) events.add(de);
        else events.set(index, de);
        updateEventGroups();
    }

    EventGroup findPreviousGroup(DefaultEventModel e) {
        float maxTime = -Float.MAX_VALUE;
        EventGroup max = null;
        for (EventGroup g : eventGroups) {
            if (g == e.group) continue;
            if (g.process != e.process) continue;
            DefaultEventModel l = g.getLeader();
            if (l.time >= e.time) continue;
            if (maxTime < l.time) {
                maxTime = l.time;
                max = g;
            }
        }
        return max;
    }

    EventGroup findNextGroup(DefaultEventModel e) {
        float minTime = Float.MAX_VALUE;
        EventGroup min = null;
        for (EventGroup g : eventGroups) {
            if (g == e.group) continue;
            if (g.process != e.process) continue;
            DefaultEventModel l = g.getLeader();
            if (l.time <= e.time) continue;
            if (minTime > l.time) {
                minTime = l.time;
                min = g;
            }
        }
        return min;
    }

    private void clearEventGroups() {
        eventGroups.clear();
        for (DefaultEventModel e : events) {
            e.group = null;
        }
    }

    private void updateEventGroups() {
        for (DefaultEventModel e : events) {
            if (e.group != null) continue;
            EventGroup prev;
            if (e.isLeader() || (prev = findPreviousGroup(e)) == null)
                eventGroups.add(new EventGroup(e));
            else prev.add(e);
        }
    }

    public int getMessageCount() {
        return messages.size();
    }

    public void setMessage(int index, MessageModel m) {
        addMessage0(index, m);
        fireStateChanged();
    }

    public MessageModel getMessage(int index) {
        return messages.get(index);
    }

    public MessageModel[] getMessage() {
        return messages.toArray(new MessageModel[messages.size()]);
    }

    public void setMessage(MessageModel[] ms) {
        messages.clear();
        for (MessageModel m : ms)
            addMessage0(-1, m);
        fireStateChanged();
    }

    public void addMessage(MessageModel m) {
        addMessage0(-1, m);
        fireStateChanged();
    }

    public boolean removeMessage(MessageModel m) {
        if (m == null) throw null;
        DefaultMessageModel dm = (DefaultMessageModel) m;
        if (dm.getParent() != this) throw new IllegalArgumentException();
        boolean result = false;
        for (int i = 0, size = messages.size(); i < size; i++) {
            if (messages.get(i) == m) {
                messages.remove(i);
                result = true;
                break;
            }
        }
        if (result) fireStateChanged();
        return result;
    }

    private void addMessage0(int index, MessageModel m) {
        if (m == null)
            throw null;
        DefaultMessageModel dm = (DefaultMessageModel) m;
        if (dm.getParent() != this) throw new IllegalArgumentException();
        DefaultMessageModel old = index < 0 ? null : messages.get(index);
        for (DefaultMessageModel k : messages) {
            if (k == old) continue;
            if (k.from.equals(dm.from) && k.to.equals(dm.to))
                throw new IllegalArgumentException();
        }
        if (index < 0) messages.add(dm);
        else messages.set(index, dm);
    }

    public int getPendingMessageCount() {
        return pendingMessages.size();
    }

    public void setPendingMessage(int index, PendingMessageModel m) {
        addPendingMessage0(index, m);
        fireStateChanged();
    }

    public PendingMessageModel getPendingMessage(int index) {
        return pendingMessages.get(index);
    }

    public PendingMessageModel[] getPendingMessage() {
        return pendingMessages.toArray(new PendingMessageModel[pendingMessages.size()]);
    }

    public void setPendingMessage(PendingMessageModel[] ms) {
        pendingMessages.clear();
        for (PendingMessageModel m : ms)
            addPendingMessage0(-1, m);
        fireStateChanged();
    }

    public void addPendingMessage(PendingMessageModel m) {
        addPendingMessage0(-1, m);
        fireStateChanged();
    }

    public boolean removePendingMessage(PendingMessageModel m) {
        if (m == null) throw null;
        DefaultPendingMessageModel dm = (DefaultPendingMessageModel) m;
        if (dm.getParent() != this) throw new IllegalArgumentException();
        boolean result = false;
        for (int i = 0, size = pendingMessages.size(); i < size; i++) {
            if (pendingMessages.get(i) == m) {
                pendingMessages.remove(i);
                result = true;
                break;
            }
        }
        if (result) fireStateChanged();
        return result;
    }

    private void addPendingMessage0(int index, PendingMessageModel m) {
        if (m == null)
            throw null;
        DefaultPendingMessageModel dm = (DefaultPendingMessageModel) m;
        if (dm.getParent() != this) throw new IllegalArgumentException();
        DefaultPendingMessageModel old = index < 0 ? null : pendingMessages.get(index);
        for (DefaultPendingMessageModel k : pendingMessages) {
            if (k == old) continue;
            if (k.from.equals(dm.from) && k.to == dm.to)
                throw new IllegalArgumentException();
        }
        if (index < 0) pendingMessages.add(dm);
        else pendingMessages.set(index, dm);
    }

    public EventModel[] getValidEvent() {
        ArrayList<EventModel> result = new ArrayList<>();
        for (int i = 0, n = processNames.size(); i < n; i++) {
            for (DefaultEventModel event : events)
                if (event.process == i)
                    result.add(event);
        }
        return result.toArray(new EventModel[result.size()]);
    }

    public MessageModel[] getValidMessage() {
        ArrayList<MessageModel> result = new ArrayList<>();
        int n = processNames.size();
        for (DefaultMessageModel m : messages) {
            if (events.contains(m.from) && m.from.process < n &&
                    events.contains(m.to) && m.to.process < n)
                result.add(m);
        }
        return result.toArray(new MessageModel[result.size()]);
    }

    public PendingMessageModel[] getValidPendingMessage() {
        ArrayList<PendingMessageModel> result = new ArrayList<>();
        int n = processNames.size();
        for (DefaultPendingMessageModel m : pendingMessages) {
            if (events.contains(m.from) && m.from.process < n && m.to < n)
                result.add(m);
        }
        return result.toArray(new PendingMessageModel[result.size()]);
    }

    public EventModel[] getHappenedLastEvent() {
        ArrayList<EventModel> happenedEvents = new ArrayList<>();
        for (int i = 0, n = processNames.size(); i < n; i++) {
            for (DefaultEventModel event : events)
                if (event.process == i && event.getTimeWithoutDelta() < getCurrentTimeWithoutDelta())
                    happenedEvents.add(event);
        }
        ArrayList<EventModel> result = new ArrayList<>();
        for (EventModel happened : happenedEvents) {
            EventModel foundAfter = null;
            EventModel foundBefore = null;
            for (EventModel other : result) {
                if (other.getProcessIndex() == happened.getProcessIndex() &&
                        happened.getTimeWithoutDelta() > other.getTimeWithoutDelta()) {
                    foundBefore = other;
                }
                if (other.getProcessIndex() == happened.getProcessIndex() &&
                        happened.getTimeWithoutDelta() <= other.getTimeWithoutDelta()) {
                    foundAfter = other;
                }
            }
            if (foundBefore != null) result.remove(foundBefore);
            if (foundAfter != null) continue;
            result.add(happened);
        }
        return result.toArray(new EventModel[result.size()]);
    }

    public Object[] getHappenedTransitMessage() {
        ArrayList<Object> result = new ArrayList<>();
        int n = processNames.size();
        for (DefaultMessageModel m : messages) {
            if (events.contains(m.from) && m.from.process < n &&
                    events.contains(m.to) && m.to.process < n &&
                    m.from.getTimeWithoutDelta() < getCurrentTimeWithoutDelta() &&
                    getCurrentTimeWithoutDelta() <= m.to.getTimeWithoutDelta())
                result.add(m);
        }
        for (DefaultPendingMessageModel m : pendingMessages) {
            if (events.contains(m.from) && m.from.process < n && m.to < n &&
                    m.from.getTimeWithoutDelta() < getCurrentTimeWithoutDelta())
                result.add(m);
        }
        return result.toArray(new Object[result.size()]);
    }

    public void clear() {
        events.clear();
        eventGroups.clear();
        messages.clear();
        pendingMessages.clear();
        processNames.clear();
        stopMoving();
        adjustCurrentTime();
        fireStateChanged();
    }

    public void clearEventsAndMessages() {
        events.clear();
        eventGroups.clear();
        messages.clear();
        pendingMessages.clear();
        adjustCurrentTime();
        fireStateChanged();
    }

    public int getEditMode() {
        return editMode;
    }

    public void setEditMode(int mode) {
        if (mode != MODE_SELECTION && mode != MODE_SWAP)
            throw new IllegalArgumentException();
        int old = editMode;
        editMode = mode;
        if (mode != old) {
            fireStateChanged();
        }
    }

    class DefaultEventModel implements EventModel {

        static final float MAX_NEAR_DIST = 1f;
        protected transient ChangeEvent changeEvent;
        int process;
        EventType type;
        float time;
        float delta;
        EventGroup group;
        boolean rollover;
        boolean pressed;
        private EventListenerList listenerList = new EventListenerList();

        DefaultEventModel(int process, EventType type, float time) {
            if (type == null)
                throw null;
            if (process < 0 || time < 0.0f)
                throw new IllegalArgumentException();
            this.process = process;
            this.type = type;
            this.time = time;
        }

        public boolean isAllowedToReorderWith(DefaultEventModel otherLeader) {
            if (type instanceof TerminateEventType || type instanceof DecideEventType)
                return false;
            if (otherLeader.type instanceof TerminateEventType || otherLeader.type instanceof DecideEventType)
                return false;
            return true;
        }

        public DefaultExecutionModel getParent() {
            return DefaultExecutionModel.this;
        }

        public void addChangeListener(ChangeListener l) {
            listenerList.add(ChangeListener.class, l);
        }

        public void removeChangeListener(ChangeListener l) {
            listenerList.remove(ChangeListener.class, l);
        }

        protected void fireStateChanged() {
            Object[] listeners = listenerList.getListenerList();
            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] == ChangeListener.class) {
                    if (changeEvent == null)
                        changeEvent = new ChangeEvent(this);
                    ((ChangeListener) listeners[i + 1]).stateChanged(changeEvent);
                }
            }
        }

        public int getProcessIndex() {
            return process;
        }

        public EventType getEventType() {
            return type;
        }

        public float getTime() {
            return time + delta + getReorderingDelta(this);
        }

        public void setTime(float time) {
            this.time = time;
            this.delta = 0.0f;
            adjustCurrentTime();
            fireStateChanged();
        }

        public float getTimeWithoutDelta() {
            return time;
        }

        public float getAscention() {
            return getReorderingAscention(this);
        }

        public boolean isLeader() {
            if (group != null)
                return group.getLeader() == this;
            return (type instanceof ReceiveEventType) ||
                    (type instanceof DecideEventType) ||
                    (type instanceof TerminateEventType);
        }

        public void setDelta(float delta) {
            this.delta = delta;
            validateGroupConstraints();
            if (group != null) group.setDelta(this.delta);
            else setDeltaByGroup(this.delta);
        }

        void setDeltaByGroup(float delta) {
            this.delta = delta;
            fireStateChanged();
        }

        public void validate() {
            validateMessageConstraints();
            if (group != null) group.setDelta(this.delta);
            else setDeltaByGroup(this.delta);
            if (group != null) group.validate();
            else validateByGroup();
        }

        void validateByGroup() {
            time = (float) Math.round(time + delta);
            delta = 0.0f;
            adjustCurrentTime();
            fireStateChanged();
        }

        void validateMessageConstraints() {
            Iterable<DefaultEventModel> events = (group != null) ?
                    group.events : Collections.singletonList(this);
            for (DefaultEventModel e : events) {
                for (DefaultMessageModel m : messages) {
                    DefaultEventModel leader, trailer;
                    if (m.to == e) {
                        leader = m.from;
                        trailer = e;
                        if (trailer.time + delta < leader.time)
                            delta = leader.time - trailer.time;
                    } else if (m.from == e) {
                        leader = e;
                        trailer = m.to;
                        if (leader.time + delta > trailer.time)
                            delta = trailer.time - leader.time;
                    } else continue;
                }
            }
        }

        void validateGroupConstraints() {
            DefaultEventModel leader = this, trailer = this;
            if (group != null) leader = group.getLeader();
            if (group != null) trailer = group.getTrailer();
            if (leader.time + delta < 0.0f) delta = -leader.time;
            boolean stopReorder = true;
            EventGroup prev = findPreviousGroup(this);
            if (prev != null) {
                float d = prev.getTrailer().time + MAX_NEAR_DIST;
                if (leader.time + delta < d) {
                    if (startOrUpdateReordering(prev, leader.time + delta - prev.getTrailer().time - 1.0f)) {
                        delta = d - leader.time;
                        stopReorder = false;
                    } else {
                        delta = d - leader.time;
                    }
                }
            }
            EventGroup next = findNextGroup(this);
            if (next != null) {
                float d = next.getLeader().time - MAX_NEAR_DIST;
                if (trailer.time + delta > d) {
                    if (startOrUpdateReordering(next, trailer.time + delta - next.getLeader().time + 1.0f)) {
                        delta = d - trailer.time;
                        stopReorder = false;
                    } else {
                        delta = d - trailer.time;
                    }
                }
            }
            if (stopReorder) stopReordering();
        }

        public boolean isRollover() {
            return rollover && movingLeader == null;
        }

        public void setRollover(boolean b) {
            if (rollover != b) {
                rollover = b;
                fireStateChanged();
            }
        }

        public boolean isPressed() {
            return pressed;
        }

        public void setPressed(boolean b) {
            if (pressed != b) {
                pressed = b;
                if (b) startMoving(this);
                else stopMoving();
                fireStateChanged();
            }
        }

        boolean overlaps(DefaultEventModel e) {
            return process == e.process && time == e.time;
        }

        public String toString() {
            return "DefaultEvent@" + process + "[" + type + "]-" + time;
        }

    }

    class EventGroup {

        int process;
        ArrayList<DefaultEventModel> events = new ArrayList<>();

        EventGroup(DefaultEventModel e) {
            process = e.process;
            add(e);
            steal(findPreviousGroup(e));
        }

        void add(DefaultEventModel e) {
            if (e.group != null)
                e.group.events.remove(e);
            e.group = this;
            events.add(e);
        }

        float getTimeLength() {
            return getTrailer().time - getLeader().time + 1.0f;
        }

        DefaultEventModel getLeader() {
            DefaultEventModel min = events.get(0);
            for (DefaultEventModel e : events)
                if (e.time < min.time) min = e;
            return min;
        }

        DefaultEventModel getTrailer() {
            DefaultEventModel max = events.get(0);
            for (DefaultEventModel e : events)
                if (e.time > max.time) max = e;
            return max;
        }

        void setDelta(float delta) {
            for (DefaultEventModel e : events)
                e.setDeltaByGroup(delta);
        }

        void validate() {
            for (DefaultEventModel e : events)
                e.validateByGroup();
        }

        private void steal(EventGroup o) {
            if (o == null) return;
            DefaultEventModel l = getLeader();
            for (DefaultEventModel e : o.events)
                if (e.time > l.time) add(e);
        }

    }

    class DefaultMessageModel implements MessageModel {

        protected transient ChangeEvent changeEvent;
        DefaultEventModel from;
        DefaultEventModel to;
        private EventListenerList listenerList = new EventListenerList();

        DefaultMessageModel(DefaultEventModel from, DefaultEventModel to) {
            if (from.getParent() != DefaultExecutionModel.this
                    || to.getParent() != DefaultExecutionModel.this
                    || !(from.type instanceof SendEventType)
                    || !(to.type instanceof ReceiveEventType)
                    || from.process == to.process)
                throw new IllegalArgumentException();
            this.from = from;
            this.to = to;
        }

        public DefaultExecutionModel getParent() {
            return DefaultExecutionModel.this;
        }

        public void addChangeListener(ChangeListener l) {
            listenerList.add(ChangeListener.class, l);
        }

        public void removeChangeListener(ChangeListener l) {
            listenerList.remove(ChangeListener.class, l);
        }

        protected void fireStateChanged() {
            Object[] listeners = listenerList.getListenerList();
            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] == ChangeListener.class) {
                    if (changeEvent == null)
                        changeEvent = new ChangeEvent(this);
                    ((ChangeListener) listeners[i + 1]).stateChanged(changeEvent);
                }
            }
        }

        public DefaultEventModel getFrom() {
            return from;
        }

        public DefaultEventModel getTo() {
            return to;
        }

        public boolean isConflicting() {
            return to.getTime() < from.getTime();
        }

    }

    class DefaultPendingMessageModel implements PendingMessageModel {

        DefaultEventModel from;
        int to;

        DefaultPendingMessageModel(DefaultEventModel from, int to) {
            if (from.getParent() != DefaultExecutionModel.this
                    || !(from.type instanceof SendEventType)
                    || to < 0)
                throw new IllegalArgumentException();
            this.from = from;
            this.to = to;
        }

        public EventModel getFrom() {
            return from;
        }

        public int getTo() {
            return to;
        }

        public ExecutionModel getParent() {
            return DefaultExecutionModel.this;
        }

    }

}
