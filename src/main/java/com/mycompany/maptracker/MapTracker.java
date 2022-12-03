/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.maptracker;

import java.awt.Dimension;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Map;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JLayeredPane;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.entrancerando.SettingsHandler.Grouping;
import com.mycompany.entrances.EntranceGroup;
import com.mycompany.entrances.EntranceIcon;
import com.mycompany.entrancerando.SettingsHandler;

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

        setPreferredSize(new java.awt.Dimension(
            MapPnl.DEFAULT_SIZE * 2, MapPnl.DEFAULT_SIZE
        ));

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
     * Sets whether or not the {@code MapTracker} will hide
     * {@code USELESS EntranceIcons}.<p>
     *
     * This is done by updating the icons in the {@code light} and {@code dark}
     * panels to set theri to {@code b}.
     * @param b
     */
    public void setUselessVisibility(boolean b) {
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
        this.mapsPnl = new JPanel(new java.awt.GridBagLayout());

        this.light = new MapPnl();
        this.dark = new MapPnl();

        this.entrances = new HashMap<>();
        this.entranceGroups = new HashMap<>();

        this.otherMinSize = new Dimension();
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
     * Changes the orientation of the layout of the BG panels, flipping from
     * vertical to horizontal or vice versa, updates {@code horzOrntn}.
     *
     * This is done by removing the {@code dark} map panel and then re-adding it
     * into the second row.
     */
    private void changeOrient() {
        this.mapsPnl.getLayout().removeLayoutComponent(this.dark);
        this.mapsPnl.add(this.dark, new java.awt.GridBagConstraints() {
            {
                gridx = horzOrntn ? 1 : 0;
                gridy = !horzOrntn ? 1 : 0;
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
            boolean inLWorld = node.get("world").asText().equals("light");
            var e = new EntranceIcon(
                    node.get("x").asInt(),
                    node.get("y").asInt(),
                    node.get("type") != null ? node.get("type").asText() : "",
                    name,
                    (double) this.light.getMapDim().width / DEFAULT_SIZE,
                    inLWorld);
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
     *
     * Reading the JSON, {@code EntranceGroup}s are keyed into
     * {@code this.entranceGroups} by their name. The JSON is structured as a
     * grouping of names of {@code EntranceIcon}s in the group and a type and
     * direction.
     *
     * The directino is read and used to add the {@code EntranceIcon} to the group
     */
    private void organizeEntranceGroups() {
        var jn = readJson("/locationdata/groups.json");
        var jnIt = jn.fieldNames();
        while (jnIt.hasNext()) {
            String groupName = jnIt.next();
            var groupIt = jn.get(groupName).fieldNames();
            EntranceGroup group = new EntranceGroup(groupName);
            while( groupIt.hasNext() ) {
                String entrName = groupIt.next();
                EntranceIcon ei = this.entrances.get(entrName);

                String dir =  jn.get(groupName).get(entrName).get("direction").asText();
                if (dir.equals("null"))
                    group.add(ei);
                else
                    group.add(ei, dir);

                ei.setGroup(group);
            }
            this.entranceGroups.put(groupName, group);
        }
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

    private JPanel mapsPnl;

    /**
     * Custom Panel containing the left/top map
     */
    private MapPnl light;

    /** Custom Panel containing the right/bot map */
    private MapPnl dark;

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
}
