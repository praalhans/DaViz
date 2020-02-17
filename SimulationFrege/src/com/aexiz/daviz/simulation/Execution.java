package com.aexiz.daviz.simulation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.aexiz.daviz.simulation.Configuration.InitialConfiguration;
import com.aexiz.daviz.frege.simulation.Event.TEvent;
import com.aexiz.daviz.frege.simulation.Simulation.TSimulation;

import frege.prelude.PreludeBase.TList;
import frege.prelude.PreludeBase.TList.DCons;
import frege.prelude.PreludeBase.TTuple2;

public class Execution {
	
	// Properties
	Simulation simulation;
	Execution parent; // may be null for root
	Event lastEvent; // may be null for root
	
	// Transient fields
	transient Configuration configuration;
	transient List<Execution> successors;
	
	// Haskell dependencies
	transient TSimulation<Object,Object,Object> hSimulation;
	
	Execution() {
	}
	
	void loadFirst() {
		if (simulation == null) throw new Error("Invalid simulation");
		if (!(configuration instanceof InitialConfiguration)) throw new Error("Invalid initial configuration");
		GlueHelper helper = new GlueHelper(simulation);
		hSimulation = com.aexiz.daviz.frege.simulation.Simulation.simulation(
				configuration.hConfiguration,
				simulation.getAlgorithm().getProcessDescription(helper));
		parent = null;
		lastEvent = null;
		// Reload configuration from Haskell
		configuration = null;
		unloadConfiguration();
	}
	
	private void invariant() {
		if (simulation == null) throw new Error("Invalid simulation");
		if (hSimulation == null) throw new Error("No Haskell simulation");
	}
	
	private void unloadConfiguration() {
		invariant();
		if (configuration != null) return;
		// 1. Set configuration, translated back from Haskell
		configuration = new Configuration();
		configuration.simulation = simulation;
		configuration.hConfiguration = hSimulation.mem$config.call();
		configuration.unload();
	}
	
	private void unloadSuccessors() {
		invariant();
		if (successors != null) return;
		successors = new ArrayList<>();
		// 2. Check if there are any successors
		TList<TTuple2<TEvent<Object,Object,Object>,TSimulation<Object,Object,Object>>> succ =
				hSimulation.mem$successors.call();
		while (succ.asCons() != null) {
			DCons<TTuple2<TEvent<Object,Object,Object>,TSimulation<Object,Object,Object>>> head = succ.asCons();
			TTuple2<TEvent<Object,Object,Object>,TSimulation<Object,Object,Object>> tup = head.mem1.call();
			Execution result = new Execution();
			result.parent = this;
			result.simulation = simulation;
			result.hSimulation = tup.mem2.call();
			result.lastEvent = Event.makeAndUnload(tup.mem1.call(), result);
			// Not unloading configuration
			// Not unloading successors
			successors.add(result);
			succ = succ.asCons().mem2.call();
		}
	}
	
	// Traversal methods
	
	public boolean hasParent() {
		invariant();
		return parent != null;
	}
	
	public Execution getParent() {
		invariant();
		return parent;
	}
	
	public boolean hasNext() {
		unloadSuccessors();
		return successors.size() != 0;
	}
	
	public int getNextCount() {
		unloadSuccessors();
		return successors.size();
	}
	
	public Execution getNext() {
		// Always choose first successor
		return getNext(0);
	}
	
	public Execution getNext(int index) {
		unloadSuccessors();
		return successors.get(index);
	}
	
	public Execution[] getSuccessors() {
		Execution[] result = new Execution[getNextCount()];
		for (int i = 0; i < result.length; i++) {
			result[i] = getNext(i);
		}
		return result;
	}
	
	public boolean hasEvents() {
		return hasParent();
	}
	
	public Event[] getLinkedEvents() {
		ArrayList<Event> events = new ArrayList<Event>();
		// Traverse and collect
		Execution elem = this;
		while (elem.parent != null) {
			events.add(elem.lastEvent);
			elem = elem.parent;
		}
		// Reverse
		Collections.reverse(events);
		// Match and link
		Event.matchAndLinkEvents(events);
		return events.toArray(new Event[events.size()]);
	}
	
	public List<Execution> getExecutionPath() {
		ArrayList<Execution> result = new ArrayList<>();
		// Traverse and collect
		Execution elem = this;
		while (elem != null) {
			result.add(elem);
			elem = elem.parent;
		}
		// Reverse
		Collections.reverse(result);
		return result;
	}
	
	public Event getLastEvent() {
		Event[] linkedEvents = getLinkedEvents();
		return linkedEvents[linkedEvents.length - 1];
	}
	
	// Property methods
	
	public Configuration getConfiguration() {
		unloadConfiguration();
		return configuration;
	}
	
}
