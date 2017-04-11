package com.sonicmax.tt_hg633helper.ui;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.sonicmax.tt_hg633helper.R;
import com.sonicmax.tt_hg633helper.adapters.LanDeviceAdapter;
import com.sonicmax.tt_hg633helper.adapters.WifiDeviceAdapter;
import com.sonicmax.tt_hg633helper.utilities.UptimeFormatter;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DiagnoseLanUiHelper {
    private static final String LOG_TAG = "HostInfoUiHelper";
    // JSON keys for main view
    private static final String STATUS = "Status";
    private static final String UP = "Up";
    private static final String NAME = "Name"; // Name of LAN port used
    private static final String HOST_NAME = "HostName";
    private static final String BLANK = "N/A";

    // JSON keys for detail view
    private static final String HOSTS = "Hosts";
    private static final String IP_ADDRESS = "IPAddress";
    private static final String MAC_ADDRESS = "MACAddress";
    private static final String BYTES_RECEIVED = "BytesReceived";
    private static final String BYTES_SENT = "BytesSent";
    private static final String PACKETS_RECEIVED = "PacketsReceived";
    private static final String PACKETS_SENT = "PacketsSent";
    private static final String PACKETS_R_DROPPED = "PacketsReceivedDrops";
    private static final String PACKETS_S_DROPPED = "PacketsSentDrops";


    public static void populateCard(Context context, JSONObject device, LanDeviceAdapter.NetworkDeviceViewHolder viewHolder) {
        try {
            String lan = device.getString(NAME);
            String hostName = BLANK;
            String ip = BLANK;
            String mac = BLANK;


            viewHolder.cardView.setVisibility(View.VISIBLE);

            Picasso.with(context)
                    .load(DeviceIconGetter.checkDevice("", hostName))
                    .into(viewHolder.iconView);

            if (device.getString(STATUS).equals(UP)) {
                JSONArray hosts = device.getJSONArray(HOSTS);
                JSONObject host = hosts.getJSONObject(0);

                ip = host.getString(IP_ADDRESS);
                mac = host.getString(MAC_ADDRESS);
                hostName = host.getString(HOST_NAME);

                viewHolder.itemView.setAlpha(1f);
            }

            else {
                viewHolder.itemView.setAlpha(0.5f);
            }

            viewHolder.lanView.setText(lan);
            viewHolder.nameView.setText(hostName);
            viewHolder.ipView.setText(ip);
            viewHolder.macView.setText(mac);

            if (viewHolder.cardView.isActivated()) {
                viewHolder.detailView.setText(getDetails(device));
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e.getCause());
            viewHolder.setIsRecyclable(true);
        }
    }

    private static String getDetails(JSONObject device) throws JSONException {
        final String LINEBREAK = "\n";
        String details = "";

        details += "Bytes sent: " + device.getInt(BYTES_SENT) + LINEBREAK;
        details += "Bytes received: " + device.getInt(BYTES_RECEIVED) + LINEBREAK;

        return details;
    }
}
