package com.tzpt.cloudlibrary.modle.local;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.Map;
import java.util.Set;

/**
 * SP存储接口
 * Created by Administrator on 2017/5/22.
 */

public class SharedPreferencesUtil {

    private static SharedPreferencesUtil mInstance;
    private SharedPreferences mSPre;
    private SharedPreferences.Editor mEditor;

    public synchronized static SharedPreferencesUtil getInstance() {
        return mInstance;
    }

    @SuppressLint("CommitPrefEdits")
    public static void init(Context context, String prefsname, int mode) {
        mInstance = new SharedPreferencesUtil();
        mInstance.mSPre = context.getSharedPreferences(prefsname, mode);
        mInstance.mEditor = mInstance.mSPre.edit();
    }

    private SharedPreferencesUtil() {
    }


    public boolean getBoolean(String key, boolean defaultVal) {
        return this.mSPre.getBoolean(key, defaultVal);
    }

    public boolean getBoolean(String key) {
        return this.mSPre.getBoolean(key, false);
    }


    public String getString(String key, String defaultVal) {
        return this.mSPre.getString(key, defaultVal);
    }

    public String getString(String key) {
        return this.mSPre.getString(key, null);
    }

    public int getInt(String key, int defaultVal) {
        return this.mSPre.getInt(key, defaultVal);
    }

    public int getInt(String key) {
        return this.mSPre.getInt(key, 0);
    }


    public float getFloat(String key, float defaultVal) {
        return this.mSPre.getFloat(key, defaultVal);
    }

    public float getFloat(String key) {
        return this.mSPre.getFloat(key, 0f);
    }

    public long getLong(String key, long defaultVal) {
        return this.mSPre.getLong(key, defaultVal);
    }

    public long getLong(String key) {
        return this.mSPre.getLong(key, 0l);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public Set<String> getStringSet(String key, Set<String> defaultVal) {
        return this.mSPre.getStringSet(key, defaultVal);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public Set<String> getStringSet(String key) {
        return this.mSPre.getStringSet(key, null);
    }

    public Map<String, ?> getAll() {
        return this.mSPre.getAll();
    }

    public boolean exists(String key) {
        return mSPre.contains(key);
    }


    public SharedPreferencesUtil putString(String key, String value) {
        mEditor.putString(key, value);
        mEditor.commit();
        return this;
    }

    public SharedPreferencesUtil putInt(String key, int value) {
        mEditor.putInt(key, value);
        mEditor.commit();
        return this;
    }

    public SharedPreferencesUtil putFloat(String key, float value) {
        mEditor.putFloat(key, value);
        mEditor.commit();
        return this;
    }

    public SharedPreferencesUtil putLong(String key, long value) {
        mEditor.putLong(key, value);
        mEditor.commit();
        return this;
    }

    public SharedPreferencesUtil putBoolean(String key, boolean value) {
        mEditor.putBoolean(key, value);
        mEditor.commit();
        return this;
    }

    public void commit() {
        mEditor.commit();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public SharedPreferencesUtil putStringSet(String key, Set<String> value) {
        mEditor.putStringSet(key, value);
        mEditor.commit();
        return this;
    }

    public void putObject(String key, Object object) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(baos);
            out.writeObject(object);
            String objectVal = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
            mEditor.putString(key, objectVal);
            mEditor.commit();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public <T> T getObject(String key, Class<T> clazz) {
        if (mSPre.contains(key)) {
            String objectVal = mSPre.getString(key, null);
            byte[] buffer = Base64.decode(objectVal, Base64.DEFAULT);
            ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(bais);
                T t = (T) ois.readObject();
                return t;
            } catch (StreamCorruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bais != null) {
                        bais.close();
                    }
                    if (ois != null) {
                        ois.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public SharedPreferencesUtil remove(String key) {
        mEditor.remove(key);
        mEditor.commit();
        return this;
    }

    public SharedPreferencesUtil removeAll() {
        mEditor.clear();
        mEditor.commit();
        return this;
    }
}
