package com.mycompany.entrancerando;

import com.mycompany.displaypnl.DisplayPnl;
import com.mycompany.maptracker.MapTracker;
import com.mycompany.entrances.ConnectionHandler;

/**
 * A handler for listening and controlling settings.
 *
 * @author aauyong
 */
public class SettingsHandler {

    public static SettingsHandler createInstance(DisplayPnl dispP, MapTracker map) {
        if (SINGLETON == null)
            SINGLETON = new SettingsHandler(dispP, map);

        return SINGLETON;
    }

    private SettingsHandler(DisplayPnl dispP, MapTracker map) {
        this.dispPnl = dispP;
        this.mapTrckr = map;

        SettingsHandler.currGroupingSetting = SettingsHandler.DEFAULT_GROUPING;

        addSettingsListeners();
    }

    /*
     * ===========================================================================
     * Private
     * ===========================================================================
     */

    private void addSettingsListeners() {
        var settingsMap = this.dispPnl.getSettings();

        /** Hide useless markers on toggle */
        var hideUselessButton = (javax.swing.JToggleButton)settingsMap.get("useless");
        hideUselessButton.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    mapTrckr.setUselessVisibility( !hideUselessButton.isSelected() );
                }
            }
        );

        var groupSettings = (javax.swing.JComboBox<String>)settingsMap.get("grouping");
        groupSettings.addItemListener(new java.awt.event.ItemListener() {

            @Override
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                currGroupingSetting = Grouping.getGrouping((String)groupSettings.getSelectedItem());

                ConnectionHandler.updateGroupSettings(currGroupingSetting);
            }

        });
    }

    /*
     * ---------------------------------------------------------------------------
     * Private Methods
     * ---------------------------------------------------------------------------
     */

    private static SettingsHandler SINGLETON;

    /** Display Panel */
    private DisplayPnl dispPnl;

    /** Map Tracker */
    private MapTracker mapTrckr;

    public enum Grouping {
        SIMPLE      ("simple"),
        RESTRICTED  ("restricted"),
        FULL        ("full"),
        CROSSED     ("crossed"),
        INSANITY    ("insanity");

        private String groupingName;
        Grouping(String s) {
            this.groupingName = s;
        }

        public final String asString() {
            return this.groupingName;
        }

        public static Grouping getGrouping(String s) {
            switch (s) {
                case "simple"       -> {return SIMPLE;}
                case "restricted"   -> {return RESTRICTED;}
                case "full"         -> {return FULL;}
                case "crossed"      -> {return CROSSED;}
                case "insanity"     -> {return INSANITY;}
                default             -> {return null;}
            }
        }

        public boolean lessThan(Grouping other) {
            return this.ordinal() < other.ordinal();
        }
    }

    public static Grouping currGroupingSetting;

    public final static Grouping DEFAULT_GROUPING = Grouping.SIMPLE;
}
