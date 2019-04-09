package com.tzpt.cloudlibrary.modle.remote.newdownload.core.dispatcher;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.tzpt.cloudlibrary.modle.remote.newdownload.DownloadListener;
import com.tzpt.cloudlibrary.modle.remote.newdownload.DownloadTask;
import com.tzpt.cloudlibrary.modle.remote.newdownload.PDownload;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.cause.EndCause;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created by Administrator on 2018/8/6.
 */

public class CallbackDispatcher {
    private static final String TAG = "CallbackDispatcher";

    // Just transmit to the main looper.
    private final DownloadListener transmit;

    private final Handler uiHandler;

    public CallbackDispatcher() {
        this.uiHandler = new Handler(Looper.getMainLooper());
        this.transmit = new DefaultTransmitListener(uiHandler);
    }

    public boolean isFetchProcessMoment(DownloadTask task) {
        final long minInterval = task.getMinIntervalMillisCallbackProcess();
        final long now = SystemClock.uptimeMillis();
        return minInterval <= 0
                || now - DownloadTask.TaskHideWrapper
                .getLastCallbackProcessTs(task) >= minInterval;
    }

    void endTasksWithCanceled(@NonNull final Collection<DownloadTask> canceledCollection, boolean del) {
        if (canceledCollection.size() <= 0) return;

        final Iterator<DownloadTask> iterator = canceledCollection.iterator();
        while (iterator.hasNext()) {
            final DownloadTask task = iterator.next();
            if (!task.isAutoCallbackToUIThread()) {
                if (!del) {
                    task.getListener().taskEnd(task, EndCause.CANCELED, null);
                } else {
                    PDownload.with().store().del(task.getUrl());
                }
                iterator.remove();
            }
        }
    }

    public DownloadListener dispatch() {
        return transmit;
    }

    static class DefaultTransmitListener implements DownloadListener {
        @NonNull
        private final Handler uiHandler;

        DefaultTransmitListener(@NonNull Handler uiHandler) {
            this.uiHandler = uiHandler;
        }

        @Override
        public void taskWait(@NonNull final DownloadTask task) {
            if (task.isAutoCallbackToUIThread()) {
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        task.getListener().taskWait(task);
                    }
                });
            } else {
                task.getListener().taskWait(task);
            }
        }

        @Override
        public void taskStart(@NonNull final DownloadTask task) {
//            inspectTaskStart(task);
            if (task.isAutoCallbackToUIThread()) {
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        task.getListener().taskStart(task);
                    }
                });
            } else {
                task.getListener().taskStart(task);
            }
        }

        @Override
        public void connectTrialStart(@NonNull final DownloadTask task) {
            if (task.isAutoCallbackToUIThread()) {
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        task.getListener().connectTrialStart(task);
                    }
                });
            } else {
                task.getListener().connectTrialStart(task);
            }
        }

        @Override
        public void connectTrialEnd(@NonNull final DownloadTask task) {
            if (task.isAutoCallbackToUIThread()) {
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        task.getListener().connectTrialEnd(task);
                    }
                });
            } else {
                task.getListener().connectTrialEnd(task);
            }
        }

        @Override
        public void connectStart(@NonNull final DownloadTask task) {
            if (task.isAutoCallbackToUIThread()) {
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        task.getListener().connectStart(task);
                    }
                });
            } else {
                task.getListener().connectStart(task);
            }
        }

        @Override
        public void connectEnd(@NonNull final DownloadTask task) {
            if (task.isAutoCallbackToUIThread()) {
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        task.getListener().connectEnd(task);
                    }
                });
            } else {
                task.getListener().connectEnd(task);
            }
        }

        @Override
        public void fetchStart(@NonNull final DownloadTask task) {
            if (task.isAutoCallbackToUIThread()) {
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        task.getListener().fetchStart(task);
                    }
                });
            } else {
                task.getListener().fetchStart(task);
            }
        }

        @Override
        public void fetchProgress(@NonNull final DownloadTask task) {
            if (task.getMinIntervalMillisCallbackProcess() > 0) {
                DownloadTask.TaskHideWrapper
                        .setLastCallbackProcessTs(task, SystemClock.uptimeMillis());
            }

            if (task.isAutoCallbackToUIThread()) {
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        task.getListener().fetchProgress(task);
                    }
                });
            } else {
                task.getListener().fetchProgress(task);
            }
        }

        @Override
        public void fetchEnd(@NonNull final DownloadTask task) {
            if (task.isAutoCallbackToUIThread()) {
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        task.getListener().fetchEnd(task);
                    }
                });
            } else {
                task.getListener().fetchEnd(task);
            }
        }


        @Override
        public void taskEnd(@NonNull final DownloadTask task, @NonNull final EndCause cause,
                            @Nullable final Exception realCause) {
            if (task.isAutoCallbackToUIThread()) {
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        task.getListener().taskEnd(task, cause, realCause);
                    }
                });
            } else {
                task.getListener().taskEnd(task, cause, realCause);
            }
        }
    }
}
