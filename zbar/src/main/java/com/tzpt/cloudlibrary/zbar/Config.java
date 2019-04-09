package com.tzpt.cloudlibrary.zbar;

/**
 * Decoder configuration options.
 * Created by Administrator on 2018/10/16.
 */

public class Config {
    /**
     * Enable symbology/feature.
     */
    public static final int ENABLE = 0;
    /**
     * Enable check digit when optional.
     */
    public static final int ADD_CHECK = 1;
    /**
     * Return check digit when present.
     */
    public static final int EMIT_CHECK = 2;
    /**
     * Enable full ASCII character set.
     */
    public static final int ASCII = 3;

    /**
     * Minimum data length for valid decode.
     */
    public static final int MIN_LEN = 0x20;
    /**
     * Maximum data length for valid decode.
     */
    public static final int MAX_LEN = 0x21;

    /**
     * Required video consistency frames.
     */
    public static final int UNCERTAINTY = 0x40;

    /**
     * Enable scanner to collect position data.
     */
    public static final int POSITION = 0x80;

    /**
     * Image scanner vertical scan density.
     */
    public static final int X_DENSITY = 0x100;
    /**
     * Image scanner horizontal scan density.
     */
    public static final int Y_DENSITY = 0x101;
}
