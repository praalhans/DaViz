package com.aexiz.daviz;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URI;

class AboutFrame extends JDialog {

    private static final long serialVersionUID = 7837200967453244201L;

    public AboutFrame(Window owner) {
        super(owner, ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width - 400) / 2, (screenSize.height - 200) / 2, 400, 200);
        setTitle("About DaViz");
        setResizable(false);

        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel pane = new JPanel(gbl);
        pane.setBackground(Color.WHITE);
        pane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Distributed AlgorithmUI Visualisation");
        title.setFont(title.getFont().deriveFont(24.0f));
        gbc.gridwidth = 2;
        gbl.setConstraints(title, gbc);
        pane.add(title);

        JLabel author = new JLabel("Programming by:   ");
        gbc.ipady = 20;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.gridy = 1;
        gbl.setConstraints(author, gbc);
        pane.add(author);

        JLabel author_name = new JLabel("Hans-Dieter Hiep");
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.gridx = 1;
        gbl.setConstraints(author_name, gbc);
        pane.add(author_name);

        JLabel icons = new JLabel("Icons:   ");
        gbc.ipady = 2;
        gbc.ipadx = 0;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbl.setConstraints(icons, gbc);
        pane.add(icons);

        JLabel icons_name = new JLabel("http://fatcow.com/free-icons");
        icons_name.setForeground(Color.BLUE);
        icons_name.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        icons_name.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent arg0) {
                try {
                    Desktop.getDesktop().browse(new URI("http://fatcow.com/free-icons"));
                } catch (Exception ex) {
                    getToolkit().beep();
                }
            }

            public void mouseEntered(MouseEvent arg0) {
            }

            public void mouseExited(MouseEvent arg0) {
            }

            public void mousePressed(MouseEvent arg0) {
            }

            public void mouseReleased(MouseEvent arg0) {
            }
        });
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.gridx = 1;
        gbl.setConstraints(icons_name, gbc);
        pane.add(icons_name);

        JLabel thanks = new JLabel("Special thanks to:");
        gbc = new GridBagConstraints();
        gbc.ipady = 20;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbl.setConstraints(thanks, gbc);
        pane.add(thanks);

        JLabel thanks_name = new JLabel("Wan Fokkink, Jacco van Splunter");
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.ipady = 0;
        gbc.gridy = 4;
        gbl.setConstraints(thanks_name, gbc);
        pane.add(thanks_name);

        JButton close = new JButton("OK");
        close.setOpaque(false);
        close.setPreferredSize(new Dimension(85, close.getPreferredSize().height));
        close.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weighty = 1.0;
        gbl.setConstraints(close, gbc);
        pane.add(close);

        add(pane, BorderLayout.CENTER);
    }

}
