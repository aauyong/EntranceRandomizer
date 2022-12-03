package com.mycompany.displaypnl;

import com.mycompany.entrancerando.SettingsHandler;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

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
     * refers to the newest item.
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

    /**
     * Gets all the components that control settings
     * @return
     */
    public Map<String, javax.swing.JComponent> getSettings() {
        var m = new HashMap<String, javax.swing.JComponent>();
        m.put("useless", this.toggleUseless);
        m.put("grouping", this.groupSettings);

        return m;
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

        this.groupSettings = new javax.swing.JComboBox<>();

        this.groupSettings.setModel(
            new javax.swing.DefaultComboBoxModel<>(
                new String[] {
                    SettingsHandler.Grouping.SIMPLE.asString(),
                    SettingsHandler.Grouping.RESTRICTED.asString(),
                    SettingsHandler.Grouping.FULL.asString(),
                    SettingsHandler.Grouping.CROSSED.asString(),
                    SettingsHandler.Grouping.INSANITY.asString(),
                }
            )
        );

    }

    private final void initLayout() {
        setLayout(new java.awt.GridBagLayout());

        add(this.evntScrll, new java.awt.GridBagConstraints(){{
            anchor = java.awt.GridBagConstraints.NORTH;
            gridheight = 2;
            weightx = 1;
            weighty = 1;
        }});

        add(this.toggleUseless, new java.awt.GridBagConstraints() {{
            gridwidth = java.awt.GridBagConstraints.REMAINDER;
            gridx = 1;
        }});

        add(this.groupSettings, new java.awt.GridBagConstraints() {{
            gridwidth = java.awt.GridBagConstraints.REMAINDER;
            gridx = 1;
            gridy = 2;
        }});
    }

    // private JTextArea evntLog;
    private EvntTbl evntLogTbl;

    private javax.swing.JScrollPane evntScrll;

    /** A toggle for hiding useless events */
    private javax.swing.JToggleButton toggleUseless;

    /** Combobox for Group Settings */
    private javax.swing.JComboBox<String> groupSettings;

}
