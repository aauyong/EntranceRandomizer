package com.mycompany.mapimagepnl;

import java.awt.Point;
import javax.swing.JButton;
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
    public EntranceIcon(Point l, String type, String name, double coordScale) {
        super();

        this.state = ConnectionState.UNKNOWN;
        this.entrPt = l;

        int offset = (int) ((double) DEFAULT_SIZE/2 * coordScale);
        this.entrPt.translate(-offset, -offset);
        this.setToolTipText("Unknown");

        this.entrType = type;
        this.entrShape = null;

        this.entrName = name;
        this.groupName = null;
        this.connection = null;

        setFocusable(true);
        setOpaque(false);
        setVisible(true);
    }

    /*===================================================================================
    OVERLOADED CONSTRUCTORS
    ===================================================================================*/
    public EntranceIcon(int x, int y, String type, String name, double coordScale) {
        this(new Point(x,y), type, name, coordScale);
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
     * @see clearConnection
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
    protected boolean clearConnection() {
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
     * (set to null)
     *
     * @return
     * {@code true} if the {@code state} is decremented successfully. <p>
     * {@code false} if the {@code state} cannot be decremented due to a set
     * {@code connection}.
     *
     * @see ConnectionState
     */
    public boolean decState() {
        if (this.connection == null) {
            setConnectionState(this.state.dec());
            return true;
        }
        return false;
    }

    public EntranceIcon getConnection() {
        return this.connection;
    }

    public Point getPt() {
        return this.entrPt;
    }

    public String getEntrName() {
        return this.entrName;
    }

    public final String getEntrType() {
        return this.entrType;
    }

    public boolean isUseless() {
        return this.state == ConnectionState.USELESS;
    }

    /**
     * Setter for {@code groupName}. Only allowed if {@code groupName} hasn't
     * been set.
     *
     * @param g
     */
    protected boolean setGroupName(String g) {
        if (this.groupName == null) {
            this.groupName = g;
            return true;
        }
        else
            return false;
    }

    /** Getter for {@code groupName}.
     * @return  */
    public String getGroupName() {
        return this.groupName;
    }

    /** Check if {@code this} is in a grouping
     * @return  */
    public boolean hasGrouping() {
        return this.groupName != null;
    }

    /** Getter for {@code groupDir}. */
    public char getGroupDir() {
        return this.groupDir;
    }

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
     * Override
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

    @Override
    public boolean contains(int x, int y) {
        return this.entrShape.contains(x, y);
    }

    protected ConnectionState getConnectionState() {
        return this.state;
    }

    //-----------------------------------------------------------------------------------

    /*===================================================================================
    Private Methods
    ===================================================================================*/

    protected void setConnectionState(ConnectionState s) {
        this.state = s;
        repaint();
    }

    private void setShape() {
        switch(this.entrType) {
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
    public final static int DEFAULT_SIZE = 15;

    /** Maximum Dimension size of the button */
    public final static int MAX_SIZE = 30;

    /** Minimum Dimension size of the button */
    public final static int MIN_SIZE = 10;

    /** Current Connection State of the button */
    private ConnectionState state;

    /**
     * Coordinate Point that the button represents based on the underlying
     * map image.
     */
    private final Point entrPt;

    /** Type of entrance, ex. cave or drop */
    private final String entrType;

    /** Shape of the Icon */
    private java.awt.Shape entrShape;

    /** Name of Entrance as declared by the ALTTPR randomizer */
    private final String entrName;

    /** Name of grouping that the entrance icon belongs to */
    private String groupName;

    /**
     *
     */
    private char groupDir;

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
