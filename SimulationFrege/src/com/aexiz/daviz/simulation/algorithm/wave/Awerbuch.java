package com.aexiz.daviz.simulation.algorithm.wave;

import com.aexiz.daviz.frege.simulation.Process.TProcessDescription;
import com.aexiz.daviz.frege.simulation.algorithm.wave.Awerbuch.TMS;
import com.aexiz.daviz.frege.simulation.algorithm.wave.Awerbuch.TPS;
import com.aexiz.daviz.frege.simulation.algorithm.wave.Awerbuch.TRRRUII;
import com.aexiz.daviz.simulation.AbstractFregeBasicAlgorithm;
import com.aexiz.daviz.simulation.Channel;
import com.aexiz.daviz.simulation.FregeAlgorithm;
import com.aexiz.daviz.simulation.FregeHelper;
import com.aexiz.daviz.simulation.algorithm.information.*;
import com.aexiz.daviz.simulation.algorithm.wave.awerbuch.*;
import frege.prelude.PreludeBase.TMaybe.DJust;
import frege.prelude.PreludeBase.TTuple2;
import frege.run8.Thunk;

import java.util.List;

import static com.aexiz.daviz.frege.simulation.algorithm.wave.Awerbuch.procDesc;

public class Awerbuch extends AbstractFregeBasicAlgorithm {

    public Awerbuch() {
        assumption = AwerbuchAssumption.makeAssumption();
    }

    @Override
    public MessageInformation makeAndUnloadMessage(FregeHelper helper, Object o) {
        FregeAlgorithm.validateParameters(helper, o);

        short t = (Short) o;

        if (t == TMS.Token) return new AwerbuchToken();
        if (t == TMS.Inf) return new AwerbuchInfo();
        if (t == TMS.Ack) return new AwerbuchAck();
        throw new Error("Unknown message");
    }

