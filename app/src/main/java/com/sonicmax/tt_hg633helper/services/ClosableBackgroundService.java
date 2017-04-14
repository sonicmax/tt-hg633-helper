package com.sonicmax.tt_hg633helper.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.Process;

import com.sonicmax.tt_hg633helper.network.WebRequest;

public abstract class ClosableBackgroundService  extends Service {
    private final String LOG_TAG = this.getClass().getSimpleName();

    // Codes for ServiceHandler
    private final int CLOSE = 1;
    private final int KEEP_OPEN = 2;

    private Messenger mMessenger;
    private Looper mServiceLooper;
    private Handler mServiceHandler;
    private Handler mDelayedExitHandler;
    private boolean mHasStarted = false;

    public boolean shouldRun;

    public ClosableBackgroundService() {
        super();
        shouldRun = true;
    }

    public abstract void triggerAction();

    @Override
    public void onCreate() {
        HandlerThread thread = new HandlerThread(LOG_TAG, Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        mServiceLooper = thread.getLooper();
        mDelayedExitHandler = new Handler();
        mServiceHandler = new Handler(mServiceLooper) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.getData().getInt("code")) {
                    case CLOSE:
                        triggerDelayedStop();
                        break;

                    case KEEP_OPEN:
                        mDelayedExitHandler.removeCallbacksAndMessages(null);
                        break;
                }

                if (!mHasStarted) {
                    triggerAction();
                    mHasStarted = true;
                }
            }
        };

        mMessenger = new Messenger(mServiceHandler);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Message message = mServiceHandler.obtainMessage();
        mServiceHandler.sendMessage(message);

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    private void triggerDelayedStop() {
        final int TWO_SECONDS = 2000;

        mDelayedExitHandler.postDelayed(new Runnable() {

            public void run() {
                shouldRun = false;
                stopSelf();
            }

        }, TWO_SECONDS);
    }
}