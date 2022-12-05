package com.mycompany.entrances;

import java.awt.Point;

import javax.swing.JButton;

import com.mycompany.maptracker.MapPnl;

import java.awt.Dimension;
import java.awt.Color;

public class EntranceIcon extends JButton {

    /**
     * Constructor
     *
     * Builds an Entrance Icon at a Point, {@code l}, for some entrance
     * {@code name} of some {@code type}. Initializes the {@code state} to be
     * {@code UNKNOWN}.
     * <p>
     * The point is adjusted such that the button will land on center of the
     * provided point. This is done by taking a {@code coordScale} that will
     * scale the default dimensions of the button up or down to place the button
     * on the point.
     *
     * The button is set to be unfocusable to avoid the highlighting border
     *
     * And Opaqueness is reaffirmed
     *
     * @param l Point to place the Entrance at
     * @param type
     * @param name
     * @param coordScale A factor of which to scale the dimensions for center
     * placement
     */
    public EntranceIcon(int x, int y, String type, String name,
            double coordScale, boolean inLightWorld) {
        super();
        Point l = new Point(x,y);

        this.state = ConnectionState.UNKNOWN;
        this.entrPt = l;

        int offset = (int) ((double) DEFAULT_SIZE/2 * coordScale);
        this.entrPt.translate(-offset, -offset);
        this.setToolTipText("Unknown");

        this.entrType = EntrType.getFrom(type);
        
        // if (type.equals("cave") || type.equals("house"))
        //     this.entrType = EntranceIcon.NORMAL;
        // else
        //     this.entrType = type;

        this.entrShapeType = type;
        this.entrShape = null;
        setShape();

        this.entrName = name;
        this.group = null;
        this.connection = null;

        this.world = inLightWorld ? LIGHT : DARK;

        this.isVisibleWhenUseless = true;

        setFocusable(true);
        setOpaque(false);
        setVisible(true);
    }
    //-----------------------------------------------------------------------------------

    /*===================================================================================
    Public Methods
    ===================================================================================*/

    /**
     * Set's a connection between the calling Entrance and the {@code other}.
     *
     * A connection is made between {@code this} and {@code other} if the
     * active {@code connection} is null. <p>
     *
     * This method can also be called with {@code other == null} to clear the
     * connection. This will call the wrapper function for clearing it.<p>
     *
     * Additionally to creating a connection, the {@code state} is updated to
     * {@code CONNECTED}.
     *
     * @param other Entrance to connect to
     * @return
     * {@code true} : When a connection is successfully made or nullified <p>
     * {@code false} : When the connection fails. This occurs when a connection
     * to another Entrance is attempted without the current {@code connection}
     * being open ({@code null}).
     *
     * @see #clearConnection()
     */
    public boolean setConnection(EntranceIcon other) {
        if (other == null) { return clearConnection(); }

        if (this.connection == null) {
            this.connection = other;
            this.setToolTipText(other.getEntrName());
            setConnectionState(ConnectionState.CONNECTED);
            return true;
        }

        return false;
    }

    /**
     * Wrapper to clear {@code connection} and update the object
     *
     * First checks whether this object doesn't have a connection or is in the
     * useless state, and does nothing if so. <p>
     *
     * If there is an active connection, the connection is removed and the
     * {@code state} is updated back to {@code UNKNOWN}. The former connection
     * is then checked for whether it still has an active connection, and if so,
     * {@code clearConnection} is called to remove it.
     *
     * @return
     * {@code true} : if both connection removals are successful <p>
     * {@code false} : if either connection fails
     */
    public boolean clearConnection() {
        if (this.connection == null || this.state == ConnectionState.USELESS) {
            return false;
        }

        this.connection = null;
        this.setConnectionState(ConnectionState.UNKNOWN);
        this.setToolTipText("Unknown");
        return true;
    }

    /**
     * Increment the Connection {@code state} of the Location icon while also
     * checking the current state and settings of the icon to ensure that the
     * state is always in an appropriate bounds
     * <p>
     * If no {@code connection} is set, then the icon can never be set to a
     * state beyond {@code SELECTED}.
     * <p>
     * If there is a connection set, then {@code state} can be set to
     * {@code CONNECTED}.
     *
     * @see ConnectionState
     * @return
     * {@code true} if the state is successfully incremented
     * <p>
     * {@code false} if the state is not incremented. This occurs if
     * incrementing the state would set it to {@code CONNECTED} without it
     * being connected.
     */
    public boolean incState() {
        if (this.connection == null) {
            var newState = ConnectionState.min(
                    ConnectionState.PENDING,
                    this.state.inc()
            );
            boolean res = newState == this.state;
            setConnectionState(newState);
            return res;
        }
        this.state = this.state.inc();
        return true;
    }

