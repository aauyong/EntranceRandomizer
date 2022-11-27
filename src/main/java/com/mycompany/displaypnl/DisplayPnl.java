package com.mycompany.displaypnl;

import java.util.List;

import javax.swing.JPanel;

/**
 * A JPanel for displayign various tracker information.
 * 
 * Contains a {@code EvntTbl} that tracks all the events that occur in the 
 * map tracker, such as connections and deletions. 
 * 
 * It also contains buttons and drop downs for turning on or off various settings,
 * such as hiding useless markers and the type of grouping such as Simple or 
 * Restricted.
 * @author aauyong
 */
public class DisplayPnl extends JPanel {
    /**
     * Constructor
     * 
     * Initializes components and the layout
     */
    public DisplayPnl() {
        super();
        initComponents();
        initLayout();
    }

    /**
     * Calls the {@code EvntTbl}'s {@code addEvent} with the parameters
     * @param evntType Type of event to add
     * @param entr Name of entrance 
     * @param ext Name of exit
     */
    public void addEvent(String evntType, String entr, String ext) {
        evntLogTbl.addEvent(evntType, entr, ext);
    }

    /**
     * @return {@code evntLog}'s table model
     */
    public javax.swing.table.TableModel getTblModel() {
        return this.evntLogTbl.getModel();
    }

    /**
     * Call {@code evntLog}'s {@code getEvents}
     * @return List of String[] containing the events
     * 
     * @see com.mycompany.displaypnl.EvntTbl
     */
    public List<String[]> getEvents() {
        return evntLogTbl.getEvents();
    }

    /**
     * Retreives the row indicated by {@code rowIdx}
     * 
     * {@code EventTbl} stores items from recent to latest, therefore index 0
     * refers to the newest item. So this is used to access items
     * @param rowIdx
     * @return
     */
    public int getRow(int rowIdx) {
        return evntLogTbl.getModel().getRowCount() - rowIdx;
    }

    /** Accesser to the {@code this.toggleUseless} button*/
    public javax.swing.JToggleButton getUselessButton() {
        return this.toggleUseless;
    }

    private final void initComponents() {
        this.evntLogTbl = new EvntTbl();

        this.evntScrll = new javax.swing.JScrollPane();
        this.evntScrll.setHorizontalScrollBarPolicy(
            javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );

        this.evntScrll.setViewportView(evntLogTbl);
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
    private EvntTbl evntLogTbl;
    private javax.swing.JScrollPane evntScrll;

    /** A toggle for hiding useless events */
    private javax.swing.JToggleButton toggleUseless;
}
