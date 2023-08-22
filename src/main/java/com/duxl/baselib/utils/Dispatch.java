package com.duxl.baselib.utils;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public enum Dispatch {

    I;

    private Handler mainHandler;
    private Handler threadHandler;
    private ExecutorService executorService;

    Dispatch() {
        mainHandler = new Handler(Looper.getMainLooper());
        HandlerThread handlerThread = new HandlerThread("handler-thread");
        handlerThread.start();
        threadHandler = new Handler(handlerThread.getLooper());
        executorService = Executors.newScheduledThreadPool(10);
    }

    public void postUIDelayed(Runnable r, long delayMillis) {
        mainHandler.postDelayed(r, delayMillis);
    }

    public void removeUICallbacks(Runnable r) {
        mainHandler.removeCallbacks(r);
    }

    public void postDelayed(Runnable r, long delayMillis) {
        threadHandler.postDelayed(r, delayMillis);
    }

    public void removeCallbacks(Runnable r) {
        threadHandler.removeCallbacks(r);
    }

    public void submit(Runnable r) {
        executorService.submit(r);
    }

    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
        }
    }

}
