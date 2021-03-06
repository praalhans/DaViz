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
import com.aexiz.daviz.sim.Awerbuch.TMS;
import com.aexiz.daviz.sim.Awerbuch.TPS;
import com.aexiz.daviz.sim.Awerbuch.TRRRUII;
import static com.aexiz.daviz.sim.Awerbuch.procDesc;

import com.aexiz.daviz.sim.Process.TProcessDescription;

import frege.prelude.PreludeBase.TMaybe.DJust;
import frege.prelude.PreludeBase.TTuple2;
import frege.run8.Thunk;

public class Awerbuch extends Algorithm {
	
	public Awerbuch() {
		assumption = new Assumption() {
			{
				centralized_user = true;
			}
		};
	}
	
	protected Information.Message makeAndUnloadMessage(GlueHelper help, Object o) {
		if (help == null || o == null) throw null;
		abstract class AwerbuchMessage extends Information.Message {
		}
		class AwerbuchToken extends AwerbuchMessage {
			public String toString() {
				return "*token*";
			}
			public boolean equals(Object obj) {
				if (obj instanceof AwerbuchToken) return true;
				return false;
			}
			public void buildProperties(PropertyBuilder builder) {
				builder.simpleProperty("", "Token");
			}
		}
		class AwerbuchInfo extends AwerbuchMessage {
			public String toString() {
				return "*info*";
			}
			public boolean equals(Object obj) {
				if (obj instanceof AwerbuchInfo) return true;
				return false;
			}
			public void buildProperties(PropertyBuilder builder) {
				builder.simpleProperty("", "Info");
			}
		}
		class AwerbuchAck extends AwerbuchMessage {
			public String toString() {
				return "*ack*";
			}
			public boolean equals(Object obj) {
				if (obj instanceof AwerbuchAck) return true;
				return false;
			}
			public void buildProperties(PropertyBuilder builder) {
				builder.simpleProperty("", "Ack");
			}
		}
		Short t = (Short) o;
		if (t == TMS.Token) {
			return new AwerbuchToken();
		} else if (t == TMS.Inf) {
			return new AwerbuchInfo();
		} else if (t == TMS.Ack) {
			return new AwerbuchAck();
		} else throw new Error("Unknown message");
	}
	
	protected Information.State makeAndUnloadState(GlueHelper help, Object o) {
		if (help == null || o == null) throw null;
		abstract class AwerbuchRRRUII implements PropertyVisitor {
		}
		class AwerbuchState extends Information.State {
			boolean hasToken;
			AwerbuchRRRUII rrruii;
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
							builder.simpleProperty(String.valueOf(i) + ":", inform.get(i).to.getLabel());
						}
					}
				});
				builder.compoundProperty("Waiting for", new PropertyVisitor() {
					public void buildProperties(PropertyBuilder builder) {
						builder.simpleProperty("", acked.size() + " elements");
						for (int i = 0, size = acked.size(); i < size; i++) {
							builder.simpleProperty(String.valueOf(i) + ":", acked.get(i).to.getLabel());
						}
					}
				});
				builder.simpleProperty("Token to:", intended == null ? "None" : intended.to.getLabel());
				builder.compoundProperty("Candidates", new PropertyVisitor() {
					public void buildProperties(PropertyBuilder builder) {
						builder.simpleProperty("", forward.size() + " elements");
						for (int i = 0, size = forward.size(); i < size; i++) {
							builder.simpleProperty(String.valueOf(i) + ":", forward.get(i).to.getLabel());
						}
					}
				});
				builder.compoundProperty("Neighbors", new PropertyVisitor() {
					public void buildProperties(PropertyBuilder builder) {
						builder.simpleProperty("", info.size() + " elements");
						for (int i = 0, size = info.size(); i < size; i++) {
							builder.simpleProperty(String.valueOf(i) + ":", info.get(i).to.getLabel());
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
		class AwerbuchReplied extends AwerbuchRRRUII {
			private Channel c;
			public String toString() {
				return "Replied<" + c + ">";
			}
			public void buildProperties(PropertyBuilder builder) {
				builder.simpleProperty("", "Replied");
				builder.simpleProperty("To:", c.to.getLabel());
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
			r.c = help.getChannelByTuple(rrruii.asReceivedSeen().mem1.call());
			result.rrruii = r;
		} else if (rrruii.asReceivedUnseen() != null) {
			AwerbuchReceivedUnseen r = new AwerbuchReceivedUnseen();
			r.c = help.getChannelByTuple(rrruii.asReceivedUnseen().mem1.call());
			result.rrruii = r;
		} else if (rrruii.asReplied() != null) {
			AwerbuchReplied r = new AwerbuchReplied();
			r.c = help.getChannelByTuple(rrruii.asReplied().mem1.call());
			result.rrruii = r;
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
		result.inform = help.forEdgeSet(st.mem$inform.call());
		result.acked = help.forEdgeSet(st.mem$acked.call());
		in = st.mem$intended.call().asJust();
		result.intended = in == null ? null : help.getChannelByTuple(in.mem1.call());
		result.forward = help.forEdgeSet(st.mem$forward.call());
		result.info = help.forEdgeSet(st.mem$info.call());
		in = st.mem$last.call().asJust();
		result.last = in == null ? null : help.getChannelByTuple(in.mem1.call());
		in = st.mem$toAck.call().asJust();
		result.toAck = in == null ? null : help.getChannelByTuple(in.mem1.call());
		return result;
	}
	
	protected Result makeAndUnloadResult(GlueHelper helper, Object o) {
		class AwerbuchTerminated extends Information.Result {
			public String toString() { return "Terminated"; }
			public void buildProperties(PropertyBuilder builder) {
				builder.simpleProperty("", "Terminated");
			}
		}
		class AwerbuchDecided extends Information.Result {
			public String toString() { return "Decided"; }
			public void buildProperties(PropertyBuilder builder) {
				builder.simpleProperty("", "Decided");
			}
		}
		boolean result = (Boolean) o;
		if (result) {
			return new AwerbuchTerminated();
		} else {
			return new AwerbuchDecided();
		}
	}
	
	protected TProcessDescription<Object, Object, Object, Object> getProcessDescription(GlueHelper helper) {
		return procDesc(Thunk.lazy(helper.getIdByNode(assumption.getInitiator()))).simsalabim();
	}
	
}
