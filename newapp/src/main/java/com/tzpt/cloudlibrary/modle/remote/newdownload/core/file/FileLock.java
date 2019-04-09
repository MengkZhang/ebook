package com.tzpt.cloudlibrary.modle.remote.newdownload.core.file;

import android.support.annotation.NonNull;

import com.tzpt.cloudlibrary.modle.remote.newdownload.core.Util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

/**
 * Created by Administrator on 2018/8/6.
 */

public class FileLock {
    private static final String TAG = "FileLock";

    @NonNull
    private final Map<String, AtomicInteger> fileLockCountMap;
    @NonNull
    private final Map<String, Thread> waitThreadForFileLockMap;

    private FileLock(@NonNull Map<String, AtomicInteger> fileLockCountMap,
                     @NonNull Map<String, Thread> waitThreadForFileLockMap) {
        this.fileLockCountMap = fileLockCountMap;
        this.waitThreadForFileLockMap = waitThreadForFileLockMap;
    }

    FileLock() {
        this(new HashMap<String, AtomicInteger>(), new HashMap<String, Thread>());
    }

    void increaseLock(@NonNull String path) {
        AtomicInteger lockCount;
        synchronized (fileLockCountMap) {
            lockCount = fileLockCountMap.get(path);
        }
        if (lockCount == null) {
            lockCount = new AtomicInteger(0);
            synchronized (fileLockCountMap) {
                fileLockCountMap.put(path, lockCount);
            }
        }
        Util.d(TAG, "increaseLock increase lock-count to " + lockCount.incrementAndGet() + path);
    }

    void decreaseLock(@NonNull String path) {
        AtomicInteger lockCount;
        synchronized (fileLockCountMap) {
            lockCount = fileLockCountMap.get(path);
        }

        if (lockCount != null && lockCount.decrementAndGet() == 0) {
            Util.d(TAG, "decreaseLock decrease lock-count to 0 " + path);
            final Thread lockedThread;
            synchronized (waitThreadForFileLockMap) {
                lockedThread = waitThreadForFileLockMap.get(path);
                if (lockedThread != null) {
                    waitThreadForFileLockMap.remove(path);
                }
            }

            if (lockedThread != null) {
                Util.d(TAG, "decreaseLock " + path + " unpark locked thread " + lockCount);
                LockSupport.unpark(lockedThread);
            }
            synchronized (fileLockCountMap) {
                fileLockCountMap.remove(path);
            }
        }
    }

    private static final long WAIT_RELEASE_LOCK_NANO = TimeUnit.MILLISECONDS.toNanos(100);

    public void waitForRelease(@NonNull String filePath) {
        AtomicInteger lockCount;
        synchronized (fileLockCountMap) {
            lockCount = fileLockCountMap.get(filePath);
        }
        if (lockCount == null || lockCount.get() <= 0) return;

        synchronized (waitThreadForFileLockMap) {
            waitThreadForFileLockMap.put(filePath, Thread.currentThread());
        }
        Util.d(TAG, "waitForRelease start " + filePath);
        while (true) {
            if (lockCount.get() <= 0)
                break;
            LockSupport.park(WAIT_RELEASE_LOCK_NANO);
        }
        Util.d(TAG, "waitForRelease finish " + filePath);
    }
}
