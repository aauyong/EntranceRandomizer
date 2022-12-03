/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.maptracker;

import java.awt.Image;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.nio.file.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.entrances.ConnectionState;
import com.mycompany.entrances.EntranceIcon;
import com.fasterxml.jackson.databind.JsonNode;
import java.net.URISyntaxException;

import javax.swing.JPanel;

/**
 * A JPanel that utilizes a
 * @author blarg
 */
public class MapPnl extends JPanel {

    public MapPnl(Image map) {
        setMap(map);
        this.locations = new HashMap<>();
        this.setLayout(null);
    }

    /*===========================================================================
    Overloaded Constructors
    ===========================================================================*/
    public MapPnl() {
        this(null);
    }


    /*===========================================================================
    Public Methods
    ===========================================================================*/
    public void setMap(Image map) {
        this.bg = map;
    }

    /**
     * Override of paintComponent <p>
     *
     * Calls {@code super} paintComponent, and then draws the stored background
     * image, {@code bg}, into the panel.<p>
     *
     * A scale is then created that takes the scale of the current size of the
     * panel to the size of the {@code bg} image. If this scale differs from
     * {@code prevScale}, the scale used to paint the component previously, then
     * the entrance locations are updated.
     *
     * @see updateEntrs
     */
    @Override
    public void paintComponent(java.awt.Graphics g) {
        super.paintComponents(g);
        g.drawImage(this.bg, 0, 0, getWidth(), getHeight(), null);

        double scale = (double)getWidth() / this.bg.getWidth(this);

        if (prevScale != scale) {
            updateEntrs(scale);
            prevScale = scale;
        }
    }

    /**
     * Getter
     */
    @Override
    public java.awt.Dimension getPreferredSize() {
        int newSize = this.getParent().getWidth()/2;

        return new java.awt.Dimension(newSize, newSize);
    }

    /**
     * Puts an EntranceIcon, {@code e} into the {@code locations} map and adds
     * it into the layout of {@code this} panel.
     * @param e EntranceIcon to insert
     * @param name Name of the icon, used for keying
     */
    public void addLoc(EntranceIcon e, String name) {
        locations.put(name, e);
        this.add(e);
    }

    public Map<String, EntranceIcon> getLocations() {
        return this.locations;
    }

    public java.awt.Dimension getMapDim() {
        return new java.awt.Dimension( this.bg.getWidth(this), this.bg.getHeight(this));
    }

    /**
     * Set's the visibility of all {@code EntranceIcon}s when they are in the
     * {@code USELESS} state
     * @param b
     */
    protected void setUselessVisibility(boolean b) {
        for (var l : locations.values()) {
            l.setHidingVisibility(b);
        }
    }

    /*===========================================================================
    Private Methods
    ===========================================================================*/
    /**
     * Update the placement of the Entrances <p>
     *
     * Iterates through each Entrance in the panel and sets its bounds at
     * the coordinate point that it stores. This point is scaled to the
     * coordinate space of the panel.<p>
     *
     * If the panel is set to hide useless points, then those points are skipped
     * over.
     * @param scale scale of panel space to the coordinate space
     */
    private void updateEntrs(double scale) {
        for (var l : locations.values()) {
            var p = l.getPt();
            java.awt.Point newP = new java.awt.Point(
                (int)( (double) p.x * scale),
                (int)( (double) p.y * scale)
            );

            l.setBounds(new java.awt.Rectangle(newP, l.getPreferredSize()));
        }
    }

    /*---------------------------------------------------------------------------
    Private Members
    ---------------------------------------------------------------------------*/
    /**
     * Image holder for the desired map
     */
    private Image bg;

    /**
     * Default width/height of the panel
     */
    public static final int DEFAULT_SIZE = 800;

    /**
     * Map of Entrances, keyed by name
     */
    private HashMap<String, EntranceIcon> locations;

    /**
     * Cache of previous scale used for painting
     */
    private double prevScale;
}
