package com.tzpt.cloudlibrary.zlibrary.core.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2017/4/7.
 */

public interface InputStreamHolder {
    InputStream getInputStream() throws IOException;
}
