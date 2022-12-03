package com.mycompany.entrances;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.Set;

import com.mycompany.eventhandling.EntranceEvent;

/**
 * A container for {@code EntranceIcons} that represents a grouping of entrances
 *
 * Contains two hashmaps, one that keys entrances to a direction and one
 * that keys entrances to a list of the type of the entrances
 *
 * @author aauyong
 */
public class EntranceGroup {

    /**
     * Constructor. Initializes both maps, inserting keys for the four cardinal
     * directions into the direction grouping.
     */
    public EntranceGroup(String name) {
        this.groupName = name;
        this.entrGrpng = new LinkedHashMap<>();
        this.dropPairing = false;
    }

    /**
     * Attempts to connect two groups together. This and {@code other} must be
     * of the same grouping type, either using directional grouping or
     * type grouping.
     * @param other
     * @return
     */
    public Deque<EntranceIcon[]> tryConnToGroup(EntranceGroup other) {
        if (!this.areConnectable(other))
            return null;

        Deque<EntranceIcon[]> evnts = new ArrayDeque<>();

        for (var thisKey : this.entrGrpng.keySet() ) {
            var thisGrp = this.entrGrpng.get(thisKey);
            var otherGrp = other.getEntrsByKey(thisKey);

            // Check if Other group contains said key
            if (otherGrp == null) return null;

            var thisGrpIt = thisGrp.iterator();
            var otherGrpIt = otherGrp.iterator();
            while (thisGrpIt.hasNext() && otherGrpIt.hasNext()) {
                EntranceIcon eiA = thisGrpIt.next();
                EntranceIcon eiB = otherGrpIt.next();
                if ( eiA.setConnection(eiB) ) {
                    evnts.add(new EntranceIcon[]{eiA, eiB});
                }
            }
        }

        if (evnts != null)
            this.connectingGroup = other;

        return evnts;
    } // connectToGroup

    /**
     * wrapper for adding elements to the group
     *
     * Adds an Entrance based on its entrance type, or if a {@code dirOpt} is
     * provided, then it will be keyed by that value.
     * @param e Entrance to add
     * @param dirOpt Optional Cardinal direction to key the entrance by
     * @return
     *      {@code true} :: if adding is successful
     *      {@code false} :: if adding fails
     */
    public boolean add(EntranceIcon e, String... dirOpt) {
        String key;
        if (dirOpt.length == 1) {
            if ( this.isViableKey(dirOpt[0]) )
                key = dirOpt[0];
            else
                return false;
        }
        else if (dirOpt.length > 0) {
            // TODO throw custom error?
            return false;
        }
        else
            key = e.getEntrType();

        if (!entrGrpng.containsKey(key)) {
            entrGrpng.put(key, new ArrayDeque<>());
        }

        entrGrpng.get(key).add(e);
        this.dropPairing = key.equals(EntranceIcon.DROP);
        return true;
    }

    public int getSize() {
        int size = 0;
        for (var c : this.entrGrpng.values()) {
            size += c.size();
        }
        return size;
    }

    /**
     * Checks if two {@code EntranceGroup} have the same keys and the
     * {@code EntranceIcon} groups mapped to these keys are the same size
     * @param other
     * @return
     */
    public boolean areConnectable(EntranceGroup other) {
        for (var k : this.entrGrpng.keySet()) {
            var otherGrp = other.getEntrsByKey(k);
            if (otherGrp == null
                    || otherGrp.size() != this.entrGrpng.get(k).size() )
                return false;
        }

        return true;
    }

    public String getGroupName() {
        return this.groupName;
    }

    public boolean isDropPairing() { return this.dropPairing; }

    public EntranceGroup getConnectingGroup() {
        return this.connectingGroup;
    }

    /**
     * Iterates through all the Entrances and gets
     * @return
     *  -1 mismatching worlds
     *  0 no connections
     *  1 all entrances that do connect, lead to light world
     *  2 all entrances that do connect, lead to dark world
     */
    protected int getConnectingWorld() {
        int prev = 0;
        for (var k : entrGrpng.keySet()) {
            for (var entr : entrGrpng.get(k)) {
                if (entr.getConnection() == null) continue;

                var w = entr.getConnection().getWorld();
                if (prev < EntranceIcon.LIGHT)
                    prev = w;
                else if (prev != w)
                    return -1;
            }
        }
        return prev;
    }

    protected void setConnectingGroup(EntranceGroup eg) {
        this.connectingGroup = eg;
    }

    protected ArrayDeque<EntranceIcon> getEntrsByKey(String k) {
        return this.entrGrpng.get(k);
    }

    /*
     * ===========================================================================
     * Private Methods
     * ===========================================================================
     */

    /**
     * Check whether a given key, {@code k} is equal one of the four cardinal
     * directions
     * @param k Key to check
     * @return {@code true} if k is equal to one of {@code NORTH}, {@code EAST},
     * {@code WEST}, or {@code SOUTH}.
     */
    private boolean isViableKey(String k) {
        return (k.equals(NORTH) || k.equals(EAST)
                || k.equals(SOUTH) || k.equals(WEST));
    }

    /** String Constant for North */
    protected static final String NORTH = "N";

    /** String Constant for East */
    protected static final String EAST = "E";

    /** String Constant for South */
    protected static final String SOUTH = "S";

    /** String Constant for West */
    protected static final String WEST = "W";

    private String groupName;

    private boolean dropPairing;

    private EntranceGroup connectingGroup;

    /**
     * Map of an arraydeque of {@code EntranceIcon}s keyed by their entrance
     * type or direction.
     *
     * TODO investigate Entrance needs, this could just be a single entrance
     */
    private LinkedHashMap<String, ArrayDeque<EntranceIcon>> entrGrpng;
}
