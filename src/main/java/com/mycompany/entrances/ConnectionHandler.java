package com.mycompany.entrances;

import java.util.ArrayDeque;
import java.util.Deque;

import com.mycompany.entrancerando.SettingsHandler;
import com.mycompany.entrancerando.SettingsHandler.Grouping;


/**
 * A Singleton handler for parsing the logic and control of the deletion and
 * connection of various EntranceIcons and EntranceGroups
 * @author aauyong
 */
public class ConnectionHandler {

    public static ConnectionHandler createInstance() {
        if (SINGLETON == null)
            SINGLETON = new ConnectionHandler();
        return SINGLETON;
    }

    /**
     * Checks if two {@code EntranceIcon}s are connectable according to the
     * currently set Grouping Settings. <p>
     *
     * There are five options for grouping, as defined by name in
     * {@code SettingsHandler}, each with their own logical checks
     * <ul>
     *  <li> SIMPLE :: The most restrictive setting
     *      <ul>
     *          <li> Single Entrances connect to singles, and multis connect
     *          to multis
     *          <li> Separate dungeon Entrances from Non-Dungeons Entrances
     *          <li> Multi Entrances are connected in the same world
     *          <li> Skull Woods front remains Skull Woods front
     *          <li> Light world death mountain stays in Light World DM
     *      </ul>
     *  <li> RESTRICTED ::
     *      <ul>
     *          <li> Non-Dungeons Singles and Multis are now mixed (including LW
     *          DM)
     *          <li> Separate dungeon Entrances from Non-Dungeons Entrances
     *          <li> Multi Entrances are connected in the same world
     *          <li> Skull Woods front remains Skull Woods front
     *      </ul>
     *  <li> FULL ::
     *      <ul>
     *          <li> Dungeon and Non-Dungeons Entrances are now mixed
     *          <li> Single and Multi entrances are now mixed
     *          <li> Multi Entrances are connected in the same world
     *      </ul>
     *  <li> CROSSED ::
     *      <ul>
     *          <li> Dungeon and Non-Dungeons Entrances are now mixed
     *          <li> Single and Multi entrances are now mixed
     *          <li> Multi Entrances can now cross worlds
     *      </ul>
     * </ul>
     * @param a
     * @param b
     * @return
     */
    public static boolean areConnectable(EntranceIcon a, EntranceIcon b) {
        if (a == b)
            return true;
        // TODO implement insanity settings
        // SIMPLE

        switch (currGroupSettings) {
            case SIMPLE -> {
                // must be same type
                if (!a.getEntrType().equals(b.getEntrType()))
                    return false;

                // must be group to group
                if (a.hasGrouping() != b.hasGrouping())
                    return false;


                // If LW DM, must connect ot LW DM

                // If Skull Woods, must connect to Skull Woods
            }
            case RESTRICTED -> {

                // must be same type
                if (!a.getEntrType().equals(b.getEntrType()))
                    return false;

                /* Dungeons must match single-single multi-multi */
                if (a.getEntrType().equals(EntranceIcon.DUNGEON)) {
                    // must be group to group
                    if (a.hasGrouping() != b.hasGrouping())
                        return false;
                }
                else {
                    if (a.hasGrouping() && !leadsToSameWorldAsGroup(a.getGroup(), b))
                        return false;

                }

                // if multi, stay in same world

                // If LW DM, must connect ot LW DM

                // If Skull Woods, must connect to Skull Woods
            }
            case FULL -> {
                // If a or b is a drop, they both avhe to be drops
                if (a.getEntrType().equals(EntranceIcon.DROP)
                        || b.getEntrType().equals(EntranceIcon.DROP)) {

                    if (!a.getEntrType().equals(b.getEntrType()))
                            return false;
                }
                // if they're both drops or neither, then can connect
            }
            case CROSSED -> {
                // If a or b is a drop, they both avhe to be drops
                if (a.getEntrType().equals(EntranceIcon.DROP)
                        || b.getEntrType().equals(EntranceIcon.DROP)) {

                    if (!a.getEntrType().equals(b.getEntrType()))
                            return false;
                }
            }
            default -> { return false; }
        }
        return true;
    }