    /**
     * Decrements the connection {@code state} and ensures that the icon is set
     * up properly such that the state is always accurate to the settings.
     *
     * If no {@code connection} is set, then the Icon can be decremented to
     * {@code USELESS}.
     *
     * However, if the {@code connection} is set, then the {@code state} cannot
     * be set below {@code CONNECTED} until the {@code connection} is cleared
     * (set to null). Additionally, if the {@code state} is useless, then it
     * fails to decrement
     *
     * @return <ul?
     *  <li> {@code true} :: if the {@code state} is decremented successfully.
     *  <li> {@code false} :: if the {@code state} cannot be decremented due to
     *  a set {@code connection} or if {@code state} is already useless
     * </ul>
     * @see ConnectionState
     */
    public boolean decState() {
        if (this.connection == null && !this.isUseless() ) {
            setConnectionState(this.state.dec());
            return true;
        }
        return false;
    } // decState

    /** Getter for the connecting {@code EntranceIcon} */
    public EntranceIcon getConnection() {
        return this.connection;
    }

    /** Get {@code Point} this refers to */
    public Point getPt() {
        return this.entrPt;
    }

    /** Get Name of this {@code EntranceIcon} */
    public String getEntrName() {
        return this.entrName;
    }

    /** Get Generic Type of this {@code EntranceIcon} */
    public final String getEntrType() {
        return this.entrType.asString();
    }

    /** If {@code this} is in the {@code USELESS} State */
    public boolean isUseless() {
        return this.state == ConnectionState.USELESS;
    }

    /** Set whether the icon should be visible if {@code USELESS} */
    public void setHidingVisibility(boolean b) {
        this.isVisibleWhenUseless = b;
        setVisible(checkIfVisible());
    }

    /**
     * Setter for {@code groupName}. Only allowed if {@code groupName} hasn't
     * been set.
     *
     * @param g
     */
    public boolean setGroup(EntranceGroup g) {
        if (this.group == null) {
            this.group = g;
            return true;
        }
        return false;
    }

    /** Getter for {@code group}.
     * @return  */
    public EntranceGroup getGroup() {
        return this.group;
    }

    /** Check if {@code this} is in a grouping
     * @return  */
    public boolean hasGrouping() {
        return this.group != null;
    }

    /**
     * @return {@code true} if {@code this} is in the l
     */
    public int getWorld() {
        return this.world;
    }

    /**
     * Override {@link #paintComponent(java.awt.Graphics)}
     * 
     * Sets the shape to be drawn and fills said shape with the chosen background 
     * color. BG is selected based on the connection state and whether the 
     * button model is armed
     */
    @Override
    protected void paintComponent(java.awt.Graphics g) {
        java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;
        setShape();

        if (getModel().isArmed())
            g2.setColor(ArmedBGColors[this.state.ordinal()]);
        else
            g2.setColor(BGColors[this.state.ordinal()]);

        g2.fill(this.entrShape);
    }

    /**
     * Override {@link #paintBorder(java.awt.Graphics)} <p>
     * 
     * Paints a 3pt black border around {@code this}'s shape as well as the clipping
     * shape/size. 
     */
    @Override
    protected void paintBorder(java.awt.Graphics g) {
        java.awt.Graphics2D g2  = (java.awt.Graphics2D) g;
        final int strokeSize = 3;
        g2.setStroke(new java.awt.BasicStroke(strokeSize));

        g2.setColor(Color.BLACK);
        g2.setClip(this.entrShape);
        g2.draw(this.entrShape);
    }

    /**
     * Override {@link #getPreferredSize()} <p>
     *
     * Scales the dimensions of the button based on the parent {@code MapPnl}
     * window and the {@code DEFAULT_SIZE}. If the button size would be out of
     * the range of {@code [MIN_SIZE, MAX_SIZE]}, then it is set either to those
     * sizes.
     */
    @Override
    public Dimension getPreferredSize() {
        int w = (int)((double) DEFAULT_SIZE / MapPnl.DEFAULT_SIZE * getParent().getWidth());
        if (w > MAX_SIZE) {
            return new Dimension(MAX_SIZE, MAX_SIZE);
        }
        else if (w < MIN_SIZE) {
            return new Dimension(MIN_SIZE, MIN_SIZE);
        }
        return new Dimension(w,w);
    }

