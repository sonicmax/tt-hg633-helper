package com.sonicmax.tt_hg633helper.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;

import com.sonicmax.tt_hg633helper.R;
import com.sonicmax.tt_hg633helper.charting.WifiDeviceChartHelper;
import com.sonicmax.tt_hg633helper.charting.TimestampAxisValueFormatter;
import com.sonicmax.tt_hg633helper.database.WifiDeviceDataProvider;
import com.sonicmax.tt_hg633helper.ui.HostInfoUiHelper;
import com.sonicmax.tt_hg633helper.utilities.SharedPreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WifiDeviceAdapter extends RecyclerView.Adapter<WifiDeviceAdapter.NetworkDeviceViewHolder> {

    private final String LOG_TAG = this.getClass().getSimpleName();

    private final int SIGNAL_QUALITY = 0;
    private final int DEVICE_RATE = 1;

    private final Context mContext;
    private final List<JSONObject> mDevices;
    private final WifiDeviceChartHelper mChartHelper;
    private final WifiDeviceDataProvider mDataProvider;
    private final long mReferenceTimestamp;

    private int mChartType;
    private int mExpandedPosition = -1;

    public WifiDeviceAdapter(Context context) {
        mContext = context;
        mDevices = new ArrayList<>();
        mChartHelper = new WifiDeviceChartHelper();
        mDataProvider = new WifiDeviceDataProvider(context);
        mReferenceTimestamp = SharedPreferenceManager.getLong(context, "reference_timestamp");

        getPerformanceRecordsFromDatabase();
    }

    private void getPerformanceRecordsFromDatabase() {
        Cursor cursor = mDataProvider.open().getAllPerformanceRecords();
        while (cursor.moveToNext()) {
            mChartHelper.getEntryFromCursor(cursor);
        }
    }

    public void addDevices(List<JSONObject> devices) {
        mDevices.clear();
        mDevices.addAll(devices);

        long relativeTime = new Date().getTime() - mReferenceTimestamp;

        try {
            for (JSONObject device : devices) {
                mChartHelper.getEntriesFromResponse(device, relativeTime);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error while preparing data for chart", e);
        }

        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDevices.size();
    }


    @Override
    public int getItemViewType(int position) {
        return R.layout.wifi_device_card;
    }

    @Override
    public WifiDeviceAdapter.NetworkDeviceViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        return new WifiDeviceAdapter.NetworkDeviceViewHolder(LayoutInflater.
                from(viewGroup.getContext()).
                inflate(viewType, viewGroup, false), mReferenceTimestamp);
    }

    @Override
    public void onBindViewHolder(WifiDeviceAdapter.NetworkDeviceViewHolder viewHolder, int position) {
        JSONObject device = mDevices.get(position);
        viewHolder.device = device;
        HostInfoUiHelper.populateCard(mContext, device, viewHolder);
        handleDetailExpansion(viewHolder, position);

        if (viewHolder.itemView.isActivated()) {
            updateLineChart(viewHolder.chartView, device);
        }
    }

    private void updateLineChart(LineChart chartView, JSONObject device) {
        LineData data = null;

        // Note: mChartType == SIGNAL_QUALITY by default

        try {
            switch (mChartType) {
                case SIGNAL_QUALITY:
                    data = new LineData(mChartHelper.getSignalDataSet(device));
                    chartView.getAxisLeft().setAxisMinimum(0);
                    chartView.getAxisLeft().setAxisMaximum(100);
                    chartView.getAxisRight().setAxisMinimum(0);
                    chartView.getAxisRight().setAxisMaximum(100);
                    chartView.getXAxis().setLabelCount(2, true);
                    chartView.getXAxis().setAvoidFirstLastClipping(true);
                    break;

                case DEVICE_RATE:
                    data = new LineData(mChartHelper.getDeviceRateDataSet(device));
                    chartView.getAxisLeft().setAxisMinimum(0);
                    chartView.getAxisLeft().setAxisMaximum(150);
                    chartView.getAxisRight().setAxisMinimum(0);
                    chartView.getAxisRight().setAxisMaximum(150);
                    chartView.getXAxis().setLabelCount(2, true);
                    chartView.getXAxis().setAvoidFirstLastClipping(true);
                    break;

                default:
                    break;
            }

            chartView.setData(data);
            chartView.invalidate();

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error while updating line chart", e);
        }
    }

    private void handleDetailExpansion(NetworkDeviceViewHolder viewHolder, final int position) {
        final boolean isExpanded = position == mExpandedPosition;

        viewHolder.detailView.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        viewHolder.chartContainerLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

        viewHolder.itemView.setActivated(isExpanded);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExpandedPosition = isExpanded ? -1 : position;
                notifyDataSetChanged();
            }
        });
    }

    public void needChartUpdate(LineChart chartView, int chartType, JSONObject device) {
        mChartType = chartType;
        updateLineChart(chartView, device);
    }

    public class NetworkDeviceViewHolder extends RecyclerView.ViewHolder {
        private final int SIGNAL_QUALITY = 0;
        private final int DEVICE_RATE = 1;

        public CardView cardView;
        public TextView nameView;
        public TextView ipView;
        public TextView macView;
        public TextView detailView;
        public ImageView signalView;
        public ImageView iconView;
        public RelativeLayout chartContainerLayout;
        public LineChart chartView;
        public JSONObject device;

        public NetworkDeviceViewHolder(View view, long referenceTimestamp) {
            super(view);
            cardView = (CardView) view;
            nameView = (TextView) view.findViewById(R.id.device_name);
            ipView = (TextView) view.findViewById(R.id.device_ip_value);
            macView = (TextView) view.findViewById(R.id.device_mac_value);
            detailView = (TextView) view.findViewById(R.id.device_details);
            signalView = (ImageView) view.findViewById(R.id.device_signal);
            iconView = (ImageView) view.findViewById(R.id.device_icon);
            chartView = (LineChart) view.findViewById(R.id.device_chart);
            chartContainerLayout = (RelativeLayout) view.findViewById(R.id.device_chart_container);
            initLineChart(view, referenceTimestamp);
            setListeners(view);
        }

        private void initLineChart(View view, long referenceTimestamp) {
            // chartView.setTouchEnabled(false);
            chartView.getDescription().setEnabled(false);
            chartView.getAxisLeft().setEnabled(false);
            chartView.getXAxis().setValueFormatter(new TimestampAxisValueFormatter(mContext, referenceTimestamp));
        }

        private void setListeners(View view) {
            Button signalButton = (Button) view.findViewById(R.id.device_chart_signal);
            Button rateButton = (Button) view.findViewById(R.id.device_chart_dbm);

            signalButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    needChartUpdate(chartView, SIGNAL_QUALITY, device);
                }
            });

            rateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    needChartUpdate(chartView, DEVICE_RATE, device);
                }
            });
        }
    }
}
