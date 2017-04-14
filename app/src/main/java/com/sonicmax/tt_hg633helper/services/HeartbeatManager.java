package com.sonicmax.tt_hg633helper.services;

import android.os.Handler;
import android.util.Log;

import com.sonicmax.tt_hg633helper.network.ApiPathManager;
import com.sonicmax.tt_hg633helper.network.WebRequest;
import com.sonicmax.tt_hg633helper.utilities.JsonDataFilter;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Makes regular requests to the triggerHeartbeat API (required for session token to remain valid).
 */

public class HeartbeatManager extends ClosableBackgroundService {
    private final String LOG_TAG = this.getClass().getSimpleName();

    // Error code for getIntervalFromResponse()
    private final int JSON_ERROR = -1;

    private String mHeartbeatEndpoint;
    private WebRequest mWebRequest;

    public HeartbeatManager() {
        super();
        final String HEARTBEAT = "heartbeat";
        String hostName = "http://192.168.1.1";
        mHeartbeatEndpoint = hostName + ApiPathManager.getFullPath(HEARTBEAT);
        mWebRequest = new WebRequest(this);
    }

    @Override
    public void triggerAction() {
        String response = mWebRequest.get(mHeartbeatEndpoint);

        if (response != null) {
            int interval = getIntervalFromResponse(response);

            if (interval == JSON_ERROR) {
                // Maybe the firmware was updated
                stopSelf();

            } else {

                if (shouldRun) {
                    new Handler().postDelayed(new Runnable() {

                        public void run() {
                            triggerAction();
                        }

                    }, interval);
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