    @Override
    public StateInformation makeAndUnloadState(FregeHelper helper, Object o) {
        FregeAlgorithm.validateParameters(helper, o);
        abstract class AwerbuchRRRUII implements PropertyVisitor {
        }
        class AwerbuchState implements StateInformation {
            boolean hasToken;
            PropertyVisitor rrruii;
            List<Channel> inform;
            List<Channel> acked;
            Channel intended;
            List<Channel> forward;
            List<Channel> info;
            Channel last;
            Channel toAck;

            public String toString() {
                return "(" + hasToken + "," + rrruii + "," + inform + "," + acked +
                        "," + intended + "," + forward + "," + info + "," + last +
                        "," + toAck + ")";
            }

            public void buildProperties(PropertyBuilder builder) {
                builder.simpleProperty("Has token?", String.valueOf(hasToken));
                builder.compoundProperty("State", rrruii);
                builder.compoundProperty("Informing", new PropertyVisitor() {
                    public void buildProperties(PropertyBuilder builder) {
                        builder.simpleProperty("", inform.size() + " elements");
                        for (int i = 0, size = inform.size(); i < size; i++) {
                            builder.simpleProperty(i + ":", inform.get(i).to.getLabel());
                        }
                    }
                });
                builder.compoundProperty("Waiting for", new PropertyVisitor() {
                    public void buildProperties(PropertyBuilder builder) {
                        builder.simpleProperty("", acked.size() + " elements");
                        for (int i = 0, size = acked.size(); i < size; i++) {
                            builder.simpleProperty(i + ":", acked.get(i).to.getLabel());
                        }
                    }
                });
                builder.simpleProperty("Token to:", intended == null ? "None" : intended.to.getLabel());
                builder.compoundProperty("Candidates", new PropertyVisitor() {
                    public void buildProperties(PropertyBuilder builder) {
                        builder.simpleProperty("", forward.size() + " elements");
                        for (int i = 0, size = forward.size(); i < size; i++) {
                            builder.simpleProperty(i + ":", forward.get(i).to.getLabel());
                        }
                    }
                });
                builder.compoundProperty("Neighbors", new PropertyVisitor() {
                    public void buildProperties(PropertyBuilder builder) {
                        builder.simpleProperty("", info.size() + " elements");
                        for (int i = 0, size = info.size(); i < size; i++) {
                            builder.simpleProperty(i + ":", info.get(i).to.getLabel());
                        }
                    }
                });
                builder.simpleProperty("Reply to:", last == null ? "None" : last.to.getLabel());
                builder.simpleProperty("Ack:", toAck == null ? "None" : toAck.to.getLabel());
            }
        }
        class AwerbuchReceivedSeen extends AwerbuchRRRUII {
            private Channel c;

            public String toString() {
                return "ReceivedSeen<" + c + ">";
            }

            public void buildProperties(PropertyBuilder builder) {
                builder.simpleProperty("", "Received");
                builder.simpleProperty("Seen token?", "true");
                builder.simpleProperty("From:", c.to.getLabel());
            }
        }
        class AwerbuchReceivedUnseen extends AwerbuchRRRUII {
            private Channel c;

            public String toString() {
                return "ReceivedUnseen<" + c + ">";
            }

            public void buildProperties(PropertyBuilder builder) {
                builder.simpleProperty("", "Received");
                builder.simpleProperty("Seen token?", "false");
                builder.simpleProperty("From:", c.to.getLabel());
            }
        }
        class AwerbuchUndefined extends AwerbuchRRRUII {
            public String toString() {
                return "Undefined";
            }

            public void buildProperties(PropertyBuilder builder) {
                builder.simpleProperty("", "Undefined");
            }
        }
        class AwerbuchInitiatorSeen extends AwerbuchRRRUII {
            public String toString() {
                return "InitiatorSeen";
            }

            public void buildProperties(PropertyBuilder builder) {
                builder.simpleProperty("", "Initiator");
                builder.simpleProperty("Seen token?", "true");
            }
        }
        class AwerbuchInitiatorUnseen extends AwerbuchRRRUII {
            public String toString() {
                return "InitiatorUnseen";
            }

            public void buildProperties(PropertyBuilder builder) {
                builder.simpleProperty("", "Initiator");
                builder.simpleProperty("Seen token?", "false");
            }
        }
        TPS st = (TPS) o;
        AwerbuchState result = new AwerbuchState();
        result.hasToken = st.mem$hasToken.call();
        TRRRUII rrruii = st.mem$state.call();

        if (rrruii.asReceivedSeen() != null) {
            AwerbuchReceivedSeen r = new AwerbuchReceivedSeen();
            r.c = helper.getChannelByTuple(rrruii.asReceivedSeen().mem1.call());
            result.rrruii = r;
        } else if (rrruii.asReceivedUnseen() != null) {
            AwerbuchReceivedUnseen r = new AwerbuchReceivedUnseen();
            r.c = helper.getChannelByTuple(rrruii.asReceivedUnseen().mem1.call());
            result.rrruii = r;
        } else if (rrruii.asReplied() != null) {
            result.rrruii = new AwerbuchReplied(helper.getChannelByTuple(rrruii.asReplied().mem1.call()));
        } else if (rrruii.asUndefined() != null) {
            result.rrruii = new AwerbuchUndefined();
        } else if (rrruii.asInitiatorSeen() != null) {
            result.rrruii = new AwerbuchInitiatorSeen();
        } else if (rrruii.asInitiatorUnseen() != null) {
            result.rrruii = new AwerbuchInitiatorUnseen();
        } else {
            throw new Error("Invalid RRRUII value");
        }
        DJust<TTuple2<Integer, Integer>> in;
        result.inform = helper.forEdgeSet(st.mem$inform.call());
        result.acked = helper.forEdgeSet(st.mem$acked.call());
        in = st.mem$intended.call().asJust();
        result.intended = in == null ? null : helper.getChannelByTuple(in.mem1.call());
        result.forward = helper.forEdgeSet(st.mem$forward.call());
        result.info = helper.forEdgeSet(st.mem$info.call());
        in = st.mem$last.call().asJust();
        result.last = in == null ? null : helper.getChannelByTuple(in.mem1.call());
        in = st.mem$toAck.call().asJust();
        result.toAck = in == null ? null : helper.getChannelByTuple(in.mem1.call());
        return result;
    }

    @Override
    public ResultInformation makeAndUnloadResult(FregeHelper helper, Object o) {
        FregeAlgorithm.validateParameters(helper, o);
        return (Boolean) o ? new AwerbuchTerminated() : new AwerbuchDecided();
    }

    @Override
    public TProcessDescription<Object, Object, Object, Object> getProcessDescription(FregeHelper helper) {
        return procDesc(Thunk.lazy(helper.getIdByNode(assumption.getInitiator()))).simsalabim();
    }

}
