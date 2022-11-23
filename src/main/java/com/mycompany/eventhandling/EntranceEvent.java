package com.mycompany.eventhandling;

import com.mycompany.mapimagepnl.EntranceIcon;

/**
 * An abstract representation of an Event involving {@code EntranceIcon}s
 *
 * An Entrance event consists of a type and at least one location. The type
 * describes the event as one of the three options: {@code CONNECTION},
 * {@code DELETION}, or {@code USELESS}. <p>
 *
 * Each option is largely self-explanatory; {@code CONNECTION} describes an
 * event that connects two entrances, {@code DELETION} describes the removal
 * of a connectionb between two entrances, and {@code USELESS} describes the
 * marking of a location as such.
 *
 * @author aauyong
 */
public class EntranceEvent {

    public EntranceEvent(String eventType_, EntranceIcon placeA_,
                EntranceIcon placeB_) {
        this.eventType = eventType_;
        this.entrA = placeA_;
        this.entrB = placeB_;
    }
    /*===========================================================================
    Overloaded Constructors
    ===========================================================================*/
    public EntranceEvent() {
        this(null, null, null);
    }

    public EntranceEvent(String event) {
        this(event, null, null);
    }

    public EntranceEvent(String eventType_, EntranceIcon placeA_) {
        this(eventType_, placeA_, null);
    }

    /*===========================================================================
    Public Methods
    ===========================================================================*/
    public String[] asArray() {
        return new String[]{
            this.eventType,
            this.entrA != null ? this.entrA.getEntrName() : "",
            this.entrB != null ? this.entrB.getEntrName() : ""
        };
    }

    public void setEventType(String t) {
        this.eventType = t;
    }

    public String getEventType() {
        return this.eventType;
    }

    public void setPlaceA(EntranceIcon a) {
        this.entrA = a;
    }

    public EntranceIcon getPlaceA() {
        return this.entrA;
    }

    public void setPlaceB(EntranceIcon b) {
        this.entrB = b;
    }

    public EntranceIcon getPlaceB() {
        return this.entrB;
    }

    /*---------------------------------------------------------------------------
    Public Members
    ---------------------------------------------------------------------------*/
    public final static String CONNECTION = "connect";
    public final static String DELETION = "delete";
    public final static String USELESS = "useless";

    /*---------------------------------------------------------------------------
    Private Members
    ---------------------------------------------------------------------------*/
    private String eventType;
    private EntranceIcon entrA;
    private EntranceIcon entrB;
}
