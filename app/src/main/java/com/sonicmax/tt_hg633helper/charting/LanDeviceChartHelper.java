package com.sonicmax.tt_hg633helper.charting;

import android.database.Cursor;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.sonicmax.tt_hg633helper.database.AppDataContract.LanPerformanceEntry;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helps to display the response from deviceinfo endpoint in a chart by keeping track of data sets
 * for each device and performing some UI modifications to the LineChart.
 */

public class LanDeviceChartHelper {
    private final String Name = "Name";
    private final String BYTES_SENT = "BytesSent";
    private final String BYTES_RECEIVED = "BytesReceived";

    private final Map<String, List<Entry>> mBytesSent;
    private final Map<String, List<Entry>> mBytesReceived;

    public LanDeviceChartHelper() {
        mBytesSent = new HashMap<>();
        mBytesReceived = new HashMap<>();
    }

    public void getEntriesFromResponse(JSONObject port, long time) throws JSONException {
        String name = port.getString(Name);

        if (mBytesSent.get(name) == null) {
            mBytesSent.put(name, new ArrayList<Entry>());
        }

        if (mBytesReceived.get(name) == null) {
            mBytesReceived.put(name, new ArrayList<Entry>());
        }

        mBytesSent.get(name).add(new Entry(time, port.getInt(BYTES_SENT)));
        mBytesReceived.get(name).add(new Entry(time, port.getInt(BYTES_RECEIVED)));
    }

    public void getEntryFromCursor(Cursor cursor) {
        String name = cursor.getString(cursor.getColumnIndex(LanPerformanceEntry.COLUMN_PORT));
        long timestamp = cursor.getLong(cursor.getColumnIndex(LanPerformanceEntry.COLUMN_TIMESTAMP));
        int sent = cursor.getInt(cursor.getColumnIndex(LanPerformanceEntry.COLUMN_BYTES_SENT));
        int received = cursor.getInt(cursor.getColumnIndex(LanPerformanceEntry.COLUMN_BYTES_RECEIVED));

        if (mBytesSent.get(name) == null) {
            mBytesSent.put(name, new ArrayList<Entry>());
        }

        if (mBytesReceived.get(name) == null) {
            mBytesReceived.put(name, new ArrayList<Entry>());
        }

        mBytesSent.get(name).add(new Entry(timestamp, sent));
        mBytesReceived.get(name).add(new Entry(timestamp, received));
    }

    public ILineDataSet getBytesSent(JSONObject port) throws JSONException {
        String name = port.getString(Name);

        LineDataSet bytesSentData = new LineDataSet(mBytesSent.get(name), "Bytes Sent");
        int color = ColorTemplate.JOYFUL_COLORS[0];
        bytesSentData.setColor(color);
        bytesSentData.setDrawFilled(true);
        bytesSentData.setFillColor(color);
        bytesSentData.setFillAlpha(50);
        bytesSentData.setDrawValues(false);
        bytesSentData.setDrawCircles(false);

        return bytesSentData;
    }

    public ILineDataSet getBytesReceived(JSONObject port) throws JSONException {
        String name = port.getString(Name);

        LineDataSet bytesReceivedData = new LineDataSet(mBytesReceived.get(name), "Bytes Received");
        int color = ColorTemplate.JOYFUL_COLORS[1];
        bytesReceivedData.setColor(color);
        bytesReceivedData.setDrawFilled(true);
        bytesReceivedData.setFillColor(color);
        bytesReceivedData.setFillAlpha(50);
        bytesReceivedData.setDrawValues(false);
        bytesReceivedData.setDrawCircles(false);

        return bytesReceivedData;
    }
}
