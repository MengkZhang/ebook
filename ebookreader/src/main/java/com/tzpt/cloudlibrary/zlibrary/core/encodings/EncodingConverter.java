package com.tzpt.cloudlibrary.zlibrary.core.encodings;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;

/**
 * Created by Administrator on 2017/4/8.
 */

public class EncodingConverter {
    public final String Name;
    private CharsetDecoder myDecoder;

    EncodingConverter(String encoding) {
        Name = encoding;
        myDecoder = Charset.forName(encoding).newDecoder()
                .onMalformedInput(CodingErrorAction.REPLACE)
                .onUnmappableCharacter(CodingErrorAction.REPLACE);
    }

    // we assume out is large enough for this conversion
    // returns number of filled chars in out buffer
    public int convert(byte[] in, int inOffset, int inLength, char[] out) {
        final ByteBuffer inBuffer = ByteBuffer.wrap(in, inOffset, inLength);
        final CharBuffer outBuffer = CharBuffer.wrap(out, 0, out.length);
        myDecoder.decode(inBuffer, outBuffer, false);
        return outBuffer.position();
    }

    public void reset() {
        myDecoder.reset();
    }
}
