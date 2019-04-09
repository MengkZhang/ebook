package com.tzpt.cloudlibrary.modle.remote.newdownload;

import android.os.SystemClock;

import com.tzpt.cloudlibrary.modle.remote.newdownload.core.Util;

/**
 * Created by Administrator on 2018/8/14.
 */

public class SpeedCalculator {

    private long timestamp;
    private long increaseBytes;

    private long bytesPerSecond;

    private long beginTimestamp;
    private long endTimestamp;
    private long allIncreaseBytes;

    public synchronized void reset() {
        timestamp = 0;
        increaseBytes = 0;
        bytesPerSecond = 0;

        beginTimestamp = 0;
        endTimestamp = 0;
        allIncreaseBytes = 0;
    }

    private long nowMillis() {
        return SystemClock.uptimeMillis();
    }

    public synchronized void downloading(long increaseBytes) {
        if (timestamp == 0) {
            this.timestamp = nowMillis();
            this.beginTimestamp = timestamp;
        }

        this.increaseBytes += increaseBytes;
        this.allIncreaseBytes += increaseBytes;
    }

    private synchronized void flush() {
        final long nowMillis = nowMillis();
        final long sinceNowIncreaseBytes = increaseBytes;
        final long durationMillis = Math.max(1, nowMillis - timestamp);

        increaseBytes = 0;
        timestamp = nowMillis;

        // precision loss
        bytesPerSecond = (long) ((float) sinceNowIncreaseBytes / durationMillis * 1000f);
    }

    /**
     * Get instant bytes per-second.
     */
    private long getInstantBytesPerSecondAndFlush() {
        flush();
        return bytesPerSecond;
    }

    /**
     * Get bytes per-second and only if duration is greater than or equal to 1 second will flush and
     * re-calculate speed.
     */
    private synchronized long getBytesPerSecondAndFlush() {
        final long interval = nowMillis() - timestamp;
        if (interval < 1000 && bytesPerSecond != 0) return bytesPerSecond;

        // the first time we using 500 milliseconds to let speed valid more quick
        if (bytesPerSecond == 0 && interval < 500) return 0;

        return getInstantBytesPerSecondAndFlush();
    }

    private synchronized long getBytesPerSecondFromBegin() {
        final long endTimestamp = this.endTimestamp == 0 ? nowMillis() : this.endTimestamp;
        final long sinceNowIncreaseBytes = allIncreaseBytes;
        final long durationMillis = Math.max(1, endTimestamp - beginTimestamp);

        // precision loss
        return (long) ((float) sinceNowIncreaseBytes / durationMillis * 1000f);
    }

    public synchronized void endTask() {
        endTimestamp = nowMillis();
    }

    /**
     * Get instant speed
     */
    public String instantSpeed() {
        return getSpeedWithSIAndFlush();
    }

    /**
     * Get speed with at least one second duration.
     */
    public String speed() {
        return humanReadableSpeed(getBytesPerSecondAndFlush());
    }

    /**
     * Get last time calculated speed.
     */
    public String lastSpeed() {
        return humanReadableSpeed(bytesPerSecond);
    }

    public synchronized long getInstantSpeedDurationMillis() {
        return nowMillis() - timestamp;
    }


    /**
     * With wikipedia: https://en.wikipedia.org/wiki/Kibibyte
     * <p>
     * 1KiB = 2^10B = 1024B
     * 1MiB = 2^10KB = 1024KB
     */
    public String getSpeedWithBinaryAndFlush() {
        return humanReadableSpeed(getInstantBytesPerSecondAndFlush());
    }

    /**
     * With wikipedia: https://en.wikipedia.org/wiki/Kilobyte
     * <p>
     * 1KB = 1000B
     * 1MB = 1000KB
     */
    public String getSpeedWithSIAndFlush() {
        return humanReadableSpeed(getInstantBytesPerSecondAndFlush());
    }

    public String averageSpeed() {
        return speedFromBegin();
    }

    public String speedFromBegin() {
        return humanReadableSpeed(getBytesPerSecondFromBegin());
    }

    private static String humanReadableSpeed(long bytes) {
        return Util.humanReadableBytes(bytes) + "/s";
    }
}
