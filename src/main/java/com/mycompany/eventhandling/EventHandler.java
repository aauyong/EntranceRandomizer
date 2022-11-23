package com.mycompany.eventhandling;

import com.mycompany.displaypnl.DisplayPnl;
import com.mycompany.mapimagepnl.MapTracker;

import java.util.List;
import java.util.ArrayList;
import java.util.Stack;

/**
 * A handler for storing, tracking, and passing events between map and display
 * panels. <p>
 *
 * The handler behaves as both a listener and a pipeline for communication. Any
 * Events are passed into the event handler are also passed onto the display
 * panel. Any actions such as deletions or undo's that occur in the Display
 * panel are passed into the event handler, which will update the corresponding
 * EntranceIcons in the Map Tracker.
 * @author aauyong
 */
public class EventHandler {
    public EventHandler(DisplayPnl dp, MapTracker mp) {
        this.dispPnl = dp;
        this.mapTrckr = mp;

        this.events = new ArrayList<>();
        this.removedEvents = new Stack<>();
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

        this.events.add(e);
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
        if (this.events.size() > 0)
            return this.events.get(this.events.size() - 1);
        else
            return null;
    }

    /*===========================================================================
    Private Methods
    ===========================================================================*/

    /**
     * Undoes an {@code EntranceEvent} of some {@code type}
     *
     * To undo an event, there is set of actions for each {@code EntranceEvent}
     * type... <p>
     * <ul>
     * <li> {@code CONNECTION} :: A connection is created between
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
                mapTrckr.deleteConnection(e.getPlaceA(), e.getPlaceB());
            case EntranceEvent.DELETION ->
                mapTrckr.createConnection(e.getPlaceA(), e.getPlaceB());
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

    /*===========================================================================
    Private Members
    ===========================================================================*/

    /** Display Panel to communicate with */
    private DisplayPnl dispPnl;

    /** Map Tracker to communicate with */
    private MapTracker mapTrckr;

    /** List of Events that is used to keep track of displayable events*/
    private List<EntranceEvent> events;

    /**
     * A container used to log event operations in a FIFO ordeinrg. This is so
     * that events can be undone.
     */
    private Stack<EntranceEvent> removedEvents;
}
