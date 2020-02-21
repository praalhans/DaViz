package com.aexiz.daviz.simulation.algorithm.wave;

import com.aexiz.daviz.frege.simulation.Process.TProcessDescription;
import com.aexiz.daviz.frege.simulation.algorithm.wave.Tree.TPS;
import com.aexiz.daviz.frege.simulation.algorithm.wave.Tree.TUP;
import com.aexiz.daviz.simulation.DefaultAlgorithm;
import com.aexiz.daviz.simulation.Assumption;
import com.aexiz.daviz.simulation.FregeHelper;
import com.aexiz.daviz.simulation.Information;
import com.aexiz.daviz.simulation.Information.*;
import com.aexiz.daviz.simulation.Channel;

import java.util.List;

import static com.aexiz.daviz.frege.simulation.algorithm.wave.Tree.procDesc;

public class Tree extends DefaultAlgorithm {

    public Tree() {
        assumption = new Assumption() {
            {
                acyclicGraph = true;
                decentralized_computed = true;
            }
        };
    }

    protected Message makeAndUnloadMessage(FregeHelper helper, Object o) {
        class TreeMessage extends Information.Message {
            public String toString() {
                return "*info*";
            }

            public boolean equals(Object obj) {
                return obj instanceof TreeMessage;
            }

            public void buildProperties(PropertyBuilder visitor) {
                visitor.simpleProperty("", "Info");
            }
        }
        Short t = (Short) o;
        if (t != 0) throw new Error("Invalid Haskell unit");
        return new TreeMessage();
    }

    protected State makeAndUnloadState(FregeHelper helper, Object o) {
        abstract class TreeUP implements PropertyVisitor {
        }
        class TreeState extends Information.State {
            List<Channel> neigh;
            TreeUP state;

            public String toString() {
                return "(" + neigh + "," + state + ")";
            }

            public void buildProperties(PropertyBuilder builder) {
                builder.compoundProperty("State", state);
                builder.compoundProperty("Neighbors", new PropertyVisitor() {
                    public void buildProperties(PropertyBuilder builder) {
                        builder.simpleProperty("", neigh.size() + " elements");
                        for (int i = 0, size = neigh.size(); i < size; i++) {
                            builder.simpleProperty(i + ":", neigh.get(i).to.getLabel());
                        }
                    }
                });
            }
        }
        class TreeUndefined extends TreeUP {
            public String toString() {
                return "Undefined";
            }

            public void buildProperties(PropertyBuilder builder) {
                builder.simpleProperty("", "Undefined");
            }
        }
        class TreeParent extends TreeUP {
            private Channel c;

            public String toString() {
                return "Parent<" + c + ">";
            }

            public void buildProperties(PropertyBuilder builder) {
                builder.simpleProperty("Parent:", c.to.getLabel());
            }
        }
        TPS st = (TPS) o;
        TreeState result = new TreeState();
        result.neigh = helper.forEdgeSet(st.mem$neigh.call());
        TUP up = st.mem$state.call();
        if (up.asUndefined() != null) {
            TreeUndefined r = new TreeUndefined();
            result.state = r;
        } else if (up.asParent() != null) {
            TreeParent r = new TreeParent();
            r.c = helper.getChannelByTuple(up.asParent().mem1.call());
            result.state = r;
        } else throw new Error();
        return result;
    }

    protected Result makeAndUnloadResult(FregeHelper helper, Object o) {
        class TreeResult extends Information.Result {
            public String toString() {
                return "Decided";
            }

            public void buildProperties(PropertyBuilder visitor) {
                visitor.simpleProperty("", "Decided");
            }
        }
        Short t = (Short) o;
        if (t != 0) throw new Error("Invalid Haskell unit");
        return new TreeResult();
    }

    protected TProcessDescription<Object, Object, Object, Object> getProcessDescription(FregeHelper helper) {
        return procDesc.call().simsalabim();
    }

}
