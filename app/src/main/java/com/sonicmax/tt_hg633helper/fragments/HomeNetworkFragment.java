package com.sonicmax.tt_hg633helper.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.sonicmax.tt_hg633helper.R;
import com.sonicmax.tt_hg633helper.adapters.NetworkDeviceAdapter;
import com.sonicmax.tt_hg633helper.loaders.AccountManager;
import com.sonicmax.tt_hg633helper.loaders.ApiRequestHandler;
import com.sonicmax.tt_hg633helper.network.ApiPoller;
import com.sonicmax.tt_hg633helper.charting.DeviceInfoChartHelper;
import com.sonicmax.tt_hg633helper.parsers.HostInfoParser;
import com.sonicmax.tt_hg633helper.ui.ProgressDialogHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeNetworkFragment extends Fragment implements ApiRequestHandler.EventInterface {
    private final String LOG_TAG = this.getClass().getSimpleName();
    private final String HOST_INFO = "HostInfo";
    private final String HEARTBEAT = "heartbeat";

    private RecyclerView mRecyclerView;

    private AccountManager mAccountManager;
    private ApiRequestHandler mApiRequestHandler;
    private NetworkDeviceAdapter mDeviceAdapter;
    private LinearLayoutManager mLayoutManager;
    private JSONObject mApiData;
    private DeviceInfoChartHelper mChartHelper;

    private List<ApiPoller> mApiPollers;
    private boolean mShowAllDevices = false;

    public HomeNetworkFragment() {}

    ///////////////////////////////////////////////////////////////////////////
    // Fragment methods
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mApiData = new JSONObject();
        mAccountManager = new AccountManager(getContext(), null);
        mApiRequestHandler = new ApiRequestHandler(getContext(), this);
        mDeviceAdapter = new NetworkDeviceAdapter(getContext());
        mLayoutManager = new LinearLayoutManager(getContext());
        mChartHelper = new DeviceInfoChartHelper();

        mApiPollers = new ArrayList<>();
        mApiPollers.add(new ApiPoller(mApiRequestHandler, HEARTBEAT, 5000));
        mApiPollers.add(new ApiPoller(mApiRequestHandler, HOST_INFO, 3000));

        ProgressDialogHandler.showDialog(getContext(), "Loading...");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home_network, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.network_device_recyclerview);
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
        try {
            switch (item.getItemId()) {
                case R.id.menu_home_ntwk_inactive:
                    mShowAllDevices = !mShowAllDevices;
                    if (mShowAllDevices) {
                        populateAdapterWithAllDevices(mApiData.getJSONArray(HOST_INFO));
                    } else {
                        populateAdapterWithActiveDevices(mApiData.getJSONArray(HOST_INFO));
                    }
                    break;

                default:
                    Log.d(LOG_TAG, "No action for menu item " + item.getItemId());
            }
        } catch (JSONException e) {
            refreshData();
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

                if (endpoint.equals(HOST_INFO)) {
                    if (mShowAllDevices) {
                        populateAdapterWithAllDevices((JSONArray) data);
                    } else {
                        populateAdapterWithActiveDevices((JSONArray) data);
                    }
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

    private void stopPolling(String endpoint) {
        for (ApiPoller apiPoller: mApiPollers) {
            if (apiPoller.getEndpoint().equals(endpoint)) {
                Log.d(LOG_TAG, "Stopping polling " + endpoint);
                apiPoller.stopPolling();
            }
        }
    }

    private void stopPolling() {
        for (ApiPoller apiPoller: mApiPollers) {
            apiPoller.stopPolling();
        }
    }

    private void refreshData() {
        ProgressDialogHandler.showDialog(getContext(), "Loading...");

        if (mApiRequestHandler == null) {
            mApiRequestHandler = new ApiRequestHandler(getContext(), this);
        }
        mApiRequestHandler.get(HOST_INFO);
    }

    private void populateAdapterWithActiveDevices(JSONArray devices) throws JSONException {
        List<JSONObject> activeDevices = HostInfoParser.getActiveDevices(devices);
        mDeviceAdapter.addDevices(HostInfoParser.sortByLeaseTime(activeDevices));
    }

    private void populateAdapterWithAllDevices(JSONArray devices) throws JSONException {
        mDeviceAdapter.addDevices(HostInfoParser.convertJSONArrayToList(devices));
    }
}
