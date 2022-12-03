package com.mycompany.entrances;
/**
 * Enum to track connection state of an individual icon
 *
 * Allows for decrementing and incrementing the state, with bounds checking
 * to stop at {@code USELESS} and {@code CONNECTED}
 * @author blarg
 */
public enum ConnectionState {
    USELESS {
        @Override
        public ConnectionState dec() {
            return this;
        }
    }
    ,UNKNOWN
    ,PENDING
    ,CONNECTED {
        @Override
        public ConnectionState inc() {
            return this;
        }
    };

    public ConnectionState inc() {
        return values()[ordinal() + 1];
    }

    public ConnectionState dec() {
        return values()[ordinal() - 1];
    }

    /**
     * Compares ordinal values of {@code a} and {@code b}
     * @param a
     * @param b
     * @return
     */
    public static ConnectionState min(ConnectionState a, ConnectionState b) {
        return a.ordinal() <= b.ordinal() ? a : b;
    }
};
