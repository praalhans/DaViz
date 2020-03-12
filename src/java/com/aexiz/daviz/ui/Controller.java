package com.aexiz.daviz.ui;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.util.EventObject;
import java.util.HashMap;
import java.util.concurrent.Callable;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.aexiz.daviz.glue.Algorithm;
import com.aexiz.daviz.glue.Network;
import com.aexiz.daviz.glue.Simulation;
import com.aexiz.daviz.glue.Viewpoint.Node;
import com.aexiz.daviz.ui.swing.DefaultExecutionModel;
import com.aexiz.daviz.ui.swing.DefaultGraphModel;
import com.aexiz.daviz.ui.swing.DefaultInfoModel;
import com.aexiz.daviz.ui.swing.DefaultObjectSelectionModel;
import com.aexiz.daviz.ui.swing.FutureEvent;
import com.aexiz.daviz.ui.swing.InfoModel;
import com.aexiz.daviz.ui.swing.ExecutionModel.CoarseTimeEventListener;
import com.aexiz.daviz.ui.swing.ExecutionModel.EventModel;
import com.aexiz.daviz.ui.swing.ExecutionModel.MessageModel;
import com.aexiz.daviz.ui.swing.ExecutionModel.PendingMessageModel;
import com.aexiz.daviz.ui.swing.GraphModel.EdgeModel;
import com.aexiz.daviz.ui.swing.GraphModel.NodeModel;
import com.aexiz.daviz.ui.swing.InfoModel.PropertyModel;
import com.aexiz.daviz.glue.Viewpoint.Channel;

class Controller {
	
	ControlFrame control;
	NetworkFrame network;
	TimelineFrame timeline;
	InfoFrame info;
	ChoiceFrame choice;
	
	DefaultObjectSelectionModel selectionModel;
	DefaultGraphModel networkModel;
	DefaultExecutionModel timelineModel;
	DefaultInfoModel infoModel;
	DefaultListModel<FutureEvent> choiceModel;
	DefaultListSelectionModel listSelectionModel;
	
	private HashMap<String,Action> actionMap = new HashMap<>();
	
	private SimulationManager simulationManager;
	
	boolean dirty;
	String filename;
	
	Controller(ControlFrame owner) {
		control = owner;
		network = new NetworkFrame(owner);
		timeline = new TimelineFrame(owner);
		info = new InfoFrame(owner);
		choice = new ChoiceFrame(owner);
		timeline.choice = choice;
		timeline.info = info;
		choice.timeline = timeline;
		
		selectionModel = new DefaultObjectSelectionModel();
		networkModel = new DefaultGraphModel();
		networkModel.setSnapToGrid(true);
		timelineModel = new DefaultExecutionModel();
		infoModel = new DefaultInfoModel();
		choiceModel = new DefaultListModel<>();
		listSelectionModel = new DefaultListSelectionModel();
		network.graph.setModel(networkModel);
		network.graph.setSelectionModel(selectionModel);
		timeline.timeline.setModel(timelineModel);
		timeline.timeline.setSelectionModel(selectionModel);
		info.table.setModel(infoModel);
		choice.carousel.setModel(choiceModel);
		choice.carousel.setSelectionModel(listSelectionModel);
		installTimeListener();
		installSelectionListener();
		
		simulationManager = new SimulationManager(this);
		
		control.registerActions(this);
		network.registerActions(this);
		timeline.registerActions(this);
		info.registerActions(this);
		choice.registerActions(this);
	}
	
	void restoreWindows() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int spaceleft = 25, spacetop = 25;
		int posleft = (screenSize.width - 300 - spaceleft - 500 - spaceleft - 600 - spaceleft) / 2;
		int postop = (screenSize.height - 300 - spacetop - 250) / 2;
		
