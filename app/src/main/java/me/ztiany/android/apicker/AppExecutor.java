package me.ztiany.android.apicker;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

final class AppExecutor {

    private AppExecutor() {
        throw new AssertionError("no instance");
    }

    private static final Executor io = Executors.newCachedThreadPool();

    private static final Handler main = new Handler(Looper.getMainLooper());

    public static void runOnIO(Runnable runnable) {
        io.execute(runnable);
    }

    public static void runOnMain(Runnable runnable) {
        main.post(runnable);
    }

}