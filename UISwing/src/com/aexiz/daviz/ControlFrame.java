package com.aexiz.daviz;

import com.aexiz.daviz.images.ImageRoot;
import com.aexiz.daviz.ui.GraphModel.NodeModel;
import com.aexiz.daviz.ui.JAssignmentField;
import com.aexiz.daviz.ui.JCoolBar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Objects;

public class ControlFrame extends JFrame {

    private static final long serialVersionUID = 6858557427390573562L;
    AboutFrame about;
    Controller controller;
    Handler handler;
    JCoolBar toolbar;
    JPanel pane;
    JComboBox<Algorithms> algorithmsBox;
    JLabel assumptionAcyclic;
    JLabel assumptionCentralized;
    JLabel assumptionDecentralized;
    JAssignmentField initiatorBox;
    JMenu testCaseMenu;
    TestCases[] testCases;
    JMenuItem[] testCaseButtons;

    ControlFrame() {
        ArrayList<Image> icons = new ArrayList<>();
        icons.add(new ImageIcon(ImageRoot.class.getResource("d16/multitool.png")).getImage());
        icons.add(new ImageIcon(ImageRoot.class.getResource("d32/multitool.png")).getImage());
        setIconImages(icons);
        setResizable(false);

        about = new AboutFrame(this);

        handler = new Handler();

        JPanel topPane = new JPanel(new BorderLayout());
        toolbar = new JCoolBar();
        topPane.add(toolbar, BorderLayout.CENTER);
        topPane.add(new JSeparator(), BorderLayout.PAGE_END);

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
        algorithmsBox.addActionListener(e -> {
            Object selection = algorithmsBox.getSelectedItem();
            Algorithms alg = (Algorithms) selection;
            assumptionAcyclic.setEnabled(alg.isAcyclicGraph());
            assumptionCentralized.setEnabled(alg.isCentralized());
            assumptionDecentralized.setEnabled(alg.isDecentralized());
            initiatorBox.setEnabled(alg.isInitiatorUser());
        });
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
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

        Container contentPane = getContentPane();
        contentPane.add(topPane, BorderLayout.PAGE_START);
        contentPane.add(pane, BorderLayout.CENTER);
        pane.requestFocusInWindow();

        controller = new Controller(this);
        controller.registerGlobalActions();
        controller.populateMenuBars();
        controller.installFocusListeners();
        controller.restoreWindows();
        controller.refreshActions();
        initiatorBox.setSelectionModel(controller.selectionModel);
        loadAlgorithms();
        updateTitle();
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                new ControlFrame();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    void updateTitle() {
        String title = "DaViz - ";
        title += Objects.requireNonNullElse(controller.filename, "Untitled");
        if (controller.isDirty()) {
            title += "*";
        }
        setTitle(title);
    }

    void loadAlgorithms() {
		/*controller.simulationManager.performJob(new Callable<Void>() {
			public Void call() throws Exception {
				// Loading the TestCases class also pulls in the Haskell compiled classes
				testCases = TestCases.getTestCases();
				SwingUtilities.invokeAndWait(() -> {
					testCaseButtons = new JMenuItem[testCases.length];
					for (int i = 0; i < testCases.length; i++) {
						testCaseButtons[i] = new JMenuItem(testCases[i].getPage() + " (" + testCases[i].getName() + ")");
						testCaseButtons[i].setActionCommand("load");
						testCaseButtons[i].addActionListener(handler);
						testCaseMenu.add(testCaseButtons[i]);
					}
					testCaseMenu.revalidate();
				});
				return null;
			}
		});*/
        controller.simulationManager.performJob(() -> {
            // Loading the Algorithms also pulls in the Haskell compiled classes
            Algorithms[] algorithms = Algorithms.getAlgorithms();
            SwingUtilities.invokeAndWait(() -> {
                for (Algorithms alg : algorithms) {
                    algorithmsBox.addItem(alg);
                }
            });
            return null;
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
                controller.simulationManager.stopSimulation();
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
		/*testCaseMenu = new JMenu("Tests");
		testCaseMenu.setMnemonic('t');
		menubar.add(testCaseMenu, 0);*/
        // Test case menu is populated asynchronously

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
        mb = new JMenuItem(controller.getAction("help-about"));
        mb.setToolTipText(null);
        menu.add(mb);
        menubar.add(menu);
    }

    class Handler implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            controller.simulationManager.performJob(() -> {
                for (int i = 0; i < testCases.length; i++) {
                    if (e.getSource() == testCaseButtons[i]) {
                        controller.simulationManager.loadSimulation(testCases[i].method);
                        break;
                    }
                }
                return null;
            });
        }

    }

}
