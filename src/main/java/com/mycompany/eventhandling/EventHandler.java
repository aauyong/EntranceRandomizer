package com.mycompany.eventhandling;

import com.mycompany.displaypnl.DisplayPnl;
import com.mycompany.entrances.*;
import com.mycompany.maptracker.MapTracker;

import java.util.Stack;

import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import java.util.List;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

/**
 * A handler for storing, tracking, and passing events between map and display
 * panels. <p>
 *
 * The handler behaves as both a listener and a pipeline for communication. Any
 * Events are passed into the event handler are also passed onto the display
 * panel. Any actions such as deletions or undo's that occur in the Display
 * panel are passed into the event handler, which will update the corresponding
 * EntranceIcons in the Map Tracker.
 *
 * //TODO Maybe main event storage should be a stack? That way this is stored
 * in the exact same way as the EventTbl
 * @author aauyong
 */
public class EventHandler {

    public EventHandler(DisplayPnl dp, MapTracker mp) {
        this.dispPnl = dp;
        this.mapTrckr = mp;

        this.events = new ArrayList<>();
        this.removedEvents = new Stack<>();

        addEventListeners();
    }

    /**
     * Attempt to add an event.<p>
     *
     * Inserts {@code e} into the list of events and updates {@code dispPnl}
     * with the new event.
     * @param e
     * @return <ul>
     *  <li> {@code true} :: if adding is successful
     *  <li> {@code false} :: if adding fails
     * </ul>
     */
    public boolean tryAddEvent(EntranceEvent e) {
        if (e == null)
            return false;

        this.events.add(0, e);
        updateDispPnl(e.asArray());

        return true;
    }

    /**
     * Removes and undoes the {@code i}-th event.
     * @param i The i-th event
     */
    public void removeEvent(int i) {
        EntranceEvent e = events.get(i);
        undoEvent(e);
        events.remove(i);
    }

    /**
     * getter for the most recent event in the list of events. If the list is
     * empty, {@code null} is returned.
     * @return
     */
    public EntranceEvent getMostRecentEvent() {
        if (!this.events.isEmpty())
            return this.events.get(0);
        else
            return null;
    }

    /**
     * Mouse Event assigned to each {@code EntranceIcon} in the tracker.
     * <p>
     *
     * Checks if {@code evt} is either a left or right click, peforms
     * accordingly, and returns an {@code EntranceEvent} describing the action
     * <p>
     *
     * @param e Clicked Icon
     * @param evt Mouse Event
     *
     * @return
     *      Returns the {@code EntranceEvent} that describes the event that
     *      occurs from the click. If there is no event, then {@code null} is
     *      returned
     */
    public Deque<EntranceEvent> entranceIconClicked(EntranceIcon e,
            java.awt.event.MouseEvent evt) {

        if (SwingUtilities.isLeftMouseButton(evt)) {
            return this.entrIconLeftClickBehavior(e);
        }
        else if (SwingUtilities.isRightMouseButton(evt)) {
            return this.entrIconRightClickBehavior(e);
        }

        return null;
    }

