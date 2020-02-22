package com.aexiz.daviz.simulation.algorithm.wave;

import com.aexiz.daviz.frege.simulation.Process.TProcessDescription;
import com.aexiz.daviz.frege.simulation.algorithm.wave.Tree.TPS;
import com.aexiz.daviz.frege.simulation.algorithm.wave.Tree.TUP;
import com.aexiz.daviz.simulation.*;
import com.aexiz.daviz.simulation.algorithm.information.*;
import com.aexiz.daviz.simulation.algorithm.wave.tree.TreeAssumption;
import com.aexiz.daviz.simulation.algorithm.wave.tree.TreeDecided;
import com.aexiz.daviz.simulation.algorithm.wave.tree.TreeInfo;
import com.aexiz.daviz.simulation.algorithm.wave.tree.TreeUndefined;

import java.util.List;

import static com.aexiz.daviz.frege.simulation.algorithm.wave.Tree.procDesc;

public class Tree extends AbstractFregeBasicAlgorithm {

    public Tree() {
        assumption = TreeAssumption.makeAssumption();
    }

    @Override
    public MessageInformation makeAndUnloadMessage(FregeHelper helper, Object o) {
        FregeAlgorithm.validateParameters(helper, o);
        if ((Short) o == 0) return new TreeInfo();
        throw new Error("Invalid Haskell unit");
    }

    @Override
    public StateInformation makeAndUnloadState(FregeHelper helper, Object o) {
        FregeAlgorithm.validateParameters(helper, o);
        abstract class TreeUP implements PropertyVisitor {
        }
        class TreeState implements StateInformation {
            List<Channel> neigh;
            PropertyVisitor state;

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

    @Override
    public ResultInformation makeAndUnloadResult(FregeHelper helper, Object o) {
        FregeAlgorithm.validateParameters(helper, o);
        if ((Short) o == 0) return new TreeDecided();
        throw new Error("Invalid Haskell unit");
    }

    @Override
    public TProcessDescription<Object, Object, Object, Object> getProcessDescription(FregeHelper helper) {
        return procDesc.call().simsalabim();
    }

}
