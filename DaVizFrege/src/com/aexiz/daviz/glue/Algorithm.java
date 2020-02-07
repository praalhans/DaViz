package com.aexiz.daviz.glue;

import com.aexiz.daviz.sim.Process.TProcessDescription;

public abstract class Algorithm {
	
	public interface MaxRounds {
		int maxRounds(Network network);
	}
	
	protected Assumption assumption;
	
	// General property before simulation
	
	public Assumption getAssumption() {
		return assumption;
	}
	
	public MaxRounds getMaxRounds() {
		return null;
	}
	
	// Unloading information from simulation
	
	protected abstract Information.Message makeAndUnloadMessage(GlueHelper helper, Object o);
	
	protected abstract Information.State makeAndUnloadState(GlueHelper helper, Object o);
	
	protected abstract Information.Result makeAndUnloadResult(GlueHelper helper, Object o);
	
	// Loading information into simulation
	
	protected abstract TProcessDescription<Object, Object, Object, Object> getProcessDescription(GlueHelper helper);
	
}