		info.setLocation(posleft, postop);
		info.setSize(300, 300 + spacetop + 250);
		info.setMinimumSize(new Dimension(300, 300 + spacetop + 250));
		info.setVisible(true);
		timeline.setLocation(posleft + 500 + spaceleft + 300 + spaceleft, postop);
		timeline.setSize(600, 300 + spacetop + 100);
		timeline.setMinimumSize(new Dimension(600, 300 + spacetop + 100));
		timeline.setVisible(true);
		choice.setLocation(posleft + 500 + spaceleft + 300 + spaceleft, postop + 300 + spacetop + 100 + spacetop);
		choice.setSize(600, 125);
		choice.setMinimumSize(new Dimension(600, 125));
		choice.setVisible(true);
		control.setLocation(posleft + 300 + spaceleft, postop);
		control.setSize(500, 200);
		control.setMinimumSize(new Dimension(500, 200));
		control.setVisible(true);
		network.setLocation(posleft + 300 + spaceleft, postop + 200 + spacetop);
		network.setSize(500, 350);
		network.setMinimumSize(new Dimension(500, 350));
		network.setVisible(true);
	}
	
	private JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("Window");
		menu.setMnemonic('w');
		menuBar.add(menu);
		menu.add(new JMenuItem(getAction("show-info")));
		menu.add(new JMenuItem(getAction("show-simulation")));
		menu.add(new JMenuItem(getAction("show-network")));
		menu.add(new JMenuItem(getAction("show-timeline")));
		menu.add(new JMenuItem(getAction("show-choice")));
		menu.addSeparator();
		menu.add(new JMenuItem(getAction("show-windows")));
		return menuBar;
	}
	
	void populateMenuBars() {
		JMenuBar menubar;
		menubar = createMenuBar();
		control.populateMenuBar(this, menubar);
		control.setJMenuBar(menubar);
		menubar = createMenuBar();
		network.populateMenuBar(this, menubar);
		network.setJMenuBar(menubar);
		menubar = createMenuBar();
		timeline.populateMenuBar(this, menubar);
		timeline.setJMenuBar(menubar);
		menubar = createMenuBar();
		info.populateMenuBar(this, menubar);
		info.setJMenuBar(menubar);
		menubar = createMenuBar();
		choice.populateMenuBar(this, menubar);
		choice.setJMenuBar(menubar);
	}
	
	void registerGlobalActions() {
		registerAction("show-info", new AbstractAction() {
			private static final long serialVersionUID = 345831661808747964L;
			{
				putValue(Action.NAME, "Information");
				putValue(Action.MNEMONIC_KEY, KeyEvent.VK_I);
				putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0));
			}
			public void actionPerformed(ActionEvent e) {
				info.setVisible(true);
				if (!info.isAutoRequestFocus())
					info.requestFocus();
			}
		});
		registerAction("show-simulation", new AbstractAction() {
			private static final long serialVersionUID = -6443065148322819900L;
			{
				putValue(Action.NAME, "Simulation");
				putValue(Action.MNEMONIC_KEY, KeyEvent.VK_S);
				putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0));
			}
			public void actionPerformed(ActionEvent e) {
				control.setVisible(true);
				if (!control.isAutoRequestFocus())
					control.requestFocus();
			}
		});
		registerAction("show-network", new AbstractAction() {
			private static final long serialVersionUID = -1727287039452985389L;
			{
				putValue(Action.NAME, "Network");
				putValue(Action.MNEMONIC_KEY, KeyEvent.VK_N);
				putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F10, 0));
			}
			public void actionPerformed(ActionEvent e) {
				network.setVisible(true);
				if (!network.isAutoRequestFocus())
					network.requestFocus();
			}
		});
		registerAction("show-timeline", new AbstractAction() {
			private static final long serialVersionUID = 3482120594679489667L;
			{
				putValue(Action.NAME, "Timeline");
				putValue(Action.MNEMONIC_KEY, KeyEvent.VK_T);
				putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0));
			}
			public void actionPerformed(ActionEvent e) {
				timeline.setVisible(true);
				if (!timeline.isAutoRequestFocus())
					timeline.requestFocus();
			}
		});
		registerAction("show-choice", new AbstractAction() {
			private static final long serialVersionUID = 8948578948320158195L;
			{
				putValue(Action.NAME, "Choice");
				putValue(Action.MNEMONIC_KEY, KeyEvent.VK_C);
				putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0));
				setEnabled(false);
			}
			public void actionPerformed(ActionEvent e) {
				if (choice.isVisible()) {
					choice.setVisible(true);
					if (!choice.isAutoRequestFocus())
						choice.requestFocus();
				}
			}
		});
		choice.addComponentListener(new ComponentAdapter() {
			public void componentHidden(ComponentEvent e) {
				getAction("show-choice").setEnabled(false);
			}
			public void componentShown(ComponentEvent e) {
				getAction("show-choice").setEnabled(true);
			}
		});
		registerAction("show-windows", new AbstractAction() {
			private static final long serialVersionUID = -718811887577227221L;
			{
				putValue(Action.NAME, "Restore windows");
				putValue(Action.MNEMONIC_KEY, KeyEvent.VK_R);
				putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0));
			}
			public void actionPerformed(ActionEvent e) {
				restoreWindows();
			}
		});
	}
	
	void registerAction(String name, Action action) {
		if (name == null || action == null) throw null;
		if (actionMap.containsKey(name)) throw new Error("Duplicate action key");
		actionMap.put(name, action);
	}
	
	Action getAction(String name) {
		if (!actionMap.containsKey(name)) throw new Error("Invalid action key");
		return actionMap.get(name);
	}
	
	void refreshActions() {
		control.refreshActions(this);
		network.refreshActions(this);
		timeline.refreshActions(this);
		info.refreshActions(this);
		choice.refreshActions(this);
	}
	
	boolean isSimulationLoaded() {
		return !simulationManager.fresh;
	}
	
	boolean isDirty() {
		return dirty;
	}
	
	void installFocusListeners() {
		Window[] windows = new Window[]{control, network, timeline, info, choice};
		class WindowHandler implements WindowListener, WindowFocusListener {
			public void windowGainedFocus(WindowEvent e) {
				Window active = e.getWindow();
				for (Window window : windows)
					window.setAutoRequestFocus(true);
				for (Window window : windows)
					if (active != window)
						window.setFocusableWindowState(false);
				for (Window window : windows)
					if (active != window && window.isVisible())
						window.setVisible(true);
				active.setVisible(true);
				SwingUtilities.invokeLater(() -> {
					for (Window window : windows)
						if (active != window)
							window.setFocusableWindowState(true);
					for (Window window : windows)
						window.setAutoRequestFocus(false);
				});
			}
			public void windowLostFocus(WindowEvent e) {
			}
			public void windowActivated(WindowEvent e) {
			}
			public void windowClosed(WindowEvent e) {
			}
			public void windowClosing(WindowEvent e) {
			}
			public void windowDeactivated(WindowEvent e) {
			}
			public void windowDeiconified(WindowEvent e) {
				Window active = e.getWindow();
				for (Window window : windows)
					if (active != window && window instanceof Frame)
						((Frame) window).setState(Frame.NORMAL);
			}
			public void windowIconified(WindowEvent e) {
				Window active = e.getWindow();
				for (Window window : windows)
					if (active != window && window instanceof Frame)
						((Frame) window).setState(Frame.ICONIFIED);
			}
			public void windowOpened(WindowEvent e) {
			}
		};
		WindowHandler h = new WindowHandler();
		for (Window window : windows) {
			window.setAutoRequestFocus(false);
			window.addWindowFocusListener(h);
			window.addWindowListener(h);
		}
		for (Window window : windows) {
			if (window instanceof JFrame)
				((JFrame) window).setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			else if (window instanceof JDialog)
				((JDialog) window).setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		}
	}
	
	private void installSelectionListener() {
		class SelectionHandler implements ChangeListener, Runnable {
			boolean delayed = false;
			public void stateChanged(ChangeEvent e) {
				if (!delayed) {
					// Consume all state change events in one go.
					delayed = true;
					SwingUtilities.invokeLater(this);
				}
			}
			public void run() {
				delayed = false;
				Object[] sels = selectionModel.getSelection();
				infoModel.clear();
				if (sels.length == 0) {
					PropertyModel p = infoModel.createProperty("Empty selection", null, InfoModel.COMPOUND_TYPE);
					infoModel.addProperty(p);
					return;
				} else if (sels.length >= 2) {
					Class<?> firstType = sels[0].getClass();
					boolean sameType = true;
					for (int i = 1; i < sels.length; i++) {
						if (sels[i].getClass() != firstType) {
							sameType = false;
							break;
						}
					}
					PropertyModel p;
					if (sameType) {
						p = infoModel.createProperty(sels.length + " homogeneous objects", null, InfoModel.COMPOUND_TYPE);
					} else {
						p = infoModel.createProperty(sels.length + " heterogeneous objects", null, InfoModel.COMPOUND_TYPE);
					}
					infoModel.addProperty(p);
					return;
				}
				Object sel = sels[0];
				if (sel instanceof NodeModel) {
					NodeModel node = (NodeModel) sel;
					simulationManager.changeNodeSelection(node);
				} else if (sel instanceof EdgeModel) {
					EdgeModel edge = (EdgeModel) sel;
					simulationManager.changeEdgeSelection(edge);
				} else if (sel instanceof EventModel) {
					EventModel ev = (EventModel) sel;
					simulationManager.changeEventSelection(ev);
				} else if (sel instanceof MessageModel || sel instanceof PendingMessageModel) {
					simulationManager.changeMessageSelection(sel);
				} else throw new Error("Unknown selected object");
			}
		}
		SelectionHandler h = new SelectionHandler();
		h.stateChanged(null);
		selectionModel.addChangeListener(h);
	}
	
	private void installTimeListener() {
		class TimeHandler implements ListSelectionListener, CoarseTimeEventListener {
			public void valueChanged(ListSelectionEvent e) {
				int index = listSelectionModel.getMinSelectionIndex();
				if (index < 0) return;
				FutureEvent fe = (FutureEvent) choiceModel.getElementAt(index);
				simulationManager.changeFutureEvent(fe);
			}
			public void timeChanged(EventObject o) {
				simulationManager.changeTime();
			}
		}
		TimeHandler h = new TimeHandler();
		timelineModel.addCoarseTimeEventListener(h);
		listSelectionModel.addListSelectionListener(h);
	}
	
	boolean confirmSave(String msg) {
		if (isDirty()) {
			int result = JOptionPane.showConfirmDialog(control, msg, "Unsaved changes", JOptionPane.YES_NO_CANCEL_OPTION);
			if (result == JOptionPane.YES_OPTION) {
				// Save and then proceed
				// TODO save
				return true;
			} else if (result == JOptionPane.NO_OPTION) {
				// Discard and proceed
				return true;
			} else if (result == JOptionPane.CANCEL_OPTION) {
				// Cancel operation
				return false;
			} else throw new Error();
		}
		return true;
	}
	
	void clear() {
		simulationManager.stopSimulation();
		simulationManager.afterSimulation(() -> {
			clear0();
		});
	}
	
	private void clear0() {
		networkModel.clear();
		clearSimulation();
		refreshActions();
	}
	
	void start() {
		simulationManager.afterSimulation(() -> {
			AlgorithmSelection alg = (AlgorithmSelection) control.algorithmsBox.getSelectedItem();
			Object[] init = control.initiatorBox.getValue();

			if (networkModel.isEmpty()) {
				JOptionPane.showMessageDialog(control, "The network is empty.", "Unable to start simulation", JOptionPane.ERROR_MESSAGE);
				return;
			}
			if (alg.isAcyclicGraph() && !networkModel.isAcyclic()) {
				JOptionPane.showMessageDialog(control, "The network contains cycles.", "Unable to start simulation", JOptionPane.ERROR_MESSAGE);				
				return;
			}
			if (alg.isInitiatorUser() && (init == null || init.length == 0)) {
				JOptionPane.showMessageDialog(control, "No initiator selected.", "Unable to start simulation", JOptionPane.ERROR_MESSAGE);				
				return;
			}
			if (alg.isInitiatorUser() && alg.isCentralized() && init != null && init.length > 1) {
				JOptionPane.showMessageDialog(control, "Too many initiators selected.", "Unable to start simulation", JOptionPane.ERROR_MESSAGE);				
				return;
			}
			simulationManager.loadSimulation(() -> {
				// Create a simulation
				Simulation sim = new Simulation();
				sim.setAlgorithm((Algorithm) alg.alg);
				// Load network vertices, edges and initiator
				Network network = new Network();
				NodeModel[] nodes = networkModel.getNode();
				Node[] ps = new Node[nodes.length];
				for (int i = 0; i < nodes.length; i++) {
					ps[i] = new Node(nodes[i].getLabel());
					ps[i].putClientProperty(Node.CLIENT_PROPERTY_NODEMODEL, nodes[i]);
					network.addNode(ps[i]);
				}
				EdgeModel[] edges = networkModel.getValidEdge();
				Channel[] es = new Channel[edges.length];
				for (int i = 0; i < edges.length; i++) {
					NodeModel from = edges[i].getFrom(), to = edges[i].getTo();
					Node f = null, t = null;
					for (int j = 0; (f == null || t == null) && j < nodes.length; j++) {
						if (nodes[j] == from) f = ps[j];
						if (nodes[j] == to) t = ps[j];
					}
					if (f == null || t == null) throw new Error();
					es[i] = new Channel(f , t);
					es[i].putClientProperty(Channel.CLIENT_PROPERTY_EDGEMODEL, edges[i]);
					es[i].putClientProperty(Channel.CLIENT_PROPERTY_FIRST_DIRECTED, true);
					network.addChannel(es[i]);
					if (!edges[i].isDirected()) {
						// Also add reverse edge
						Channel c = new Channel(t, f);
						c.putClientProperty(Channel.CLIENT_PROPERTY_EDGEMODEL, edges[i]);
						c.putClientProperty(Channel.CLIENT_PROPERTY_FIRST_DIRECTED, false);
						network.addChannel(c);
					}
				}
				sim.setNetwork(network);
				if (init != null) {
					Node[] is = new Node[init.length];
					for (int i = 0; i < init.length; i++) {
						for (int j = 0; is[i] == null && j < nodes.length; j++) {
							if (nodes[j] == init[i]) is[i] = ps[j];
						}
						if (is[i] == null) throw new Error();
					}
					sim.setInitiator(is[0]);
				}
				sim.load();
				return sim;
			});
		});
	}
	
	void startTestCase(TestCase testCase) {
		// Clear any existing model, because we are loading a new one
		networkModel.clear();
		control.algorithmsBox.setSelectedItem(testCase.algorithm);
		// Load the simulation as prepared by the TestCase method.
		simulationManager.performJob(new Callable<Void>() {
			public Void call() throws Exception {
				// By the action listener, the status flags are updated too
				simulationManager.loadSimulation(testCase.method);
				return null;
			}
		});
	}
	
	void clearSimulation() {
		selectionModel.clearSelection();
		listSelectionModel.clearSelection();
		timelineModel.clear();
		choiceModel.clear();
	}
	
	void stop() {
		// stopSimulation calls clearSimulation
		simulationManager.stopSimulation();
	}
	
	void performJob(Callable<Void> call) {
		// Runs the task in a separate thread
		simulationManager.performJob(call);
	}
	
	// Executes within AWT dispatch thread
	void setWaiting(boolean loading) {
		Cursor c;
		if (loading) {
			c = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
		} else {
			c = Cursor.getDefaultCursor();
		}
		control.setCursor(c);
		network.setCursor(c);
		timeline.setCursor(c);
		info.setCursor(c);
	}
	
}
