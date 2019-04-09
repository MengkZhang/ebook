package com.tzpt.cloudlibrary.zlibrary.text.view.sytle;

import com.tzpt.cloudlibrary.zlibrary.core.filesystem.ZLFile;
import com.tzpt.cloudlibrary.zlibrary.core.util.MiscUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 读取CSS文件的工具
 * Created by Administrator on 2017/4/8.
 */

public class SimpleCSSReader {
    private enum State {
        EXPECT_SELECTOR,
        EXPECT_OPEN_BRACKET,
        EXPECT_NAME,
        EXPECT_VALUE,
        READ_COMMENT,
    }

    private State mState;
    private State mSavedState;
    private Map<Integer, ZLTextNGStyleDescription> mDescriptionMap;
    private Map<String, String> mCurrentMap;
    private String mSelector;
    private String mName;

    Map<Integer, ZLTextNGStyleDescription> read(ZLFile file) {
        mDescriptionMap = new LinkedHashMap<>();
        mState = State.EXPECT_SELECTOR;

        InputStream stream = null;
        try {
            stream = file.getInputStream();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            while ((line = reader.readLine()) != null) {
                for (String token : MiscUtil.smartSplit(line)) {
                    processToken(token);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return mDescriptionMap;
    }

    private void processToken(String token) {
        if (mState != State.READ_COMMENT && token.startsWith("/*")) {
            mSavedState = mState;
            mState = State.READ_COMMENT;
            return;
        }

        switch (mState) {
            case READ_COMMENT:
                if (token.endsWith("*/")) {
                    mState = mSavedState;
                }
                break;
            case EXPECT_SELECTOR:
                mSelector = token;
                mState = State.EXPECT_OPEN_BRACKET;
                break;
            case EXPECT_OPEN_BRACKET:
                if ("{".equals(token)) {
                    mCurrentMap = new HashMap<>();
                    mState = State.EXPECT_NAME;
                }
                break;
            case EXPECT_NAME:
                if ("}".equals(token)) {
                    if (mSelector != null) {
                        try {
                            mDescriptionMap.put(
                                    Integer.valueOf(mCurrentMap.get("fbreader-id")),
                                    new ZLTextNGStyleDescription(mSelector, mCurrentMap)
                            );
                        } catch (Exception e) {
                            // ignore
                        }
                    }
                    mState = State.EXPECT_SELECTOR;
                } else {
                    mName = token;
                    mState = State.EXPECT_VALUE;
                }
                break;
            case EXPECT_VALUE:
                if (mCurrentMap != null && mName != null) {
                    mCurrentMap.put(mName, token);
                }
                mState = State.EXPECT_NAME;
                break;
        }
    }
}
