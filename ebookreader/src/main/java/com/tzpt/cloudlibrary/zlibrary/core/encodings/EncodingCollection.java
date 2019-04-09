package com.tzpt.cloudlibrary.zlibrary.core.encodings;

import java.util.List;

/**
 * Created by Administrator on 2017/4/8.
 */

public abstract class EncodingCollection {
    public abstract List<Encoding> encodings();
    public abstract Encoding getEncoding(String alias);
    public abstract Encoding getEncoding(int code);
}
