package com.sonicmax.tt_hg633helper.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.sonicmax.tt_hg633helper.R;
import com.sonicmax.tt_hg633helper.adapters.LanDeviceAdapter;
import com.sonicmax.tt_hg633helper.adapters.WifiDeviceAdapter;
import com.sonicmax.tt_hg633helper.loaders.ApiRequestHandler;
import com.sonicmax.tt_hg633helper.network.ApiPoller;
import com.sonicmax.tt_hg633helper.parsers.DiagnoseLanParser;
import com.sonicmax.tt_hg633helper.parsers.HostInfoParser;
import com.sonicmax.tt_hg633helper.ui.ProgressDialogHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class LanDeviceFragment extends ApiConsumerFragment implements ApiRequestHandler.EventInterface {
    private final String LOG_TAG = this.getClass().getSimpleName();
    private final String DIAGNOSE_LAN = "diagnose_lan";
    private final String HEARTBEAT = "heartbeat";

    private RecyclerView mRecyclerView;

    private ApiRequestHandler mApiRequestHandler;
    private LanDeviceAdapter mDeviceAdapter;
    private LinearLayoutManager mLayoutManager;
    private JSONObject mApiData;

    public LanDeviceFragment() {}

    ///////////////////////////////////////////////////////////////////////////
    // Fragment methods
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mApiData = new JSONObject();
        mDeviceAdapter = new LanDeviceAdapter(getContext());
        mLayoutManager = new LinearLayoutManager(getContext());
        mApiRequestHandler = new ApiRequestHandler(getContext(), this);
        mApiPollers.add(new ApiPoller(mApiRequestHandler, DIAGNOSE_LAN, 3000));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.lan_tab, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.lan_recyclerview);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mDeviceAdapter);

        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ProgressDialogHandler.dismissDialog();
        stopPolling();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                Log.d(LOG_TAG, "No action for menu item " + item.getItemId());
        }

        return super.onOptionsItemSelected(item);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Interface methods
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onLoadComplete(Object data, String endpoint) {
        ProgressDialogHandler.dismissDialog();

        // Store response and decide how adapter should be populated with data.

        if (data != null) {
            try {
                if (mApiData == null) {
                    mApiData = new JSONObject();
                }

                mApiData.put(endpoint, data);

                if (endpoint.equals(DIAGNOSE_LAN)) {
                    populateAdapterWithAllDevices((JSONArray) data);
                }

            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error retrieving data from JSONObject", e);
                Log.d(LOG_TAG, "Endpoint: " + endpoint);

            } catch (NullPointerException e) {
                Log.e(LOG_TAG, "Data from ApiRequestHandler was null", e);
                Log.d(LOG_TAG, "Endpoint: " + endpoint);
                stopPolling(endpoint);
            }
        }
        else {
            stopPolling(endpoint);
        }
    }

    @Override
    public void onError(String endpoint) {
        stopPolling(endpoint);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Helper methods
    ///////////////////////////////////////////////////////////////////////////

    private void refreshData() {
        ProgressDialogHandler.showDialog(getContext(), "Loading...");

        if (mApiRequestHandler == null) {
            mApiRequestHandler = new ApiRequestHandler(getContext(), this);
        }

        mApiRequestHandler.get(DIAGNOSE_LAN);
    }

    private void populateAdapterWithAllDevices(JSONArray devices) throws JSONException {
        mDeviceAdapter.addDevices(DiagnoseLanParser.convertToList(devices));
    }
}
