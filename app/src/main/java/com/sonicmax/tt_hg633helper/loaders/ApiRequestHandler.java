package com.sonicmax.tt_hg633helper.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;

import com.sonicmax.tt_hg633helper.network.WebRequest;
import com.sonicmax.tt_hg633helper.parsers.DiagnoseLanParser;
import com.sonicmax.tt_hg633helper.parsers.HostInfoParser;
import com.sonicmax.tt_hg633helper.utilities.ApiPathManager;
import com.sonicmax.tt_hg633helper.utilities.DataFilter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Sends, receives and stores data from router APIs.
 */

public class ApiRequestHandler implements LoaderManager.LoaderCallbacks<Object> {
    private final String LOG_TAG = this.getClass().getSimpleName();

    // Endpoint strings
    private final String HOST_INFO = "HostInfo";
    private final String HOST_INFO_WIRELESS = "HostInfo?devicetype=wireless";
    private final String DIAGNOSE_LAN = "diagnose_lan";

    // Loader ID
    private final int POST_REQUEST = -1;

    private final Context mContext;
    private final EventInterface mEventInterface;
    private final LoaderManager mLoaderManager;

    private String mHostname;

    public ApiRequestHandler(Context context, EventInterface eventInterface) {
        mContext = context;
        mEventInterface = eventInterface;
        mLoaderManager = ((FragmentActivity) mContext).getSupportLoaderManager();
        mHostname = "http://192.168.1.1";
    }

    public interface EventInterface {
        void onLoadComplete(Object data, String endpoint);
        void onError(String endpoint);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Helper methods
    ///////////////////////////////////////////////////////////////////////////

    public void get(String endpoint) {
        String fullPath = mHostname + ApiPathManager.getFullPath(endpoint);

        Bundle args = new Bundle(2);
        args.putString("endpoint", endpoint);
        args.putString("url", fullPath);

        mLoaderManager.initLoader(LoaderIdProvider.getIdForEndpoint(endpoint), args, this).forceLoad();
    }

    public void post(String endpoint, JSONObject payload) {
        String fullPath = mHostname + ApiPathManager.getFullPath(endpoint);

        Bundle args = new Bundle(3);
        args.putString("endpoint", endpoint);
        args.putString("url", fullPath);
        args.putString("payload", payload.toString());

        mLoaderManager.initLoader(LoaderIdProvider.getIdForEndpoint(endpoint), args, this).forceLoad();
    }

    /**
     * Inserts API response from certain endpoints to database
     *
     * @param data
     * @param endpoint
     */
    private void checkAndInsertToDatabase(Object data, String endpoint) throws JSONException {
        switch(endpoint) {
            case HOST_INFO:
            case HOST_INFO_WIRELESS:
                HostInfoParser.insertPerformanceRecordsToDatabase(mContext, (JSONArray) data);
                break;

            case DIAGNOSE_LAN:
                DiagnoseLanParser.insertPerformanceRecordsToDatabase(mContext, (JSONArray) data);
                break;

            default:
                // Do nothing
                break;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Loader callbacks
    ///////////////////////////////////////////////////////////////////////////


    @Override
    public Loader<Object> onCreateLoader(int id, final Bundle args) {
        switch (id) {
            case POST_REQUEST:
                return null;

            default:
                return new AsyncLoader(mContext, args) {

                    @Override
                    public Object loadInBackground() {
                        String endpoint = args.getString("endpoint");
                        String response;

                        if (args.getString("payload") != null) {
                            response = new WebRequest(mContext)
                                    .post(args.getString("url"), args.getString("payload"));

                        } else {
                            response = new WebRequest(mContext).get(args.getString("url"));
                        }


                        if (response != null) {
                            String filteredResponse = DataFilter.filter(response);

                            try {
                                Object data;

                                // Some endpoint responses cast to JSONArray, while others cast to JSONObject.
                                // We can decide how to read data in UI layer

                                if (filteredResponse.startsWith("[")) {
                                    data = DataFilter.getDataAsJsonArray(filteredResponse);
                                } else {
                                    data = DataFilter.getDataAsJson(filteredResponse);
                                }

                                checkAndInsertToDatabase(data, endpoint);

                                return data;

                            } catch (JSONException e) {
                                Log.e(LOG_TAG, "Error parsing response", e);
                                Log.d(LOG_TAG, "Response: " + response);
                                return null;
                            }

                        } else {
                            Log.v(LOG_TAG, "response was null");
                            return null;
                        }
                    }
                };
        }
    }

    @Override
    public void onLoadFinished(Loader<Object> loader, Object data) {

        switch (loader.getId()) {
            case POST_REQUEST:
                break;

            default:
                String endpoint = LoaderIdProvider.getEndpointFromId(loader.getId());

                if (data != null) {
                    Log.d(LOG_TAG, "data: " + data.toString());
                    mEventInterface.onLoadComplete(data, endpoint);
                }
                else {
                    mEventInterface.onError(endpoint);
                }

                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Object> loader) {
        loader.reset();
    }

}
