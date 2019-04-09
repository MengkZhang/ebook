package com.tzpt.cloudlibrary.zbar;

/**
 * Decoder symbology modifiers.
 *
 * Created by Administrator on 2018/10/16.
 */

public class Modifier {
    /**
     * barcode tagged as GS1 (EAN.UCC) reserved
     * (eg, FNC1 before first data character).
     * data may be parsed as a sequence of GS1 AIs
     */
    public static final int GS1 = 0;

    /**
     * barcode tagged as AIM reserved
     * (eg, FNC1 after first character or digit pair)
     */
    public static final int AIM = 1;
}
