package com.shadorc.ahchat;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ThreadPoolManager {

    public static ThreadPoolManager instance;

    static {
        ThreadPoolManager.instance = new ThreadPoolManager();
    }

    private final Executor executor;

    private ThreadPoolManager() {
        this.executor = Executors.newCachedThreadPool();
    }

    public void execute(final Runnable runnable) {
        this.executor.execute(runnable);
    }

    public static ThreadPoolManager getInstance() {
        return ThreadPoolManager.instance;
    }

}
