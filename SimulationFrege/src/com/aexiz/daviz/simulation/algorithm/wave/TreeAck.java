package com.aexiz.daviz.simulation.algorithm.wave;

import com.aexiz.daviz.frege.simulation.Process.TProcessDescription;
import com.aexiz.daviz.frege.simulation.algorithm.wave.TreeAck.TMS;
import com.aexiz.daviz.frege.simulation.algorithm.wave.TreeAck.TPS;
import com.aexiz.daviz.frege.simulation.algorithm.wave.TreeAck.TUPDS;
import com.aexiz.daviz.simulation.Assumption;
import com.aexiz.daviz.simulation.Channel;
import com.aexiz.daviz.simulation.DefaultAlgorithm;
import com.aexiz.daviz.simulation.FregeHelper;
import com.aexiz.daviz.simulation.algorithm.information.*;

import java.util.List;

import static com.aexiz.daviz.frege.simulation.algorithm.wave.TreeAck.procDesc;

public class TreeAck extends DefaultAlgorithm {

    public TreeAck() {
        assumption = new Assumption() {
            {
                acyclicGraph = true;
                decentralized_computed = true;
            }
        };
    }

    protected MessageInformation makeAndUnloadMessage(FregeHelper helper, Object o) {
        abstract class TreeAckMessage implements MessageInformation {
        }
        class TreeAckInfoMessage extends TreeAckMessage {
            public String toString() {
                return "*info*";
            }

            public boolean equals(Object obj) {
                return obj instanceof TreeAckInfoMessage;
            }

            public void buildProperties(PropertyBuilder visitor) {
                visitor.simpleProperty("", "Info");
            }
        }
        class TreeAckAckMessage extends TreeAckMessage {
            public String toString() {
                return "*ack*";
            }

            public boolean equals(Object obj) {
                return obj instanceof TreeAckAckMessage;
            }

            public void buildProperties(PropertyBuilder visitor) {
                visitor.simpleProperty("", "Ack");
            }
        }
        Short t = (Short) o;
        if (t == TMS.Info) return new TreeAckInfoMessage();
        if (t == TMS.Ack) return new TreeAckAckMessage();
        throw new Error("Invalid message");
    }

    protected StateInformation makeAndUnloadState(FregeHelper helper, Object o) {
        abstract class TreeAckUPDS implements PropertyVisitor {
        }
        class TreeAckState implements StateInformation {
            List<Channel> neighbors;
            List<Channel> children;
            TreeAckUPDS state;

            public String toString() {
                return "(" + neighbors + "," + children + "," + state + ")";
            }

            public void buildProperties(PropertyBuilder builder) {
                builder.compoundProperty("State", state);
                builder.compoundProperty("Neighbors", new PropertyVisitor() {
                    public void buildProperties(PropertyBuilder builder) {
                        builder.simpleProperty("", neighbors.size() + " elements");
                        for (int i = 0, size = neighbors.size(); i < size; i++) {
                            builder.simpleProperty(i + ":", neighbors.get(i).to.getLabel());
                        }
                    }
                });
                builder.compoundProperty("Children", new PropertyVisitor() {
                    public void buildProperties(PropertyBuilder builder) {
                        builder.simpleProperty("", children.size() + " elements");
                        for (int i = 0, size = children.size(); i < size; i++) {
                            builder.simpleProperty(i + ":", children.get(i).to.getLabel());
                        }
                    }
                });
            }
        }
        class TreeAckUndefined extends TreeAckUPDS {
            public String toString() {
                return "Undefined";
            }

            public void buildProperties(PropertyBuilder builder) {
                builder.simpleProperty("", "Undefined");
            }
        }
        class TreeAckParent extends TreeAckUPDS {
            private Channel c;

            public String toString() {
                return "Parent<" + c + ">";
            }

            public void buildProperties(PropertyBuilder builder) {
                builder.simpleProperty("Parent:", c.to.getLabel());
            }
        }
        class TreeAckDecider extends TreeAckUPDS {
            private Channel c;

            public String toString() {
                return "Decider<" + c + ">";
            }

            public void buildProperties(PropertyBuilder builder) {
                builder.simpleProperty("Decider:", c.to.getLabel());
            }
        }
        class TreeAckSpreader extends TreeAckUPDS {
            private Channel c;

            public String toString() {
                return "Spreader<" + c + ">";
            }

            public void buildProperties(PropertyBuilder builder) {
                builder.simpleProperty("Spreader:", c.to.getLabel());
            }
        }
        TPS st = (TPS) o;
        TreeAckState result = new TreeAckState();
        result.neighbors = helper.forEdgeSet(st.mem$neighbors.call());
        result.children = helper.forEdgeSet(st.mem$children.call());
        TUPDS up = st.mem$state.call();
        if (up.asUndefined() != null) {
            TreeAckUndefined r = new TreeAckUndefined();
            result.state = r;
        } else if (up.asParent() != null) {
            TreeAckParent r = new TreeAckParent();
            r.c = helper.getChannelByTuple(up.asParent().mem1.call());
            result.state = r;
        } else if (up.asDecider() != null) {
            TreeAckDecider r = new TreeAckDecider();
            r.c = helper.getChannelByTuple(up.asDecider().mem1.call());
            result.state = r;
        } else if (up.asSpreader() != null) {
            TreeAckSpreader r = new TreeAckSpreader();
            r.c = helper.getChannelByTuple(up.asSpreader().mem1.call());
            result.state = r;
        } else throw new Error();
        return result;
    }

    protected ResultInformation makeAndUnloadResult(FregeHelper helper, Object o) {
        class TreeAckTerminated extends TerminationInformation {
        }

        class TreeAckDecided extends DecidedInformation {
        }

        boolean t = (Boolean) o;
        if (t) return new TreeAckDecided();
        else return new TreeAckTerminated();
    }

    protected TProcessDescription<Object, Object, Object, Object> getProcessDescription(FregeHelper helper) {
        return procDesc.call().simsalabim();
    }

}
