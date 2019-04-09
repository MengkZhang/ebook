package com.tzpt.cloudlibrary.zbar;

/**
 * Decoded symbol coarse orientation.
 *
 * Created by Administrator on 2018/10/16.
 */

public class Orientation {
    /**
     * Unable to determine orientation.
     */
    public static final int UNKNOWN = -1;
    /**
     * Upright, read left to right.
     */
    public static final int UP = 0;
    /**
     * sideways, read top to bottom
     */
    public static final int RIGHT = 1;
    /**
     * upside-down, read right to left
     */
    public static final int DOWN = 2;
    /**
     * sideways, read bottom to top
     */
    public static final int LEFT = 3;
}
