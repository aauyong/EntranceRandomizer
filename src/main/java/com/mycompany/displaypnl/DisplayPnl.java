package com.mycompany.displaypnl;

import java.util.List;

import javax.swing.JPanel;

public class DisplayPnl extends JPanel {
    public DisplayPnl() {
        super();
        initComponents();
        initLayout();
    }

    public void addEvent(String evnt, String entr, String ext) {
        evntLog.addEvent(evnt, entr, ext);
    }

    public javax.swing.table.TableModel getTblModel() {
        return this.evntLog.getModel();
    }

    public List<String[]> getEvents() {
        return evntLog.getEvents();
    }

    public int getAlteredRowNum(int row) {
        return evntLog.getModel().getRowCount() - row;
    }

    public javax.swing.JToggleButton getUselessButton() {
        return this.toggleUseless;
    }

    private final void initComponents() {
        this.evntLog = new EvntTbl();

        this.evntScrll = new javax.swing.JScrollPane();
        this.evntScrll.setHorizontalScrollBarPolicy(
            javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );

        this.evntScrll.setViewportView(evntLog);
        this.evntScrll.setPreferredSize( new java.awt.Dimension(550,200));

        this.toggleUseless = new javax.swing.JToggleButton();
        this.toggleUseless.setText("Hide Useless");
    }

    private final void initLayout() {
        setLayout(new java.awt.GridBagLayout());

        add(this.evntScrll, new java.awt.GridBagConstraints(){{
            anchor = java.awt.GridBagConstraints.NORTH;
            weightx = 1;
            weighty = 1;
        }});

        add(this.toggleUseless, new java.awt.GridBagConstraints() {{
            gridx = 1;
        }});
    }

    // private JTextArea evntLog;
    private EvntTbl evntLog;
    private javax.swing.JScrollPane evntScrll;

    /** A toggle for hiding useless events */
    private javax.swing.JToggleButton toggleUseless;
}
