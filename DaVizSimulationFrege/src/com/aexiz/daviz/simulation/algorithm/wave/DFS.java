package com.aexiz.daviz.simulation.algorithm.wave;

import java.util.List;

import com.aexiz.daviz.simulation.Algorithm;
import com.aexiz.daviz.simulation.Assumption;
import com.aexiz.daviz.simulation.Viewpoint.*;
import com.aexiz.daviz.simulation.GlueHelper;
import com.aexiz.daviz.simulation.Information;
import com.aexiz.daviz.simulation.Information.PropertyBuilder;
import com.aexiz.daviz.simulation.Information.PropertyVisitor;
import com.aexiz.daviz.simulation.Information.Result;
import com.aexiz.daviz.frege.simulation.alg.DFS.TRRUI;
import static com.aexiz.daviz.frege.simulation.alg.DFS.procDesc;

import com.aexiz.daviz.frege.simulation.Process.TProcessDescription;
import com.aexiz.daviz.frege.simulation.Set.TSet;

import frege.prelude.PreludeBase.TMaybe;
import frege.prelude.PreludeBase.TMaybe.DJust;
import frege.prelude.PreludeBase.TTuple2;
import frege.prelude.PreludeBase.TTuple4;
import frege.run8.Thunk;

public class DFS extends Algorithm {
	
	public DFS() {
		assumption = new Assumption() {
			{
				centralized_user = true;
			}
		};
	}
	
	protected Information.Message makeAndUnloadMessage(GlueHelper help, Object o) {
		if (help == null || o == null) throw null;
		class DFS_Message extends Information.Message {
			public String toString() {
				return "*token*";
			}
			public boolean equals(Object obj) {
				if (obj instanceof DFS_Message) return true;
				return false;
			}
			public void buildProperties(PropertyBuilder builder) {
				builder.simpleProperty("", "Token");
			}
		}
		Short t = (Short) o;
		if (t != 0) throw new Error("Invalid Haskell unit");
		return new DFS_Message();
	}
	
	protected Information.State makeAndUnloadState(GlueHelper help, Object o) {
		if (help == null || o == null) throw null;
		abstract class DFS_RRUI implements PropertyVisitor {
		}
		class DFS_State extends Information.State {
			boolean hasToken;
			DFS_RRUI rrui;
			List<Channel> neighbors;
			Channel incoming;
			public String toString() {
				return "(" + hasToken + "," + rrui + "," + neighbors + "," + incoming + ")";
			}
			public void buildProperties(PropertyBuilder builder) {
				builder.simpleProperty("Has token?", String.valueOf(hasToken));
				builder.compoundProperty("State", rrui);
				builder.simpleProperty("Reply to:", incoming == null ? "None" : incoming.to.getLabel());
				builder.compoundProperty("Neighbors", new PropertyVisitor() {
					public void buildProperties(PropertyBuilder builder) {
						builder.simpleProperty("", neighbors.size() + " elements");
						for (int i = 0, size = neighbors.size(); i < size; i++) {
							builder.simpleProperty(String.valueOf(i) + ":", neighbors.get(i).to.getLabel());
						}
					}
				});
			}
		}
		class DFS_Received extends DFS_RRUI {
			private Channel c;
			public String toString() {
				return "Received<" + c + ">";
			}
			public void buildProperties(PropertyBuilder builder) {
				builder.simpleProperty("", "Received");
				builder.simpleProperty("From:", c.to.getLabel());
			}
		}
		class DFS_Replied extends DFS_RRUI {
			private Channel c;
			public String toString() {
				return "Replied<" + c + ">";
			}
			public void buildProperties(PropertyBuilder builder) {
				builder.simpleProperty("", "Replied");
				builder.simpleProperty("To:", c.to.getLabel());
			}
		}
		class DFS_Undefined extends DFS_RRUI {
			public String toString() {
				return "Undefined";
			}
			public void buildProperties(PropertyBuilder builder) {
				builder.simpleProperty("", "Undefined");
			}
		}
		class DFS_Initiator extends DFS_RRUI {
			public String toString() {
				return "Initiator";
			}
			public void buildProperties(PropertyBuilder builder) {
				builder.simpleProperty("", "Initiator");
			}
		}
		@SuppressWarnings("unchecked")
		TTuple4<Boolean, TRRUI, TSet<TTuple2<Integer, Integer>>, TMaybe<TTuple2<Integer, Integer>>> st =
				(TTuple4<Boolean, TRRUI, TSet<TTuple2<Integer, Integer>>, TMaybe<TTuple2<Integer, Integer>>>) o;
		DFS_State result = new DFS_State();
		result.hasToken = st.mem1.call();
		TRRUI rrui = st.mem2.call();
		if (rrui.asReceived() != null) {
			DFS_Received r = new DFS_Received();
			r.c = help.getChannelByTuple(rrui.asReceived().mem1.call());
			result.rrui = r;
		} else if (rrui.asReplied() != null) {
			DFS_Replied r = new DFS_Replied();
			r.c = help.getChannelByTuple(rrui.asReplied().mem1.call());
			result.rrui = r;
		} else if (rrui.asUndefined() != null) {
			result.rrui = new DFS_Undefined();
		} else if (rrui.asInitiator() != null) {
			result.rrui = new DFS_Initiator();
		} else {
			throw new Error("Invalid RRUI value");
		}
		result.neighbors = help.forEdgeSet(st.mem3.call());
		DJust<TTuple2<Integer, Integer>> in = st.mem4.call().asJust();
		result.incoming = in == null ? null : help.getChannelByTuple(in.mem1.call());
		return result;
	}
	
	protected Result makeAndUnloadResult(GlueHelper helper, Object o) {
		class DFSTerminated extends Information.Result {
			public String toString() { return "Terminated"; }
			public void buildProperties(PropertyBuilder builder) {
				builder.simpleProperty("", "Terminated");
			}
		}
		class DFSDecided extends Information.Result {
			public String toString() { return "Decided"; }
			public void buildProperties(PropertyBuilder builder) {
				builder.simpleProperty("", "Decided");
			}
		}
		boolean result = (Boolean) o;
		if (result) {
			return new DFSTerminated();
		} else {
			return new DFSDecided();
		}
	}
	
	protected TProcessDescription<Object, Object, Object, Object> getProcessDescription(GlueHelper helper) {
		return procDesc(Thunk.lazy(helper.getIdByNode(assumption.getInitiator()))).simsalabim();
	}
	
}
