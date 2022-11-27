package com.mycompany.mapimagepnl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A container for {@code EntranceIcons} that represents a grouping of entrances
 * 
 * Contains two hashmaps, one that keys entrances to a direction and one 
 * that keys entrances to a list of the type of the entrances
 * 
 *  
 * TODO implement group deletion
 * 
 * TODO parse this down to a single hashmap, and instead just denote the type
 * TODO this only works if the type grouping map can produce a single entrance instead of a full list
 * @author aauyong
 */
public class EntranceGroup {

    /**
     * Constructor. Initializes both maps, inserting keys for the four cardinal
     * directions into the direction grouping.
     */
    public EntranceGroup() {
        this.dirGrpng = new HashMap<>(){{
            put(NORTH, null);
            put(EAST, null);
            put(WEST, null);
            put(SOUTH, null);
        }};
        this.typeGrpng = new HashMap<>();
    }

    /**
     * Attempts to connect two groups together. This and {@code other} must be
     * of the same grouping type, either using directional grouping or
     * type grouping.
     * @param other
     * @return
     *      {@code true} :: if the grouping connection is successful
     *      {@code false} :: there are multiple failure conditions. Either
     *      the grouping is not the same, or the groups do not line up
     * 
     * @see #groupByDirection(EntranceGroup)
     * @see #groupByType(EntranceGroup)
     */
    public boolean connectToGroup(EntranceGroup other) {
        if ( !this.groupingType.equals(other.getGroupingType()) )
            return false;
    

        if (this.groupingType.equals(DIRECTION))
            return groupByDirection(other);

        return groupByType(other);
    }

    

    /**
     * wrapper for adding elements to the group
     * 
     * Adds an Entrance based on its entrance type, or if a {@code dirOpt} is 
     * provided, then it will be keyed by that value.
     * @param e Entrance to add
     * @param dirOpt Cardinal direction to key the entrance by
     * @return 
     *      {@code true} :: if adding is successful
     *      {@code false} :: if adding fails
     * 
     * @see #addDirEntrance(String, EntranceIcon)
     * @see #addDirectionLessEntrance(String, EntranceIcon)
     */
    public boolean add(EntranceIcon e, String... dirOpt) {
        if ( dirOpt.length > 0 && !dirOpt[0].equals("null") ){
            this.groupingType = DIRECTION;
            return addDirEntrance(dirOpt[0], e);
        }
        else {
            this.groupingType = TYPE;
            return addDirectionLessEntrance(e.getEntrType(), e);
        }
    }

    /** Getter for the type of the grouping */
    public String getGroupingType() {
        return this.groupingType;
    }

    public int getSize() {
        return this.dirGrpng.size() + this.typeGrpng.size();
    }

    public boolean areConnectable(EntranceGroup other) {
        return this.getSize() == other.getSize() 
                && this.groupingType.equals(other.getGroupingType());
    }

    /** 
     * @param d Either {@code NORTH}, {@code EAST}, {@code WEST}, or {@code SOUTH}
     * @return {@code EntranceIcon} that correlates to the Direction
    */
    protected EntranceIcon getDir(String d) {
        if (this.dirGrpng.containsKey(d)) {
            return this.dirGrpng.get(d);
        }
        return null;
    }

    protected List<EntranceIcon> getByTypes(String type) {
        if (this.typeGrpng.containsKey(type)) {
            return this.typeGrpng.get(type);
        }

        return null;
    }

    /*
     * ===========================================================================
     * Private Methods
     * ===========================================================================
     */

    /**
     * Add an {@code EntranceIcon} to the directional map keyed by the cardinal 
     * direction 
     * @param dir
     * @param e
     * @return
     *      {@code true} :: if the provided {@code dir} is one of the four cardinal 
     *                      directions 
     */
    private boolean addDirEntrance(String dir, EntranceIcon e) {
        if (this.dirGrpng.containsKey(dir)) {
            this.dirGrpng.put(dir, e);
            return true;
        }
        return false;
    }

    /**
     * Add an {@code EntranceIcon} to the directional map keyed by the type. If the 
     * type does not exist as a key, then a new list is created for the type
     * @param type
     * @param e
     * @return {@code true}
     */
    private boolean addDirectionLessEntrance(String type, EntranceIcon e) {
        if (!this.typeGrpng.containsKey(type))
            this.typeGrpng.put(type, new ArrayList<>());

        this.typeGrpng.get(type).add(e);
        return true;
    }

    /**
     * Connects groups {@code this} and {@code other} together by their cardinal
     * direction mapping.
     * 
     * Due to the nature of the connections, group may be partially connected
     * @param other EntranceGroup to connect to
     * @return 
     *      {@code true} :: if {@code this} and {@code other} are fully connected
     *      {@code false} :: if any individual connection fails
     */
    private boolean groupByDirection(EntranceGroup other) {
        for (var d : List.of(NORTH, EAST, SOUTH, WEST)) {
            EntranceIcon a = this.dirGrpng.get(d);
            EntranceIcon b = other.getDir(d);

            if (!a.setConnection(b) || !b.setConnection(a))
                return false;
        }

        return true;
    }

    /**
     * Connects groups {@code this} and {@code other} together by their typing.
     * @param other
     * @return
     */
    private boolean groupByType(EntranceGroup other) {
        for( var t : this.typeGrpng.keySet()) {
            var groupA = this.typeGrpng.get(t);
            var groupB = other.getByTypes(t);

            if (groupA.size() != groupB.size()) return false;

            for (int i = 0; i < groupA.size(); i++) {
                var a = groupA.get(i);
                var b = groupB.get(i);
                if (!a.setConnection(b) || !b.setConnection(a))
                    return false;
            }
        }
        return true;
    }

    /** String Constant for North */
    protected static final String NORTH = "N";

    /** String Constant for East */
    protected static final String EAST = "E";

    /** String Constant for South */
    protected static final String SOUTH = "S";

    /** String Constant for West */
    protected static final String WEST = "W";

    /** String Constant for the Direcional grouping type */
    protected static final String DIRECTION = "direction";

    /** String Constant for the Type grouping type*/
    protected static final String TYPE = "type";

    /** Grouping type of this Group */
    protected String groupingType;

    /** Directional map for grouping entrances */
    private HashMap<String, EntranceIcon> dirGrpng;

    /** Type map for grouping entrances */
    private HashMap<String, List<EntranceIcon> > typeGrpng;
}
