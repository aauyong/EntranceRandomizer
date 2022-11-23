package com.mycompany.mapimagepnl;

import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A container for {@code EntranceIcons} that represents a grouping of
 *
 * @author aauyong
 */
public class EntranceGroup {

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
     * the grouping is not the same, or the groups do not line up
     */
    public boolean connectToGroup(EntranceGroup other) {
        if (!this.groupingType.equals(other.getGroupingType())) {
            return false;
        }

        if (this.groupingType.equals(DIRECTION)) {
            return groupByDirection(other);
        }

        return groupByType(other);
    }

    /**
     * wrapper for adding elements
     * @param e
     * @param dirOpt
     * @return
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

    public String getGroupingType() {
        return this.groupingType;
    }

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
    private boolean addDirEntrance(String dir, EntranceIcon e) {
        if (this.dirGrpng.containsKey(dir)) {
            this.dirGrpng.put(dir, e);
            return true;
        }
        return false;
    }

    private boolean addDirectionLessEntrance(String type, EntranceIcon e) {
        if (!this.typeGrpng.containsKey(type))
            this.typeGrpng.put(type, new ArrayList<>());

        this.typeGrpng.get(type).add(e);
        return true;
    }

    private boolean groupByDirection(EntranceGroup other) {
        for (var d : List.of(NORTH, EAST, SOUTH, WEST)) {
            EntranceIcon a = this.dirGrpng.get(d);
            EntranceIcon b = other.getDir(d);

            if (!a.setConnection(b) || !b.setConnection(a))
                return false;
        }

        return true;
    }

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

    protected static String NORTH = "N";
    protected static String EAST = "E";
    protected static String SOUTH = "S";
    protected static String WEST = "W";
    protected static String DIRECTION = "direction";
    protected static String TYPE = "type";
    protected String groupingType;
    private HashMap<String, EntranceIcon> dirGrpng;
    private HashMap<String, List<EntranceIcon> > typeGrpng;

}