    /**
     * Override {@link #contains(int, int)} <p>
     * 
     * Calls the super implementation of {@code entrShape}'s contains
     */
    @Override
    public boolean contains(int x, int y) {
        return this.entrShape.contains(x, y);
    }

    /** Get the {@code ConnectionState} of {@code this} */
    public ConnectionState getConnectionState() {
        return this.state;
    }

    /** Sets {@code this.state} to the {@code UNKNOWN} state*/
    public boolean setToUnknown() {
        if (this.state == ConnectionState.UNKNOWN )
            return false;
        this.state = ConnectionState.UNKNOWN;
        return true;
    }
    
    private void setConnectionState(ConnectionState s) {
        this.state = s;
        setVisible(checkIfVisible());
        repaint();
    }

    //-----------------------------------------------------------------------------------

    /*===================================================================================
    Private Methods
    ===================================================================================*/
    // TODO write doc
    private boolean checkIfVisible() {
        return this.isVisibleWhenUseless || (this.state != ConnectionState.USELESS);
    }

    /**
     * Set the Shape and location of {@code this} based on the Entrane Type.<p>
     * Draws a circle for {@code drop}'s, diamond for {@code cave}'s, a triangle
     * for {@code dungeon}s, and a rectangle by default.
     */
    private void setShape() {
        switch(this.entrShapeType) {
            // TODO write case for dungeons, triangle
            case "drop" -> this.entrShape = new java.awt.geom.Ellipse2D.Double(
                    0, 0, getWidth(), getHeight()
                );
            case "cave" -> {
                    var w = getWidth();
                    this.entrShape = new java.awt.Polygon(
                            new int[]{w/2, w, w/2, 0}, // x
                            new int[]{0, w/2, w, w/2}, // y
                            4
                    );
                }
            default -> this.entrShape = new java.awt.Rectangle(
                    0, 0, getWidth(), getHeight()
                );
        }
    }

    /*===================================================================================
    Variables
    ===================================================================================*/

    /** Default Dimensions of the button */
    public final static int DEFAULT_SIZE = 25;

    /** Maximum Dimension size of the button */
    public final static int MAX_SIZE = DEFAULT_SIZE * 2;

    /** Minimum Dimension size of the button */
    public final static int MIN_SIZE = DEFAULT_SIZE/3;

    /** Normal cateogrization to describe Type */
    protected final static String NORMAL = "normal";

    /** Drop categorization to describe Type */
    protected final static String DROP = "drop";

    /** Dungeon categorization to describe Type */
    protected final static String DUNGEON = "dungeon";

    /** Constant Static int for Light World representation */
    protected final static int LIGHT = 1;

    /** Constant Static int for Dark World representation */
    protected final static int DARK = 2;

    /** Current Connection State of the button */
    private ConnectionState state;

    /**
     * Coordinate Point that the button represents based on the underlying
     * map image.
     */
    private final Point entrPt;

    /**  */
    // private final String entrType;
    private final EntrType entrType;

    enum EntrType {
        NORMAL  ("normal"),
        DROP    ("drop"),
        DUNGEON ("dungeon");
        
        private String category;
        EntrType(String s) {
            this.category = s;
        }

        public String asString() { return this.category; }

        public static EntrType getFrom(String s) {
            switch(s) {
                case "normal":  return NORMAL;
                case "cave":    return NORMAL;
                case "drop":    return DROP;
                case "dungeon": return DUNGEON;
            }
            return null;
        }
    };

    /** Shape of the Icon */
    private java.awt.Shape entrShape;

    /** Type of entrance, ex. cave or drop */
    private final String entrShapeType;

    /** Name of Entrance as declared by the ALTTPR randomizer */
    private final String entrName;

    private final int world;

    private EntranceGroup group;

    private boolean isVisibleWhenUseless;

    /** {@code EntranceIcon} that {@code this} entrance is connected to*/
    private EntranceIcon connection;

    /**
     * Background colors to paint. Used in conjunction with the
     * {@code state} to decide which background color should be painted
     */
    private final Color[] BGColors = {
        Color.RED,
        new Color(0,0,200),
        Color.ORANGE,
        Color.GREEN
    };

    private final Color[] ArmedBGColors = {
        new Color(255,122,122),
        new Color(33,33,165),
        new Color(255,165,122),
        new Color(122,255,122)
    };
}
