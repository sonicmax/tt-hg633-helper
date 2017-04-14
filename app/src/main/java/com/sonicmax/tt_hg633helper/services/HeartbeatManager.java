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
import android.util.Log;

import com.sonicmax.tt_hg633helper.network.ApiPathManager;
import com.sonicmax.tt_hg633helper.network.WebRequest;
import com.sonicmax.tt_hg633helper.utilities.JsonDataFilter;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Makes regular requests to the heartbeat API (required for session token to remain valid).
 * TODO: Needs to be stopped when app is not running
 */
public class HeartbeatManager extends Service {
    private final String LOG_TAG = this.getClass().getSimpleName();
    private final int JSON_ERROR = -1;

    private final String mHeartbeatEndpoint;
    private final WebRequest mWebRequest;

    private Messenger mMessenger;
    private Looper mServiceLooper;
    private Handler mServiceHandler;

    private boolean mShouldRun;
    private boolean mIsRunning = false;
    private int mActivityCount = 1;

    public HeartbeatManager() {
        super();
        final String HEARTBEAT = "heartbeat";
        String hostName = "http://192.168.1.1";
        mHeartbeatEndpoint = hostName + ApiPathManager.getFullPath(HEARTBEAT);
        mWebRequest = new WebRequest(this);
        mShouldRun = true;
    }

    @Override
    public void onCreate() {
        HandlerThread thread = new HandlerThread(LOG_TAG, Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        mServiceLooper = thread.getLooper();
        mServiceHandler = new Handler(mServiceLooper) {
            final int OPEN = 1;
            final int CLOSED = 2;
            final int START = 3;

            @Override
            public void handleMessage(Message msg) {
                switch (msg.getData().getInt("code")) {
                    case OPEN:
                        mActivityCount++;
                        break;

                    case CLOSED:
                        mActivityCount--;
                        break;

                    case START:
                        if (!mIsRunning) {
                            mIsRunning = true;
                            heartbeat();
                        }
                        break;
                }

                if (mActivityCount == 0) {
                    mShouldRun = false;
                    stopSelf();
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

    private void heartbeat() {
        String response = mWebRequest.get(mHeartbeatEndpoint);

        if (response != null) {
            int interval = getIntervalFromResponse(response);

            if (interval == JSON_ERROR) {
                // Maybe the firmware was updated
                this.stopSelf();

            } else {

                if (mShouldRun) {
                    new Handler().postDelayed(new Runnable() {

                        public void run() {
                            heartbeat();
                        }

                    }, interval);
                }

                else {
                    mIsRunning = false;
                    this.stopSelf();
                }
            }
        }

        else {
            // Null response means session token was invalidated (or we lost connection)
            this.stopSelf();
        }
    }

    /**
     * Removes JSON hijacking protection and returns interval from response.
     * @param response
     * @return
     */
    private int getIntervalFromResponse(String response) {
        final String INTERVAL = "interval";

        try {
            JSONObject json = JsonDataFilter.getDataAsJson(response);
            return json.getInt(INTERVAL);

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error while parsing heartbeat response", e);
            return JSON_ERROR;
        }
    }
}
