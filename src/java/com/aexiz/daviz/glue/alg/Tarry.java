package com.aexiz.daviz.glue.alg;

import java.util.List;

import com.aexiz.daviz.glue.Algorithm;
import com.aexiz.daviz.glue.Assumption;
import com.aexiz.daviz.glue.Viewpoint.*;
import com.aexiz.daviz.glue.GlueHelper;
import com.aexiz.daviz.glue.Information;
import com.aexiz.daviz.glue.Information.PropertyBuilder;
import com.aexiz.daviz.glue.Information.PropertyVisitor;
import com.aexiz.daviz.glue.Information.Result;
import com.aexiz.daviz.sim.Process.TProcessDescription;
import com.aexiz.daviz.sim.Set.TSet;
import com.aexiz.daviz.sim.Tarry.TDUI;
import static com.aexiz.daviz.sim.Tarry.procDesc;

import frege.prelude.PreludeBase.TTuple2;
import frege.prelude.PreludeBase.TTuple3;
import frege.run8.Thunk;

public class Tarry extends Algorithm {
	
	public Tarry() {
		assumption = new Assumption() {
			{
				centralized_user = true;
			}
		};
	}
	
	protected Information.Message makeAndUnloadMessage(GlueHelper help, Object o) {
		if (help == null || o == null) throw null;
		class TarryMessage extends Information.Message {
			public String toString() {
				return "*token*";
			}
			public boolean equals(Object obj) {
				if (obj instanceof TarryMessage) return true;
				return false;
			}
			public void buildProperties(PropertyBuilder visitor) {
				visitor.simpleProperty("", "Token");
			}
		}
		Short t = (Short) o;
		if (t != 0) throw new Error("Invalid Haskell unit");
		return new TarryMessage();
	}
	
	protected Information.State makeAndUnloadState(GlueHelper help, Object o) {
		if (help == null || o == null) throw null;
		abstract class TarryDUI implements PropertyVisitor {
		}
		class TarryState extends Information.State {
			boolean hasToken;
			TarryDUI dui;
			List<Channel> neighbors;
			public String toString() {
				return "(" + hasToken + "," + dui + "," + neighbors + ")";
			}
			public void buildProperties(PropertyBuilder builder) {
				builder.simpleProperty("Has token?", String.valueOf(hasToken));
				builder.compoundProperty("State", dui);
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
		class TarryReceived extends TarryDUI {
			private Channel c;
			public String toString() {
				return "Received<" + c + ">";
			}
			public void buildProperties(PropertyBuilder builder) {
				builder.simpleProperty("", "Received");
				builder.simpleProperty("From:", c.to.getLabel());
			}
		}
		class TarryReplied extends TarryDUI {
			private Channel c;
			public String toString() {
				return "Replied<" + c + ">";
			}
			public void buildProperties(PropertyBuilder builder) {
				builder.simpleProperty("", "Replied");
				builder.simpleProperty("To:", c.to.getLabel());
			}
		}
		class TarryUndefined extends TarryDUI {
			public String toString() {
				return "Undefined";
			}
			public void buildProperties(PropertyBuilder builder) {
				builder.simpleProperty("", "Undefined");
			}
		}
		class TarryInitiator extends TarryDUI {
			public String toString() {
				return "Initiator";
			}
			public void buildProperties(PropertyBuilder builder) {
				builder.simpleProperty("", "Initiator");
			}
		}
		@SuppressWarnings("unchecked")
		TTuple3<Boolean, TDUI, TSet<TTuple2<Integer, Integer>>> st =
				(TTuple3<Boolean, TDUI, TSet<TTuple2<Integer, Integer>>>) o;
		TarryState result = new TarryState();
		result.hasToken = st.mem1.call();
		TDUI dui = st.mem2.call();
		if (dui.asReceived() != null) {
			TarryReceived r = new TarryReceived();
			r.c = help.getChannelByTuple(dui.asReceived().mem1.call());
			result.dui = r;
		} else if (dui.asReplied() != null) {
			TarryReplied r = new TarryReplied();
			r.c = help.getChannelByTuple(dui.asReplied().mem1.call());
			result.dui = r;
		} else if (dui.asUndefined() != null) {
			result.dui = new TarryUndefined();
		} else if (dui.asInitiator() != null) {
			result.dui = new TarryInitiator();
		} else {
			throw new Error("Invalid DUI value");
		}
		result.neighbors = help.forEdgeSet(st.mem3.call());
		return result;
	}
	
	protected Result makeAndUnloadResult(GlueHelper helper, Object o) {
		class TarryTerminated extends Information.Result {
			public String toString() { return "Terminated"; }
			public void buildProperties(PropertyBuilder builder) {
				builder.simpleProperty("", "Terminated");
			}
		}
		class TarryDecided extends Information.Result {
			public String toString() { return "Decided"; }
			public void buildProperties(PropertyBuilder builder) {
				builder.simpleProperty("", "Decided");
			}
		}
		boolean result = (Boolean) o;
		if (result) {
			return new TarryDecided();
		} else {
			return new TarryTerminated();
		}
	}
	
	protected TProcessDescription<Object, Object, Object, Object> getProcessDescription(GlueHelper helper) {
		return procDesc(Thunk.lazy(helper.getIdByNode(assumption.getInitiator()))).simsalabim();
	}
	
}
