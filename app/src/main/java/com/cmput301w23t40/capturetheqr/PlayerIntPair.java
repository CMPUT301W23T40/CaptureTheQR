package com.cmput301w23t40.capturetheqr;

/**
 * This class is for containing the statistics associated with users,
 * e.g., highest code score, number of codes, code sum
 */
public class PlayerIntPair {
    private Player player;
    private int integer;

    public PlayerIntPair(Player player, int integer) {
        this.player = player;
        this.integer = integer;
    }

    public Player getPlayer() {
        return player;
    }

    /**
     * Returns an integer, which could represent highest code score, number of codes, code sum,
     * depending on the function called
     * @return integer
     */
    public int getInteger() {
        return integer;
    }
}
