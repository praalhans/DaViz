package com.aexiz.daviz.simulation.algorithm.wave;

import com.aexiz.daviz.frege.simulation.Process.TProcessDescription;
import com.aexiz.daviz.frege.simulation.algorithm.wave.Cidon.TMS;
import com.aexiz.daviz.frege.simulation.algorithm.wave.Cidon.TPS;
import com.aexiz.daviz.frege.simulation.algorithm.wave.Cidon.TRRUI;
import com.aexiz.daviz.simulation.*;
import com.aexiz.daviz.simulation.Information.PropertyBuilder;
import com.aexiz.daviz.simulation.Information.PropertyVisitor;
import com.aexiz.daviz.simulation.Information.Result;
import com.aexiz.daviz.simulation.Viewpoint.Channel;
import frege.prelude.PreludeBase.TMaybe.DJust;
import frege.prelude.PreludeBase.TTuple2;
import frege.run8.Thunk;

import java.util.List;

import static com.aexiz.daviz.frege.simulation.algorithm.wave.Cidon.procDesc;

public class Cidon extends Algorithm {

    private static MaxRounds MAX_ROUNDS = new MaxRounds() {
        public int maxRounds(Network network) {
            return (network.getNodes().length + network.getChannels().length) * 15;
        }
    };

    public Cidon() {
        assumption = new Assumption() {
            {
                centralized_user = true;
            }
        };
    }

    public MaxRounds getMaxRounds() {
        return MAX_ROUNDS;
    }

    protected Information.Message makeAndUnloadMessage(GlueHelper help, Object o) {
        if (help == null || o == null) throw null;
        abstract class CidonMessage extends Information.Message {
        }
        class CidonToken extends CidonMessage {
            public String toString() {
                return "*token*";
            }

            public boolean equals(Object obj) {
                return obj instanceof CidonToken;
            }

            public void buildProperties(PropertyBuilder builder) {
                builder.simpleProperty("", "Token");
            }
        }
        class CidonInfo extends CidonMessage {
            public String toString() {
                return "*info*";
            }

            public boolean equals(Object obj) {
                return obj instanceof CidonInfo;
            }

            public void buildProperties(PropertyBuilder builder) {
                builder.simpleProperty("", "Info");
            }
        }
        Short t = (Short) o;
        if (t == TMS.Token) {
            return new CidonToken();
        } else if (t == TMS.Inf) {
            return new CidonInfo();
        } else throw new Error("Unknown message");
    }

    protected Information.State makeAndUnloadState(GlueHelper help, Object o) {
        if (help == null || o == null) throw null;
        abstract class CidonRRUI implements PropertyVisitor {
        }
        class CidonState extends Information.State {
            boolean hasToken;
            CidonRRUI rrui;
            Channel intention;
            List<Channel> forward;
            List<Channel> info;

            public String toString() {
                return "(" + hasToken + "," + rrui + "," + intention +
                        "," + forward + "," + info + ")";
            }

            public void buildProperties(PropertyBuilder builder) {
                builder.simpleProperty("Has token?", String.valueOf(hasToken));
                builder.compoundProperty("State", rrui);
                builder.simpleProperty("Token to:", intention == null ? "None" : intention.to.getLabel());
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
            }
        }
        class CidonReceived extends CidonRRUI {
            private Channel c;

            public String toString() {
                return "Received<" + c + ">";
            }

            public void buildProperties(PropertyBuilder builder) {
                builder.simpleProperty("", "Received");
                builder.simpleProperty("From:", c.to.getLabel());
            }
        }
        class CidonReplied extends CidonRRUI {
            private Channel c;

            public String toString() {
                return "Replied<" + c + ">";
            }

            public void buildProperties(PropertyBuilder builder) {
                builder.simpleProperty("", "Replied");
                builder.simpleProperty("To:", c.to.getLabel());
            }
        }
        class CidonUndefined extends CidonRRUI {
            public String toString() {
                return "Undefined";
            }

            public void buildProperties(PropertyBuilder builder) {
                builder.simpleProperty("", "Undefined");
            }
        }
        class CidonInitiator extends CidonRRUI {
            public String toString() {
                return "Initiator";
            }

            public void buildProperties(PropertyBuilder builder) {
                builder.simpleProperty("", "Initiator");
            }
        }
        TPS st = (TPS) o;
        CidonState result = new CidonState();
        result.hasToken = st.mem$hasToken.call();
        TRRUI rrui = st.mem$state.call();
        if (rrui.asReceived() != null) {
            CidonReceived r = new CidonReceived();
            r.c = help.getChannelByTuple(rrui.asReceived().mem1.call());
            result.rrui = r;
        } else if (rrui.asReplied() != null) {
            CidonReplied r = new CidonReplied();
            r.c = help.getChannelByTuple(rrui.asReplied().mem1.call());
            result.rrui = r;
        } else if (rrui.asUndefined() != null) {
            result.rrui = new CidonUndefined();
        } else if (rrui.asInitiator() != null) {
            result.rrui = new CidonInitiator();
        } else {
            throw new Error("Invalid RRUI value");
        }
        DJust<TTuple2<Integer, Integer>> in;
        in = st.mem$intention.call().asJust();
        result.intention = in == null ? null : help.getChannelByTuple(in.mem1.call());
        result.forward = help.forEdgeSet(st.mem$forward.call());
        result.info = help.forEdgeSet(st.mem$info.call());
        return result;
    }

    protected Result makeAndUnloadResult(GlueHelper helper, Object o) {
        class CidonTerminated extends Information.Result {
            public String toString() {
                return "Terminated";
            }

            public void buildProperties(PropertyBuilder builder) {
                builder.simpleProperty("", "Terminated");
            }
        }
        class CidonDecided extends Information.Result {
            public String toString() {
                return "Decided";
            }

            public void buildProperties(PropertyBuilder builder) {
                builder.simpleProperty("", "Decided");
            }
        }
        boolean result = (Boolean) o;
        if (result) {
            return new CidonTerminated();
        } else {
            return new CidonDecided();
        }
    }

    protected TProcessDescription<Object, Object, Object, Object> getProcessDescription(GlueHelper helper) {
        return procDesc(Thunk.lazy(helper.getIdByNode(assumption.getInitiator()))).simsalabim();
    }

}
