package com.sonicmax.tt_hg633helper.fragments;

import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;
import com.sonicmax.tt_hg633helper.R;
import com.sonicmax.tt_hg633helper.loaders.ApiRequestHandler;
import com.sonicmax.tt_hg633helper.network.ApiPoller;
import com.sonicmax.tt_hg633helper.charting.DslInfoChartHelper;
import com.sonicmax.tt_hg633helper.ui.InternetInfoUiHelper;
import com.sonicmax.tt_hg633helper.ui.ProgressDialogHandler;

import org.json.JSONException;
import org.json.JSONObject;

public class InternetFragment extends ApiConsumerFragment implements ApiRequestHandler.EventInterface {
    private final String LOG_TAG = this.getClass().getSimpleName();
    private final String DSL_INFO = "dslinfo";
    private final String WAN_INFO = "defaultwaninfo";
    private final String HEARTBEAT = "heartbeat";

    private ApiRequestHandler mApiRequestHandler;
    private JSONObject mApiData;
    private TextView mInternetInfoView;
    private LineChart mInternetInfoChart;
    private DslInfoChartHelper mChartHelper;

    private boolean mWaitingForWan = true;
    private boolean mWaitingForDsl = true;
    private int mDuration;

    public InternetFragment() {}

    ///////////////////////////////////////////////////////////////////////////
    // Fragment methods
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mApiRequestHandler = new ApiRequestHandler(getContext(), this);
        mChartHelper = new DslInfoChartHelper();

        mApiPollers.add(new ApiPoller(mApiRequestHandler, HEARTBEAT, 5000));
        mApiPollers.add(new ApiPoller(mApiRequestHandler, WAN_INFO, 10000));
        mApiPollers.add(new ApiPoller(mApiRequestHandler, DSL_INFO, 2000));

        ProgressDialogHandler.showDialog(getContext(), "Loading...");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_internet, container, false);
        mInternetInfoView = (TextView) rootView.findViewById(R.id.internet_info_view);
        mInternetInfoChart = (LineChart) rootView.findViewById(R.id.internet_info_chart);
        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ProgressDialogHandler.dismissDialog();
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

        if (data != null) {
            try {
                if (mApiData == null) {
                    mApiData = new JSONObject();
                }

                mApiData.put(endpoint, data);

                if (endpoint.equals(WAN_INFO)) {
                    mWaitingForWan = false;
                    if (!mWaitingForDsl) {
                        populateUi();
                    }
                }

                if (endpoint.equals(DSL_INFO)) {
                    mWaitingForDsl = false;
                    if (!mWaitingForWan) {
                        populateUi();
                    }
                }

            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error retrieving data from JSONObject", e);
                Log.d(LOG_TAG, "Endpoint: " + endpoint);

            } catch (NullPointerException e) {
                Log.e(LOG_TAG, "Data from ApiRequestHandler was null", e);
                Log.d(LOG_TAG, "Endpoint: " + endpoint);
            }
        }
        else {
            Log.d(LOG_TAG, "Stopping polling " + endpoint);
            stopPolling(endpoint);
        }
    }

    @Override
    public void onError(String endpoint) {
        Log.d(LOG_TAG, "Stopping polling " + endpoint);
        stopPolling(endpoint);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Helper methods
    ///////////////////////////////////////////////////////////////////////////

    private void populateUi() {
        try {
            JSONObject wanInfo = mApiData.getJSONObject(WAN_INFO);
            JSONObject dslInfo = mApiData.getJSONObject(DSL_INFO);

            // Display text
            SpannableStringBuilder info = InternetInfoUiHelper.buildString(wanInfo, dslInfo);
            mInternetInfoView.setText(info);

            // Prepare data and update line chart
            mChartHelper.getEntriesFromResponse(dslInfo, mDuration);
            LineData data = new LineData(mChartHelper.getLineDataSets());
            mInternetInfoChart.setData(data);
            mInternetInfoChart.invalidate();
            mDuration += 10;


        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error while populating UI:", e);
            Log.d(LOG_TAG, mApiData.toString());
        }
    }
}