    /**
     * Creates a {@code connection} between {@code EntranceIcon}'s {@code a} and
     * {@code b}.<p>
     *
     * If {@code this.connectByGroups} is {@code true} and {@code a} and {@code b}
     * are in groupings, then the groups are retrieved. If the two groups are
     * connectable, then they are connected together
     *
     * Otherwise, {@code a} and {@code b} are connected together.
     *
     * @param a
     * @param b
     * @return A {@code Deque} of {@code EntranceEvents} if any events occur,
     * otherwise returns {@code null}.
     *
     * @see #tryConnect(EntranceIcon, EntranceIcon)
     */
    public Deque<EntranceEvent> createConnection(EntranceIcon a, EntranceIcon b) {

        if (!ConnectionHandler.areConnectable(a,b))
            return null;

        var evnts = ConnectionHandler.tryConnect(a,b);

        return new ArrayDeque<>(){{
            for (EntranceIcon[] e : evnts) {
                add(new EntranceEvent( EntranceEvent.CONNECTION, e[0], e[1] ));
            }
        }};
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
    public Deque<EntranceEvent> deleteConnection(EntranceIcon a, EntranceIcon b) {

        if ( tryDeleteConn(a,b) ) {
            return new ArrayDeque<>(){{
                add(new EntranceEvent( EntranceEvent.DELETION, a, b ));
            }};
        }

        return null;
    }

    /*
    * ===========================================================================
    * Private Methods
    * ===========================================================================
    */

    /**
     * Undoes an {@code EntranceEvent} of some {@code type}
     *
     * To undo an event, there is set of actions for each {@code EntranceEvent}
     * type... <p>
     * <ul>
     *  <li> {@code CONNECTION} :: A connection is created between
     * {@code placeA} and {@code placeB} of {@code e}.
     *
     *  <li> {@code DELETION} :: The connection between {@code placeA} and
     * {@code placeB} of {@code e} is deleted.
     *
     *  <li> {@code USELESS} :: {@code e} is incremented to the state of
     * {@code UNKNOWN}
     * </ul>
     *
     * @param e Event that will be undone
     */
    private void undoEvent(EntranceEvent e) {
        switch (e.getEventType()) {
            case EntranceEvent.CONNECTION ->
                deleteConnection(e.getPlaceA(), e.getPlaceB());
            case EntranceEvent.DELETION ->
                createConnection(e.getPlaceA(), e.getPlaceB());
            case EntranceEvent.USELESS ->
                e.getPlaceA().incState();
        }
        /**
         * TODO need to add the event created from the undoing action to the
         * removedEvents stack
         */
    }

    /**
     * Update the Display Panel with the first three elements of the event
     * @param evnt
     */
    private void updateDispPnl(String[] evnt) {
        this.dispPnl.addEvent(evnt[0], evnt[1], evnt[2]);
    }

    /**
     * Add Listeners for the purpose of Event Handling
     * // TODO Write Doc
     */
    private void addEventListeners() {
        // When table is changed, update the event handler to remove the
        // corresponding event
        dispPnl.getTblModel().addTableModelListener( new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent tblEvt) {
                if (tblEvt.getType() == TableModelEvent.DELETE) {
                    removeEvent(dispPnl.getRow(tblEvt.getFirstRow()));
                }
            }

        });

        // Clear active Entrance if the tracker is clicked
        mapTrckr.addMouseListener(new java.awt.event.MouseAdapter() {

            @Override
            public void mouseClicked(java.awt.event.MouseEvent mouseEvt) {
                if (SwingUtilities.isLeftMouseButton(mouseEvt)) {
                    clearActiveEntr();
                }
            }

        });

        // Each entrance icon gets a listener that adds the event ot the
        for (var e : mapTrckr.getEntrances().values()) {
            e.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(java.awt.event.MouseEvent mouseEvt) {
                    var evnts = entranceIconClicked(e, mouseEvt);
                    // TODO remove events from table on a right click/DELETION
                    if (evnts != null)
                        for (var evnt : evnts) tryAddEvent(evnt);
                }

            });
        }
    }

   /**
     * Wrapper for setting {@code activeEntr}. <p>
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
    private Deque<EntranceEvent> entrIconLeftClickBehavior(EntranceIcon e) {
        if (this.activeEntr == null) {
            setActiveEntr(e);
            return null;
        }

        var evnts = createConnection(this.activeEntr, e);

        clearActiveEntr();
        return evnts;
    }

    /**
     * Deletes the connection at {@code e} if there is one,
     * otherwise decrements the state of {@code e}. In either situation, the
     * active entrance is cleared and any selected icons are deselected. When
     * an icon is decremented, it only produces an event if the icon is either
     * marked as {@code USELESS}.
     *
     * @param e
     * @return
     */
    private Deque<EntranceEvent> entrIconRightClickBehavior(EntranceIcon e) {
        clearActiveEntr();

        if (e.getConnection() != null) {
            return deleteConnection(e, e.getConnection());
        }

        if ( e.decState() && e.isUseless() ) {
            return new ArrayDeque<EntranceEvent>(){{
                add(new EntranceEvent(EntranceEvent.USELESS, e, null));
            }};
        }

        return null;
    }

    /**
     * Set a connection between {@code a} and {@code b}.
     * @param a
     * @param b
     * @return {@code true} if the connection is successfully created, else
     * {@code false}
     */
    private boolean tryConnect(EntranceIcon a, EntranceIcon b) {
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
    private boolean tryDeleteConn(EntranceIcon a, EntranceIcon b) {
        if (a.getConnection() != b && b.getConnection() != a)
            return false;
        return a.clearConnection() && b.clearConnection();
    }

    /*===========================================================================
    Private Members
    ===========================================================================*/

    /** Display Panel to communicate with */
    private DisplayPnl dispPnl;

    /** Map Tracker to communicate with */
    private MapTracker mapTrckr;

    /** Actively Selected Entrance */
    private EntranceIcon activeEntr;

    /** List of Events that is used to keep track of displayable events*/
    private List<EntranceEvent> events;

    /**
     * A container used to log event operations in a FIFO ordeinrg. This is so
     * that events can be undone.
     */
    private Stack<EntranceEvent> removedEvents;
}
