package com.aexiz.daviz.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.net.URI;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.WindowConstants;

import com.aexiz.daviz.images.ImageRoot;
import com.aexiz.daviz.ui.swing.JAssignmentField;
import com.aexiz.daviz.ui.swing.JCoolBar;
import com.aexiz.daviz.ui.swing.GraphModel.NodeModel;

public class ControlFrame extends JFrame {
	
	private static final long serialVersionUID = 6858557427390573562L;
	
	private static final boolean SHOW_TESTCASE_MENU = true;

	AboutFrame about;
	Controller controller;
	Handler handler;
	
	JCoolBar toolbar;
	JPanel pane;
	JComboBox<AlgorithmSelection> algorithmsBox;
	JLabel assumptionAcyclic;
	JLabel assumptionCentralized;
	JLabel assumptionDecentralized;
	JAssignmentField initiatorBox;
	
	JMenu testCaseMenu;
	JMenuItem[] testCaseButtons;
	
	public static void launch() {
		new ControlFrame();
	}
	
	ControlFrame() {
		ArrayList<Image> icons = new ArrayList<Image>();
		icons.add(new ImageIcon(ImageRoot.class.getResource("d16/multitool.png")).getImage());
		icons.add(new ImageIcon(ImageRoot.class.getResource("d32/multitool.png")).getImage());
		setIconImages(icons);
		
		about = new AboutFrame(this);
		
		handler = new Handler();
		
		JPanel topPane = new JPanel(new BorderLayout());
		toolbar = new JCoolBar();
		topPane.add(toolbar, BorderLayout.CENTER);
		
		pane = new JPanel();
		GridBagLayout gbl = new GridBagLayout();
		pane.setLayout(gbl);
		pane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		GridBagConstraints gbc = new GridBagConstraints();
		JLabel label = new JLabel("Algorithm:");
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbl.setConstraints(label, gbc);
		pane.add(label);
		algorithmsBox = new JComboBox<>();
		algorithmsBox.setOpaque(false);
		algorithmsBox.setBorder(null);
		algorithmsBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				Object selection = algorithmsBox.getSelectedItem();
				AlgorithmSelection alg = (AlgorithmSelection) selection;
				assumptionAcyclic.setEnabled(alg.isAcyclicGraph());
				assumptionCentralized.setEnabled(alg.isCentralized());
				assumptionDecentralized.setEnabled(alg.isDecentralized());
				initiatorBox.setEnabled(alg.isInitiatorUser());
			}
		});
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbl.setConstraints(algorithmsBox, gbc);
		pane.add(algorithmsBox);
		
		label = new JLabel("Assumptions:");
		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbl.setConstraints(label, gbc);
		pane.add(label);
		
		JPanel assPanel = new JPanel(new FlowLayout());
		label = assumptionAcyclic = new JLabel("Acyclic");
		label.setEnabled(false);
		assPanel.add(label);
		label = assumptionCentralized = new JLabel("Centralized");
		label.setEnabled(false);
		assPanel.add(label);
		label = assumptionDecentralized = new JLabel("Decentralized");
		label.setEnabled(false);
		assPanel.add(label);
		
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbl.setConstraints(assPanel, gbc);
		pane.add(assPanel);
		
		label = new JLabel("Initiators:");
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbl.setConstraints(label, gbc);
		pane.add(label);
		
		initiatorBox = new JAssignmentField() {
			private static final long serialVersionUID = 2140481562657730772L;
			protected void filterValue() {
				ArrayList<Object> result = new ArrayList<>();
				for (Object o : value) {
					if (o instanceof NodeModel) {
						result.add(o);
					}
				}
				value = result.toArray();
			}
		};
		gbc.gridx = 1;
		gbl.setConstraints(initiatorBox, gbc);
		pane.add(initiatorBox);
		
		JPanel fillPanel = new JPanel();
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 2;
		gbc.weighty = 1.0f;
		pane.add(fillPanel);
		
		Container contentPane = getContentPane();
		contentPane.add(topPane, BorderLayout.PAGE_START);
		contentPane.add(pane, BorderLayout.CENTER);
		pane.requestFocusInWindow();
		
		controller = new Controller(this);
		controller.registerGlobalActions();
		controller.populateMenuBars();
		controller.installFocusListeners();
		controller.refreshActions();
		initiatorBox.setSelectionModel(controller.selectionModel);
		loadAlgorithms();
		updateTitle();
		
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setVisible(true);
		
		Timer showWindowTimer = new Timer(10, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.restoreWindows();
				controller.control.requestFocus();
			}
		});
		showWindowTimer.setRepeats(false);
		showWindowTimer.start();
	}
	
	void updateTitle() {
		String title = "DaViz - ";
		if (controller.filename != null) {
			title += controller.filename;
		} else {
			title += "Untitled";
		}
		if (controller.isDirty()) {
			title += "*";
		}
		setTitle(title);
	}
	
	void loadAlgorithms() {
		if (SHOW_TESTCASE_MENU) {
			controller.performJob(new Callable<Void>() {
				public Void call() throws Exception {
					// Loading the TestCases class also pulls in the Haskell compiled classes
					final TestCase[] testCases = TestCase.getTestCases();
					SwingUtilities.invokeAndWait(() -> {
						testCaseButtons = new JMenuItem[testCases.length];
						for (int i = 0; i < testCases.length; i++) {
							testCaseButtons[i] = new JMenuItem(testCases[i].getPage() + " (" + testCases[i].getName() + ")");
							testCaseButtons[i].setActionCommand("load");
							testCaseButtons[i].putClientProperty("TestCase", testCases[i]);
							testCaseButtons[i].addActionListener(handler);
							testCaseMenu.add(testCaseButtons[i]);
						}
						testCaseMenu.revalidate();
					});
					return null;
				}
			});
		}
		controller.performJob(new Callable<Void>() {
			public Void call() throws Exception {
				// Loading the Algorithms also pulls in the Haskell compiled classes
				final AlgorithmSelection[] algorithms = AlgorithmSelection.getAlgorithms();
				SwingUtilities.invokeAndWait(() -> {
					for (AlgorithmSelection alg : algorithms) {
						algorithmsBox.addItem(alg);
					}
				});
				return null;
			}
		});
	}
	
	// Called by Controller to create actions
	void registerActions(Controller controller) {
		controller.registerAction("control-start", new AbstractAction() {
			private static final long serialVersionUID = -7423205754550925733L;
			{
				putValue(Action.NAME, "Start simulation");
				putValue(Action.SHORT_DESCRIPTION, "Start simulation");
				putValue(Action.MNEMONIC_KEY, KeyEvent.VK_S);
				putValue(Action.LARGE_ICON_KEY, new ImageIcon(ImageRoot.class.getResource("d32/record_slide_show.png")));
				putValue(Action.SMALL_ICON, new ImageIcon(ImageRoot.class.getResource("d16/record_slide_show.png")));
			}
			public void actionPerformed(ActionEvent e) {
				controller.start();
			}
		});
		controller.registerAction("control-reset", new AbstractAction() {
			private static final long serialVersionUID = -7423205754550925733L;
			{
				putValue(Action.NAME, "Reset simulation");
				putValue(Action.SHORT_DESCRIPTION, "Reset simulation");
				putValue(Action.MNEMONIC_KEY, KeyEvent.VK_R);
				putValue(Action.LARGE_ICON_KEY, new ImageIcon(ImageRoot.class.getResource("d32/clock_history_frame.png")));
				putValue(Action.SMALL_ICON, new ImageIcon(ImageRoot.class.getResource("d16/clock_history_frame.png")));
			}
			public void actionPerformed(ActionEvent e) {
				controller.stop();
			}
		});
		controller.registerAction("new-scenario", new AbstractAction() {
			private static final long serialVersionUID = -7423205754550925733L;
			{
				putValue(Action.NAME, "New scenario");
				putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
				putValue(Action.SHORT_DESCRIPTION, "New scenario");
				putValue(Action.MNEMONIC_KEY, KeyEvent.VK_N);
			}
			public void actionPerformed(ActionEvent e) {
				if (controller.confirmSave("Unsaved changes get lost if you start from scratch.\nDo you want to save the current scenario?")) {
					controller.clear();
				}
			}
		});
		controller.registerAction("load-scenario", new AbstractAction() {
			private static final long serialVersionUID = -7423205754550925733L;
			{
				putValue(Action.NAME, "Open scenario...");
				putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
				putValue(Action.SHORT_DESCRIPTION, "Open scenario");
				putValue(Action.MNEMONIC_KEY, KeyEvent.VK_O);
			}
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		controller.registerAction("save-scenario", new AbstractAction() {
			private static final long serialVersionUID = -7423205754550925733L;
			{
				putValue(Action.NAME, "Save scenario");
				putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
				putValue(Action.SHORT_DESCRIPTION, "Save scenario");
				putValue(Action.MNEMONIC_KEY, KeyEvent.VK_S);
			}
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		controller.registerAction("save-as-scenario", new AbstractAction() {
			private static final long serialVersionUID = -7423205754550925733L;
			{
				putValue(Action.NAME, "Save as...");
				putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
				putValue(Action.SHORT_DESCRIPTION, "Save scenario");
				putValue(Action.MNEMONIC_KEY, KeyEvent.VK_A);
			}
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		controller.registerAction("exit", new AbstractAction() {
			private static final long serialVersionUID = -7423205754550925733L;
			{
				putValue(Action.NAME, "Exit");
				putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
				putValue(Action.MNEMONIC_KEY, KeyEvent.VK_X);
			}
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		controller.registerAction("help-contents", new AbstractAction() {
			private static final long serialVersionUID = 345831661808747964L;
			{
				putValue(Action.NAME, "Contents");
				putValue(Action.MNEMONIC_KEY, KeyEvent.VK_C);
				putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
			}
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().browse(new URI("https://github.com/praalhans/DaViz/wiki"));
				} catch (Exception ex) {
					getToolkit().beep();
				}
			}
		});
		controller.registerAction("help-about", new AbstractAction() {
			private static final long serialVersionUID = 345831661808747964L;
			{
				putValue(Action.NAME, "About");
				putValue(Action.MNEMONIC_KEY, KeyEvent.VK_A);
			}
			public void actionPerformed(ActionEvent e) {
				about.setVisible(true);
			}
		});
	}
	
	// Called  by Controller to refresh registered actions
	void refreshActions(Controller controller) {
		if (controller.isDirty()) {
			controller.getAction("save-as-scenario").setEnabled(true);
			controller.getAction("save-scenario").setEnabled(true);
		} else {
			controller.getAction("save-as-scenario").setEnabled(false);
			controller.getAction("save-scenario").setEnabled(false);
		}
		updateTitle();
		if (controller.isSimulationLoaded()) {
			controller.getAction("control-start").setEnabled(false);
			controller.getAction("control-reset").setEnabled(true);
			algorithmsBox.setEnabled(false);
			initiatorBox.setEnabled(false);
			pane.setEnabled(false);
		} else {
			controller.getAction("control-start").setEnabled(true);
			controller.getAction("control-reset").setEnabled(false);
			algorithmsBox.setEnabled(true);
			initiatorBox.setEnabled(true);
			pane.setEnabled(true);
		}
	}
	
	void populateMenuBar(Controller controller, JMenuBar menubar) {
		if (SHOW_TESTCASE_MENU) {
			testCaseMenu = new JMenu("Book");
			testCaseMenu.setMnemonic('b');
			menubar.add(testCaseMenu, 0);
			// Test case menu is populated asynchronously
		}
		
		JMenu menu = new JMenu("Simulation");
		menu.setMnemonic('s');
		JMenuItem mb = new JMenuItem(controller.getAction("control-start"));
		mb.setToolTipText(null);
		AbstractButton tb = new JButton(controller.getAction("control-start"));
		tb.setHideActionText(true);
		toolbar.add(tb);
		menu.add(mb);
		mb = new JMenuItem(controller.getAction("control-reset"));
		mb.setToolTipText(null);
		tb = new JButton(controller.getAction("control-reset"));
		tb.setHideActionText(true);
		toolbar.add(tb);
		menu.add(mb);
		menubar.add(menu, 0);
		
		menu = new JMenu("File");
		menu.setMnemonic('f');
		mb = new JMenuItem(controller.getAction("new-scenario"));
		mb.setToolTipText(null);
		menu.add(mb);
		// TODO: saving and loading scenarios is not yet supported
		/*mb = new JMenuItem(controller.getAction("load-scenario"));
		mb.setToolTipText(null);
		menu.add(mb);
		mb = new JMenuItem(controller.getAction("save-scenario"));
		mb.setToolTipText(null);
		menu.add(mb);
		mb = new JMenuItem(controller.getAction("save-as-scenario"));
		mb.setToolTipText(null);
		menu.add(mb);*/
		menu.addSeparator();
		mb = new JMenuItem(controller.getAction("exit"));
		mb.setToolTipText(null);
		menu.add(mb);
		menubar.add(menu, 0);
		
		menu = new JMenu("Help");
		menu.setMnemonic('h');
		mb = new JMenuItem(controller.getAction("help-contents"));
		mb.setToolTipText(null);
		menu.add(mb);
		menu.addSeparator();
		mb = new JMenuItem(controller.getAction("help-about"));
		mb.setToolTipText(null);
		menu.add(mb);
		menubar.add(menu);
	}
	
	class Handler implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			for (int i = 0; i < testCaseButtons.length; i++) {
				if (e.getSource() == testCaseButtons[i]) {
					TestCase test = (TestCase) testCaseButtons[i].getClientProperty("TestCase");
					controller.startTestCase(test);
				}
			}
		}
		
	}
	
}
