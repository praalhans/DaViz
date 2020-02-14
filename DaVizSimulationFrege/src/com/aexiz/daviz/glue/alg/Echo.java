package com.aexiz.daviz.glue.alg;

import com.aexiz.daviz.glue.Algorithm;
import com.aexiz.daviz.glue.Assumption;
import com.aexiz.daviz.glue.GlueHelper;
import com.aexiz.daviz.glue.Information;
import com.aexiz.daviz.glue.Information.Message;
import com.aexiz.daviz.glue.Information.PropertyBuilder;
import com.aexiz.daviz.glue.Information.PropertyVisitor;
import com.aexiz.daviz.glue.Information.Result;
import com.aexiz.daviz.glue.Information.State;
import com.aexiz.daviz.glue.Viewpoint.Channel;
import com.aexiz.daviz.sim.Process.TProcessDescription;
import com.aexiz.daviz.sim.Echo.TPS;
import com.aexiz.daviz.sim.Echo.TMS;
import com.aexiz.daviz.sim.Echo.TRRUI;
import static com.aexiz.daviz.sim.Echo.procDesc;

import frege.run8.Thunk;

import java.util.List;

public class Echo extends Algorithm {

	public Echo() {
		assumption = new Assumption() {
			{
				centralized_user = true;
			}
		};
	}
	
	protected Message makeAndUnloadMessage(GlueHelper helper, Object o) {
		class EchoMessage extends Information.Message {
			public String toString() {
				return "*broadcast*";
			}
			public boolean equals(Object obj) {
				if (obj instanceof EchoMessage) return true;
				return false;
			}
			public void buildProperties(PropertyBuilder visitor) {
				visitor.simpleProperty("", "Broadcast");
			}
		}
		Short t = (Short) o;
		if (t == TMS.Broadcast) return new EchoMessage();
		throw new Error("Invalid message");
	}

	protected State makeAndUnloadState(GlueHelper helper, Object o) {
		abstract class EchoRRUI implements PropertyVisitor {
		}
		class EchoState extends Information.State {
			List<Channel> neighbors;
			List<Channel> children;
			EchoRRUI state;
			public String toString() {
				return "(" + neighbors + "," + children + "," + state + ")";
			}
			public void buildProperties(PropertyBuilder builder) {
				builder.compoundProperty("State", state);
				builder.compoundProperty("Neighbors", new PropertyVisitor() {
					public void buildProperties(PropertyBuilder builder) {
						builder.simpleProperty("", neighbors.size() + " elements");
						for (int i = 0, size = neighbors.size(); i < size; i++) {
							builder.simpleProperty(String.valueOf(i) + ":", neighbors.get(i).to.getLabel());
						}
					}
				});
				builder.compoundProperty("Children", new PropertyVisitor() {
					public void buildProperties(PropertyBuilder builder) {
						builder.simpleProperty("", children.size() + " elements");
						for (int i = 0, size = children.size(); i < size; i++) {
							builder.simpleProperty(String.valueOf(i) + ":", children.get(i).to.getLabel());
						}
					}
				});
			}
		}
		class EchoUndefined extends EchoRRUI {
			public String toString() { return "Undefined"; }
			public void buildProperties(PropertyBuilder builder) {
				builder.simpleProperty("", "Undefined");
			}
		}
		class EchoInitiator extends EchoRRUI {
			public String toString() {
				return "Initiator";
			}
			public void buildProperties(PropertyBuilder builder) {
				builder.simpleProperty("", "Initiator");
			}
		}
		class EchoReceived extends EchoRRUI {
			private Channel c;
			public String toString() {
				return "Received<" + c + ">";
			}
			public void buildProperties(PropertyBuilder builder) {
				builder.simpleProperty("Received:", c.to.getLabel());
			}
		}
		class EchoReplied extends EchoRRUI {
			private Channel c;
			public String toString() {
				return "Replied<" + c + ">";
			}
			public void buildProperties(PropertyBuilder builder) {
				builder.simpleProperty("Replied:", c.to.getLabel());
			}
		}
		TPS st = (TPS) o;
		EchoState result = new EchoState();
		result.neighbors = helper.forEdgeSet(st.mem$neighbors.call());
		result.children = helper.forEdgeSet(st.mem$children.call());
		TRRUI up = st.mem$state.call();
		if (up.asUndefined() != null) {
			EchoUndefined r = new EchoUndefined();
			result.state = r;
		} else if (up.asInitiator() != null) {
			EchoInitiator r = new EchoInitiator();
			result.state = r;
		} else if (up.asReceived() != null) {
			EchoReceived r = new EchoReceived();
			r.c = helper.getChannelByTuple(up.asReceived().mem1.call());
			result.state = r;
		} else if (up.asReplied() != null) {
			EchoReplied r = new EchoReplied();
			r.c = helper.getChannelByTuple(up.asReplied().mem1.call());
			result.state = r;
		} else throw new Error();
		return result;
	}

	protected Result makeAndUnloadResult(GlueHelper helper, Object o) {
		class TreeAckDecided extends Information.Result {
			public String toString() { return "Decided"; }
			public void buildProperties(PropertyBuilder visitor) {
				visitor.simpleProperty("", "Decided");
			}
		}
		class TreeAckTerminated extends Information.Result {
			public String toString() { return "Terminated"; }
			public void buildProperties(PropertyBuilder visitor) {
				visitor.simpleProperty("", "Terminated");
			}
		}
		boolean t = (Boolean) o;
		if (t) return new TreeAckDecided();
		else return new TreeAckTerminated();
	}

	protected TProcessDescription<Object, Object, Object, Object> getProcessDescription(GlueHelper helper) {
		return procDesc(Thunk.lazy(helper.getIdByNode(assumption.getInitiator()))).simsalabim();
	}
	
}