    //TODO may not be necessary
    public static void updateGroupSettings(Grouping newGroupSettings) {
        currGroupSettings = newGroupSettings;

        // if (newGroupSettings.ordinal() == Grouping.INSANITY.ordinal()) {
        //     setEverythingTo(false);
        //     return;
        // }

        // setEverythingTo(true);

        // if (newGroupSettings.ordinal() >= Grouping.RESTRICTED.ordinal()) {
        //     RESTRICT_SINGLE_ENTR_CAVES = false;
        //     RESTRICT_MULTI_ENTR_CAVES = false;
        //     RESTRICT_LW_DM = false;
        // }
        // if (newGroupSettings.ordinal() >= Grouping.FULL.ordinal()) {
        //     RESTRICT_SINGLE_ENTR_DUNGS = false;
        //     RESTRICT_MULTI_ENTR_DUNGS = false;
        // }
        // if (newGroupSettings.ordinal() >= Grouping.CROSSED.ordinal()) {
        //     RESTRICT_MULTI_ENTR_WORLD = false;
        // }
    }

    /**
     * Set a connection between {@code a} and {@code b}.
     * @param a
     * @param b
     * @return {@code true} if the connection is successfully created, else
     * {@code false}
     */
    public static Deque<EntranceIcon[]> tryConnect(EntranceIcon a, EntranceIcon b) {
        // TODO implement one way connections

        // Temporary one-way boolean variable
        boolean biDirectional = true;

        if (a == b) {
            a.setConnection(a);
            return new ArrayDeque<>(){{ add(new EntranceIcon[]{a,a});  }};
        }

        if (shouldConnectByGroup(a,b))
            return connectByGroup(a.getGroup(), b.getGroup(), biDirectional);

        if (a.setConnection(b)) {
            if (biDirectional) b.setConnection(a);
            return new ArrayDeque<>(){{ add(new EntranceIcon[]{a,b}); }};
        }
        return null;
    }

    /* ===========================================================================
    * PRIVATE METHODS
    * ===========================================================================*/

    private ConnectionHandler() {
        currGroupSettings = SettingsHandler.DEFAULT_GROUPING;
    }

    /**
     * Checks whether the two icons should be treated as groups and connected
     * as such given the current settings
     *
     * If both aren't groups, then they cannot be connected as groups. <p>
     *
     * If the setting is SIMPLE, always connect as a group. <p>
     *
     * If the setting is INSANITY, never connect as a group. <p>
     *
     * If the setting is CROSSED, FULL, or RESTRICTED, we connect them by groups
     * if they are both drop pairings. <p>
     *
     * If the setting is RESTRICTED, we connect them if they are both dungeons
     * @param a
     * @param b
     * @return
     */
    private static boolean shouldConnectByGroup(EntranceIcon a, EntranceIcon b) {
        if (a.hasGrouping() && b.hasGrouping()) {

            if (currGroupSettings == Grouping.SIMPLE)
                return true;
            else if (currGroupSettings == Grouping.INSANITY)
                return false;

            if (currGroupSettings.lessThan(Grouping.INSANITY)) {
                if (a.getGroup().isDropPairing() && b.getGroup().isDropPairing())
                    return true;
            }

            if (currGroupSettings == Grouping.RESTRICTED) {
                if (areBothOf(EntranceIcon.DUNGEON, a, b));
            }
        }
        return false;
    }

    private static Deque<EntranceIcon[]> connectByGroup(EntranceGroup aG,
            EntranceGroup bG, boolean biDirectional) {
        if (biDirectional) { bG.tryConnToGroup(aG); }
        return aG.tryConnToGroup(bG);
    }

    private static boolean areBothOf(String of, EntranceIcon a, EntranceIcon b) {
        boolean oneIsDrop =  a.getEntrType().equals(of)
                || b.getEntrType().equals(of);

        return oneIsDrop && a.getEntrType().equals(b.getEntrType());
    }

    /**
     *
     * @param grp
     * @param dest
     * @return
     */
    private static boolean leadsToSameWorldAsGroup(EntranceGroup grp,
            EntranceIcon dest) {
        int groupWorld = grp.getConnectingWorld();
        return groupWorld == 0 || dest.getWorld() == groupWorld;
    }

    private static Grouping currGroupSettings;

    private static ConnectionHandler SINGLETON;
}
