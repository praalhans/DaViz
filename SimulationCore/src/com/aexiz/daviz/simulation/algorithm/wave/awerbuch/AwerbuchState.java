package com.aexiz.daviz.simulation.algorithm.wave.awerbuch;

import com.aexiz.daviz.simulation.algorithm.information.state.AbstractAlgorithmState;
import com.aexiz.daviz.simulation.algorithm.information.state.PropertyVisitor;
import com.aexiz.daviz.simulation.viewpoint.Channel;

import java.util.HashMap;
import java.util.List;

public class AwerbuchState extends AbstractAlgorithmState {
    boolean hasToken;
    PropertyVisitor state;
    List<Channel> inform;
    List<Channel> acked;
    Channel intended;
    List<Channel> forward;
    List<Channel> info;
    Channel last;
    Channel toAck;

    @Override
    public String toString() {
        return "(" + hasToken + "," + state + "," + inform + "," + acked +
                "," + intended + "," + forward + "," + info + "," + last +
                "," + toAck + ")";
    }

    public void setHasToken(boolean hasToken) {
        this.hasToken = hasToken;
    }

    public void setState(PropertyVisitor state) {
        this.state = state;
    }

    public void setInform(List<Channel> inform) {
        this.inform = inform;
    }

    public void setAcked(List<Channel> acked) {
        this.acked = acked;
    }

    public void setIntended(Channel intended) {
        this.intended = intended;
    }

    public void setForward(List<Channel> forward) {
        this.forward = forward;
    }

    public void setInfo(List<Channel> info) {
        this.info = info;
    }

    public void setLast(Channel last) {
        this.last = last;
    }

    public void setToAck(Channel toAck) {
        this.toAck = toAck;
    }

    @Override
    public void makeProperties() {
        properties = new HashMap<String, Object>() {{
            put("Has token?", String.valueOf(hasToken));
            put("State", state);
            put("Informing", makeNodesProperty(inform));
            put("Waiting for", makeNodesProperty(acked));
            put("Candidates", makeNodesProperty(forward));
            put("Neighbors", makeNodesProperty(info));
            put("Reply to:", last == null ? "None" : last.to.getLabel());
            put("Ack:", toAck == null ? "None" : toAck.to.getLabel());
        }};
    }
}
