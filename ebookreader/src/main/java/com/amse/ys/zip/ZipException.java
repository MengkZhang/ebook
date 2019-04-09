package com.amse.ys.zip;

import java.io.IOException;

/**
 * Created by Administrator on 2017/4/7.
 */

public class ZipException extends IOException {
    ZipException(String message) {
        super(message);
    }
}
