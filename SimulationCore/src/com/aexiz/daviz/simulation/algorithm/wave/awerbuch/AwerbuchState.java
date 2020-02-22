package com.aexiz.daviz.simulation.algorithm.wave.awerbuch;

import com.aexiz.daviz.simulation.Channel;
import com.aexiz.daviz.simulation.algorithm.information.PropertyBuilder;
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

    public void makeProperty() {
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

//    public void buildProperties(PropertyBuilder builder) {
//        builder.compoundProperty(;
//        builder.compoundProperty(, new PropertyVisitor() {
//            public void buildProperties(PropertyBuilder builder) {
//                builder.simpleProperty("", inform.size() + " elements");
//                for (int i = 0, size = inform.size(); i < size; i++) {
//                    builder.simpleProperty(i + ":", inform.get(i).to.getLabel());
//                }
//            }
//        });
//        builder.compoundProperty("Waiting for", new PropertyVisitor() {
//            public void buildProperties(PropertyBuilder builder) {
//                builder.simpleProperty("", acked.size() + " elements");
//                for (int i = 0, size = acked.size(); i < size; i++) {
//                    builder.simpleProperty(i + ":", acked.get(i).to.getLabel());
//                }
//            }
//        });
//        builder.simpleProperty("Token to:", intended == null ? "None" : intended.to.getLabel());
//        builder.compoundProperty("Candidates", new PropertyVisitor() {
//            public void buildProperties(PropertyBuilder builder) {
//                builder.simpleProperty("", forward.size() + " elements");
//                for (int i = 0, size = forward.size(); i < size; i++) {
//                    builder.simpleProperty(i + ":", forward.get(i).to.getLabel());
//                }
//            }
//        });
//        builder.compoundProperty("Neighbors", new PropertyVisitor() {
//            public void buildProperties(PropertyBuilder builder) {
//                builder.simpleProperty("", info.size() + " elements");
//                for (int i = 0, size = info.size(); i < size; i++) {
//                    builder.simpleProperty(i + ":", info.get(i).to.getLabel());
//                }
//            }
//        });
//        builder.simpleProperty("Reply to:", last == null ? "None" : last.to.getLabel());
//        builder.simpleProperty("Ack:", toAck == null ? "None" : toAck.to.getLabel());
//    }
}
