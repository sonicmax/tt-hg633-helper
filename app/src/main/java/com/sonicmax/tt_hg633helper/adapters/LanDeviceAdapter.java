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
import com.sonicmax.tt_hg633helper.charting.LanDeviceChartHelper;
import com.sonicmax.tt_hg633helper.charting.TimestampAxisValueFormatter;
import com.sonicmax.tt_hg633helper.database.LanDeviceDataProvider;
import com.sonicmax.tt_hg633helper.database.WifiDeviceDataProvider;
import com.sonicmax.tt_hg633helper.ui.DiagnoseLanUiHelper;
import com.sonicmax.tt_hg633helper.utilities.SharedPreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LanDeviceAdapter extends RecyclerView.Adapter<LanDeviceAdapter.NetworkDeviceViewHolder> {

    private final String LOG_TAG = this.getClass().getSimpleName();

    private final int BYTES_SENT = 0;
    private final int BYTES_RECEIVED = 1;

    private final Context mContext;
    private final List<JSONObject> mDevices;
    private final LanDeviceChartHelper mChartHelper;
    private final LanDeviceDataProvider mDataProvider;
    private final long mReferenceTimestamp;

    private int mChartType;
    private int mExpandedPosition = -1;

    public LanDeviceAdapter(Context context) {
        mContext = context;
        mDevices = new ArrayList<>();
        mChartHelper = new LanDeviceChartHelper();
        mDataProvider = new LanDeviceDataProvider(context);
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
                Log.v(LOG_TAG, device.toString());
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
        return R.layout.lan_device_card;
    }

    @Override
    public LanDeviceAdapter.NetworkDeviceViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        return new LanDeviceAdapter.NetworkDeviceViewHolder(LayoutInflater.
                from(viewGroup.getContext()).
                inflate(viewType, viewGroup, false), mReferenceTimestamp);
    }

    @Override
    public void onBindViewHolder(LanDeviceAdapter.NetworkDeviceViewHolder viewHolder, int position) {
        JSONObject device = mDevices.get(position);
        viewHolder.device = device;
        DiagnoseLanUiHelper.populateCard(mContext, device, viewHolder);
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
                case BYTES_SENT:
                    data = new LineData(mChartHelper.getBytesSent(device));
                    chartView.getXAxis().setLabelCount(2, true);
                    chartView.getXAxis().setAvoidFirstLastClipping(true);
                    break;

                case BYTES_RECEIVED:
                    data = new LineData(mChartHelper.getBytesReceived(device));
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
        public CardView cardView;
        public TextView lanView;
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
            lanView = (TextView) view.findViewById(R.id.device_lan);
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
            Button sentButton = (Button) view.findViewById(R.id.device_chart_bytes_sent);
            Button receivedButton = (Button) view.findViewById(R.id.device_chart_bytes_received);

            sentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    needChartUpdate(chartView, BYTES_SENT, device);
                }
            });

            receivedButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    needChartUpdate(chartView, BYTES_RECEIVED, device);
                }
            });
        }
    }
}
