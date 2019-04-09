package com.tzpt.cloudlibrary.modle.remote.newdownload;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.tzpt.cloudlibrary.modle.remote.newdownload.core.Util;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.breakpoint.DownloadStore;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.breakpoint.DownloadStoreOnDB;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.connection.DownloadConnection;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.dispatcher.CallbackDispatcher;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.dispatcher.DownloadDispatcher;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.download.DownloadStrategy;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.file.DownloadOutputStream;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.file.DownloadUriOutputStream;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.file.ProcessFileStrategy;

/**
 * Created by Administrator on 2018/8/6.
 */

public class PDownload {
    @SuppressLint("StaticFieldLeak")
    private static volatile PDownload singleton;

    private final DownloadDispatcher downloadDispatcher;
    private final CallbackDispatcher callbackDispatcher;
    private final DownloadStore mStore;
    private final DownloadConnection.Factory connectionFactory;
    private final DownloadOutputStream.Factory outputStreamFactory;
    private final ProcessFileStrategy processFileStrategy;
    private final DownloadStrategy downloadStrategy;

    private final Context context;

    @Nullable
    private DownloadMonitor monitor;

    PDownload(Context context, DownloadDispatcher downloadDispatcher,
              CallbackDispatcher callbackDispatcher, DownloadStore store,
              DownloadConnection.Factory connectionFactory,
              DownloadOutputStream.Factory outputStreamFactory,
              ProcessFileStrategy processFileStrategy, DownloadStrategy downloadStrategy) {
        this.context = context;
        this.downloadDispatcher = downloadDispatcher;
        this.callbackDispatcher = callbackDispatcher;
        this.connectionFactory = connectionFactory;
        this.outputStreamFactory = outputStreamFactory;
        this.processFileStrategy = processFileStrategy;
        this.downloadStrategy = downloadStrategy;
        this.mStore = store;
    }


    public DownloadDispatcher downloadDispatcher() {
        return downloadDispatcher;
    }

    public CallbackDispatcher callbackDispatcher() {
        return callbackDispatcher;
    }

    public DownloadConnection.Factory connectionFactory() {
        return connectionFactory;
    }

    public DownloadOutputStream.Factory outputStreamFactory() {
        return outputStreamFactory;
    }

    public ProcessFileStrategy processFileStrategy() {
        return processFileStrategy;
    }

    public DownloadStrategy downloadStrategy() {
        return downloadStrategy;
    }

    public DownloadStore store() {
        return mStore;
    }

    public Context context() {
        return this.context;
    }

    void setMonitor(@Nullable DownloadMonitor monitor) {
        this.monitor = monitor;
    }

    @Nullable
    public DownloadMonitor getMonitor() {
        return monitor;
    }

    public static PDownload with() {
        if (singleton == null) {
            synchronized (PDownload.class) {
                if (singleton == null) {
                    if (PDownloadProvider.context == null) {
                        throw new IllegalStateException("context == null");
                    }
                    singleton = new Builder(PDownloadProvider.context).build();
                }
            }
        }
        return singleton;
    }

//    public static void setSingletonInstance(@NonNull PDownload okDownload) {
//        if (singleton != null) {
//            throw new IllegalArgumentException(("OkDownload must be null."));
//        }
//
//        synchronized (PDownload.class) {
//            if (singleton != null) {
//                throw new IllegalArgumentException(("OkDownload must be null."));
//            }
//            singleton = okDownload;
//        }
//    }

    public static class Builder {
        private DownloadDispatcher downloadDispatcher;
        private CallbackDispatcher callbackDispatcher;
        private DownloadStore downloadStore;
        private DownloadConnection.Factory connectionFactory;
        private ProcessFileStrategy processFileStrategy;
        private DownloadStrategy downloadStrategy;
        private DownloadOutputStream.Factory outputStreamFactory;
        private DownloadMonitor monitor;
        private final Context context;

        public Builder(@NonNull Context context) {
            this.context = context.getApplicationContext();
        }

        public Builder downloadDispatcher(DownloadDispatcher downloadDispatcher) {
            this.downloadDispatcher = downloadDispatcher;
            return this;
        }

        public Builder callbackDispatcher(CallbackDispatcher callbackDispatcher) {
            this.callbackDispatcher = callbackDispatcher;
            return this;
        }

        public Builder downloadStore(DownloadStore downloadStore) {
            this.downloadStore = downloadStore;
            return this;
        }

        public Builder connectionFactory(DownloadConnection.Factory connectionFactory) {
            this.connectionFactory = connectionFactory;
            return this;
        }

        public Builder outputStreamFactory(DownloadOutputStream.Factory outputStreamFactory) {
            this.outputStreamFactory = outputStreamFactory;
            return this;
        }

        public Builder processFileStrategy(ProcessFileStrategy processFileStrategy) {
            this.processFileStrategy = processFileStrategy;
            return this;
        }

        public Builder downloadStrategy(DownloadStrategy downloadStrategy) {
            this.downloadStrategy = downloadStrategy;
            return this;
        }

        public Builder monitor(DownloadMonitor monitor) {
            this.monitor = monitor;
            return this;
        }

        public PDownload build() {
            if (downloadDispatcher == null) {
                downloadDispatcher = new DownloadDispatcher();
            }

            if (callbackDispatcher == null) {
                callbackDispatcher = new CallbackDispatcher();
            }

            if (downloadStore == null) {
                downloadStore = new DownloadStoreOnDB();
            }

            if (connectionFactory == null) {
                connectionFactory = Util.createDefaultConnectionFactory();
            }

            if (outputStreamFactory == null) {
                outputStreamFactory = new DownloadUriOutputStream.Factory();
            }

            if (processFileStrategy == null) {
                processFileStrategy = new ProcessFileStrategy();
            }

            if (downloadStrategy == null) {
                downloadStrategy = new DownloadStrategy();
            }

            PDownload pDownload = new PDownload(context,
                    downloadDispatcher,
                    callbackDispatcher,
                    downloadStore,
                    connectionFactory,
                    outputStreamFactory,
                    processFileStrategy,
                    downloadStrategy);

            pDownload.setMonitor(monitor);

            return pDownload;
        }
    }
}
