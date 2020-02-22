package com.aexiz.daviz.simulation.algorithm.wave.awerbuch;

import com.aexiz.daviz.simulation.Channel;
import com.aexiz.daviz.simulation.algorithm.information.state.AbstractAlgorithmState;
import com.aexiz.daviz.simulation.algorithm.information.state.PropertyVisitor;

import java.util.List;
import java.util.Map;

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
        properties = Map.of(
                "Has token?", String.valueOf(hasToken),
                "State", state,
                "Informing", makeNodesProperty(inform),
                "Waiting for", makeNodesProperty(acked),
                "Candidates", makeNodesProperty(forward),
                "Neighbors", makeNodesProperty(info),
                "Reply to:", last == null ? "None" : last.to.getLabel(),
                "Ack:", toAck == null ? "None" : toAck.to.getLabel()
        );
    }
}
