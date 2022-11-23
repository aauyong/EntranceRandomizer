/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mapimagepnl;

import java.awt.Dimension;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.Connections.*;
import com.mycompany.eventhandling.EntranceEvent;

/**
 * A Container for the Map Tracker, which is composed of two
 * MapBgPnls that track locations and icons.
 *
 *
 * @author blarg
 */
public class MapTracker extends JLayeredPane {

    public MapTracker() {
        super();

        initComponents();
        initMapsPnl();
        readAndAddEntrs();

        this.setLayout(new javax.swing.OverlayLayout(this));

        add(this.mapsPnl, JLayeredPane.DEFAULT_LAYER);
        add(this.overlayPnl, JLayeredPane.PALETTE_LAYER);

        setPreferredSize(new java.awt.Dimension(
            MapPnl.DEFAULT_SIZE * 2, MapPnl.DEFAULT_SIZE
        ));

        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent mouseEvt) {
                if (SwingUtilities.isLeftMouseButton(mouseEvt)) {
                    clearActiveEntr();
                }
            }
        });
    }

    /**
     * Mouse Event assigned to each {@code EntranceIcon} in the tracker.
     * <p>
     *
     * Checks if {@code evt} is either a left or right click, peforms
     * accordingly, and returns an {@code EntranceEvent} describing the action
     * <p>
     *
     *
     *
     * @param e Clicked Icon
     * @param evt Mouse Event
     *
     * @return
     *      Returns the {@code EntranceEvent} that describes the event that
     *      occurs from the click. If there is no event, then {@code null} is
     *      returned
     */
    public EntranceEvent entranceIconClicked(EntranceIcon e,
            java.awt.event.MouseEvent evt) {

        if (SwingUtilities.isLeftMouseButton(evt)) {
            return this.leftClickBehavior(e);
        }
        else if (SwingUtilities.isRightMouseButton(evt)) {
            return this.rightClickBehavior(e);
        }

        return null;
    }


    /**
     * Setter for {@code otherMinSize}
     *
     * @param d
     */
    public void setOtherMinSize(Dimension d) {
        otherMinSize = d;
    }

    /**
     * Changes the orientation of the layout of the BG panels, flipping from
     * vertical to horizontal or vice versa, updates {@code horzOrntn}.
     *
     * This is done by removing the {@code dark} map panel and then re-adding it
     * into the second row.
     */
    public void changeOrient() {
        this.mapsPnl.getLayout().removeLayoutComponent(this.dark);
        this.mapsPnl.add(this.dark, new java.awt.GridBagConstraints() {
            {
                gridx = horzOrntn ? 1 : 0;
                gridy = !horzOrntn ? 1 : 0;
                ;
                anchor = java.awt.GridBagConstraints.NORTHWEST;
                fill = java.awt.GridBagConstraints.BOTH;
                weightx = 1;
                weighty = 1;
            }
        });
        revalidate();
        repaint();
    }

    /**
     * Returns a preferred size that fits the space provided to the map tracker
     *
     * Returns a dimension that fits the provided space, calculated by taking
     * the size of the parent window and subtracting the minimum space required
     * by other elements, {@code otherMinSize} from the size of the current
     * window.
     *
     * Maintains a rectangle, 2:1 if horizontal or 1:2 if vertical
     *
     * If the dimension would {@code <= 0}, then the {@code DEFAUlT_SIZE} is used.
     *
     * @see {@code horzOrntn}
     * @see {@code changeOrient}
     * @see {@code otherMinSize}
     * @see {@code setOtherMinSize}
     * @see {@code this.DEFAULT_SIZE}
     */
    @Override
    public Dimension getPreferredSize() {
        Dimension d = this.getParent().getSize();
        d.height -= otherMinSize.height;

        if (d.width > 0
                && horzOrntn
                && d.width < MapPnl.DEFAULT_SIZE * 2) {
            this.horzOrntn = false;
            changeOrient();
        } else if (!horzOrntn && d.width >= MapPnl.DEFAULT_SIZE * 2) {
            this.horzOrntn = true;
            changeOrient();
        }

        int prio, other;
        int x = 1, y = 1;
        if (horzOrntn) {
            prio = d.width;
            other = d.height;
            x = 2;
        } else {
            prio = d.height;
            other = d.width;
            y = 2;
        }

        Dimension newDim = new Dimension();
        if (other < (prio / 2)) {
            other = other <= 0 ? MapPnl.DEFAULT_SIZE : other;
            newDim.setSize(other * x, other * y);
        } else {
            prio = prio <= 0 ? MapPnl.DEFAULT_SIZE : prio;
            newDim.setSize(prio / y, prio / x);
        }
        return newDim;
    }

    public Map<String, EntranceIcon> getEntrances() {
        return entrances;
    }

    /**
     * Creates a {@code connection} between EntranceIcon's {@code a} and
     * {@code b}.
     * <p>
     *
     * Calls {@code setConnection} for both {@code a} and {@code b}, using each
     * other as parameters.
     * <p>
     *
     * TODO update
     * TODO needs some overhaul, how do i connect groups and inform the events?. 
     * Might need to do something like returning a list of events. As it is, the events
     * are only generated by the clicking action, maybe should be generated by the 
     * createConnection?
     *
     * @param a
     * @param b
     * @return
     *         {@code true} : If both connections are successfully made.
     *         <p>
     *         {@code false} : If either connection fails.
     *         <p>
     */
    public boolean createConnection(EntranceIcon a, EntranceIcon b) {
        if ( connectByGroups && (a.hasGrouping() && b.hasGrouping()) ) {
            EntranceGroup egA = this.entranceGroups.get(a.getGroupName());
            EntranceGroup egB = this.entranceGroups.get(b.getGroupName());

            return egA.connectToGroup(egB);
        }
        return connect(a,b);
    }

    /**
     * Delete the connection between {@code e} and {@code e2}.<p>
     *
     * Calls both {@code EntranceIcon}'s clearConnection, and returns the
     * {@code AND} of their results.
     *
     * TODO update
     *
     * @param a
     * @return
     * <ul>
     *  <li>{@code true} : if both connections are successfully cleared
     *  <li>{@code false} : if either connection fails to be cleared or
     *      {@code e} and {@code e2} are not connected to one another.
     * </ul>
     */
    public boolean deleteConnection(EntranceIcon a, EntranceIcon b) {
        if (connectByGroups
                && (a.getGroupName() != null && b.getGroupName() != null) ) {
            // return deleteGroupConn(a,b);
        }
        return deleteConn(a,b);
    }


    /**
     * Sets whether or not the {@code MapTracker} will hide
     * {@code USELESS EntranceIcons}.<p>
     *
     * This is done by updating the icons in the {@code light} and {@code dark}
     * panels to set their visibilty to {@code b}.
     * @param b
     */
    public void isHidingUseless(boolean b) {
        this.light.setUselessVisibility(b);
        this.dark.setUselessVisibility(b);
    }

    // -----------------------------------------------------------------------------------

    /*
     * =============================================================================
     * Private Methods
     * =============================================================================
     */

    /**
     * Initialize the components in memory
     */
    private final void initComponents() {
        this.overlayPnl = new ConnectionPnl();
        this.mapsPnl = new JPanel(new java.awt.GridBagLayout());

        this.light = new MapPnl();
        this.dark = new MapPnl();
        this.activeEntr = null;

        this.entrances = new HashMap<>();
        this.entranceGroups = new HashMap<>();

        this.otherMinSize = new Dimension();

        this.hideUseless = false;
    }

    /**
     * Initiation of the two background panels for the {@code light} world and
     * {@code dark} world.
     *
     * Sets the map for each {@code mapBgPnl} and places it in the layout.
     *
     * @see {@code mapBgPnl}
     */
    private void initMapsPnl() {
        try {
            this.light.setMap(ImageIO.read(
                    this.getClass().getResource("/maps/light_world.png")));
        } catch (java.io.IOException e) {
            this.light = null;
            System.out.println("Light world map missing");
        }

        try {
            this.dark.setMap(ImageIO.read(
                    this.getClass().getResource("/maps/dark_world.png")));
        } catch (java.io.IOException e) {
            this.dark = null;
            System.out.println("Dark world map missing");
        }
        this.mapsPnl.setLayout(new java.awt.GridBagLayout());

        this.mapsPnl.add(this.light, new java.awt.GridBagConstraints() {
            {
                anchor = java.awt.GridBagConstraints.NORTHEAST;
                fill = java.awt.GridBagConstraints.BOTH;
                weightx = 1;
                weighty = 1;
            }
        });

        this.mapsPnl.add(this.dark, new java.awt.GridBagConstraints() {
            {
                gridx = 1;
                anchor = java.awt.GridBagConstraints.NORTHWEST;
                fill = java.awt.GridBagConstraints.BOTH;
                weightx = 1;
                weighty = 1;
            }
        });
    }

    /**
     * Wrapper for setting {@code activeEntr}.
     * <p>
     *
     * Sets {@code activeEntr} to {@code e} if {@code e} isn't in the
     * {@code USELESS} state. Increments the state of the entrance regardless.
     *
     * @param e
     */
    private void setActiveEntr(EntranceIcon e) {
        if (!e.isUseless())
            this.activeEntr = e;
        e.incState();
    }

    /**
     * Set a connection between {@code a} and {@code b}.
     * @param a
     * @param b
     * @return {@code true} if the connection is successfully created, else
     * {@code false}
     */
    private boolean connect(EntranceIcon a, EntranceIcon b) {
        if (a == b) {
            return a.setConnection(b);
        }
        return a.setConnection(b) && b.setConnection(a);
    }

    /**
     * Delete the connection between {@code a} and {@code b}.
     * @param a
     * @param b
     * @return {@code true} if the connection is successfully deleted, else
     * {@code false}
     */
    private boolean deleteConn(EntranceIcon a, EntranceIcon b) {
        if (a.getConnection() != b && b.getConnection() != a)
            return false;
        return a.clearConnection() && b.clearConnection();
    }
    /*
    private boolean createGroupConn(EntranceIcon a, EntranceIcon b) {
        var groupA = this.entranceGroups.get(a.getGroupName());
        var groupB = this.entranceGroups.get(b.getGroupName());

        for (int i = 0; i < groupA.size(); i++) {
            if ( !connect( groupA.get(i), groupB.get(i) ) )
                return false;
        }

        return true;
    }

    private boolean deleteGroupConn(EntranceIcon a, EntranceIcon b) {
        var groupA = this.entranceGroups.get(a.getGroupName());
        var groupBName = b.getGroupName();

        for (var ei : groupA) {
            var conn = ei.getConnection();
            if ( conn == null || !conn.getGroupName().equals(groupBName) )
                return false;
            else if ( !deleteConn(ei, conn) )
                return false;
        }

        return true;
    }
    // */
    /**
     * Reads and adds entrances from the locations JSON file.<p>
     *
     * Locations are read into a Jackson {@code JsonNode}, and then parsed to
     * gather the data about each possible entrance detailed in the file.<p>
     *
     * Each node is expected to at least contain:
     * <ul>
     *  <li> x coordinate : X position on a 4096pix map
     *  <li> y coordinate : Y position on a 4096pix map
     *  <li> type : A description of the type of entrance the object is
     *  <li> name : Name of the entrance
     *  <li> world : Phase of world where the entrance is located.
     * </ul> <p>
     *
     * this information is read into a new {@code EntranceIcon} that is then
     * passed to the proper map panel based on its {@code world} setting.
     *
     * @see EntranceIcon
     * @see JsonNode
     * @see ObjectMapper
     */
    private void readAndAddEntrs() {
        var jn = readJson("/locationdata/locations.json");
        var it = jn.fieldNames();
        while (it.hasNext()) {
            String name = it.next();

            var node = jn.get(name);
            var e = new EntranceIcon(
                    node.get("x").asInt(),
                    node.get("y").asInt(),
                    node.get("type") != null ? node.get("type").asText() : "",
                    name,
                    (double) this.light.getMapDim().width / DEFAULT_SIZE);
            if (node.get("world").asText().equals("light") )
                this.light.addLoc(e, name);
            else
                this.dark.addLoc(e, name);

            this.entrances.put(name, e);
        }
        organizeEntranceGroups();
    }

    /**
     * Following a grouping JSON file, organize all previously read
     * {@code EntranceIcon} into a mapping by the group name and assign the
     * group name to each icon for later use.
     * TODO update doc
     */
    private void organizeEntranceGroups() {
        var jn = readJson("/locationdata/groups.json");
        var jnIt = jn.fieldNames();
        while (jnIt.hasNext()) {
            String groupName = jnIt.next();
            var groupIt = jn.get(groupName).fieldNames();
            EntranceGroup group = new EntranceGroup();
            while( groupIt.hasNext() ) {
                String entrName = groupIt.next();
                EntranceIcon ei = this.entrances.get(entrName);
                String dir =  jn.get(groupName).get(entrName).get("direction").asText();
                group.add(ei, dir);
                ei.setGroupName(groupName);
            }
            this.entranceGroups.put(groupName, group);
        }
        var x = 10 + 10;
    }

    /**
     * Sets {@code activeEntr} to null, and updates any
     * {@code PENDING EntranceIcon}s back to their {@code UNKNOWN} state.
     */
    private void clearActiveEntr() {
        if (this.activeEntr == null) return;

        if (this.activeEntr.getConnectionState() == ConnectionState.PENDING) {
            this.activeEntr.setConnectionState(ConnectionState.UNKNOWN);
        }
        this.activeEntr = null;
    }

    /**
     * Sets the {@code activeEntr} to {@code e} if it's
     * empty. Otherwise, a connection is formed between the {@code activeEntr}
     * and the {@code e} and {@code activeEntr} is cleared.
     *
     * @param e
     * @return
     */
    private EntranceEvent leftClickBehavior(EntranceIcon e) {
        if (this.activeEntr == null) {
            setActiveEntr(e);
            return null;
        }

        createConnection(this.activeEntr, e);

        var evnt = new EntranceEvent( EntranceEvent.CONNECTION, this.activeEntr,
                e);
        clearActiveEntr();
        return evnt;
    }

    /**
     * Deletes the connection at {@code e} if there is one,
     * otherwise decrements the state of {@code e}. In either situation, the
     * active entrance is cleared and any selected icons are deselected. When
     * an icon is decremented, it only produces an event if the icon is either
     * marked as {@code USELESS}.
     *
     * @param e
     *
     * @return
     */
    private EntranceEvent rightClickBehavior(EntranceIcon e) {
        clearActiveEntr();

        if (e.getConnection() != null) {
            var evnt = new EntranceEvent( EntranceEvent.DELETION, e,
                    e.getConnection() );
            deleteConnection(e, e.getConnection());
            return evnt;
        }

        e.decState();
        if ( e.isUseless() ) {
            e.setVisible(!this.hideUseless);
            return new EntranceEvent( EntranceEvent.USELESS, e, null );
        }

        return null;
    }

    /**
     * Reads a JSON file located at {@code path}.
     * @param path Relative resource path
     * @return {@code JsonNode} created from reading the file. {@code null} if
     * there is any reading errors
     */
    private JsonNode readJson(String resourcePath) {
        Path p;
        try {
            p = Paths.get(getClass().getResource(resourcePath).toURI());
        } catch (URISyntaxException e) {
            return null;
        }

        JsonNode jn = null;
        try {
            byte[] mapData = Files.readAllBytes(p);
            ObjectMapper objectMapper = new ObjectMapper();
            jn = objectMapper.readTree(mapData);
        } catch (IOException e) {
            return null;
        }
        return jn;
    }
    // -----------------------------------------------------------------------------------

    /*
     * =============================================================================
     * Variables
     * =============================================================================
     */

    /**
     * Default Size of the JPanel to be used when the size of the parent window
     * hasn't been set yet
     */
    public final static int DEFAULT_SIZE = 600;

    private ConnectionPnl overlayPnl;

    private JPanel mapsPnl;

    /**
     * Custom Panel containing the left/top map
     */
    private MapPnl light;

    /**
     * Custom Panel containing the right/bot map
     */
    private MapPnl dark;

    private EntranceIcon activeEntr;

    /**
     * Whether Orientation of the panel should be horizontal or not
     */
    private boolean horzOrntn = true;

    /**
     * Dimension that stores the minimum size of other elements in the window
     * Used to calculate the preferred size of the component
     */
    private Dimension otherMinSize;

    private Map<String, EntranceIcon> entrances;

    /**
     * a mapping of each group to its corresponding elements.
     */
    private Map<String, EntranceGroup> entranceGroups;

    /**
     * A state variable that indicates whether or not useless locations are
     * being hidden on the map tracker.
     * */
    private boolean hideUseless;

    private boolean connectByGroups = true;
}
