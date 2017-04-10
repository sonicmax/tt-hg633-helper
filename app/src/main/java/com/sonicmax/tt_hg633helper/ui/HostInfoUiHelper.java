package com.sonicmax.tt_hg633helper.ui;

import android.content.Context;
import android.net.Network;
import android.util.Log;
import android.view.View;

import com.sonicmax.tt_hg633helper.R;
import com.sonicmax.tt_hg633helper.adapters.NetworkDeviceAdapter;
import com.sonicmax.tt_hg633helper.utilities.UptimeFormatter;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class HostInfoUiHelper {
    private static final String LOG_TAG = "HostInfoUiHelper";
    // For main view
    private static final String ACTIVE = "Active";
    private static final String VENDOR_ID = "VendorClassID";
    private static final String HOST_NAME = "HostName";
    private static final String RSSI = "Rssi";
    private static final String LAYER_2_INTERFACE = "Layer2Interface";
    private static final String IP_ADDRESS = "IPAddress";
    private static final String MAC_ADDRESS = "MACAddress";
    // For detail view
    private static final String LEASE_TIME = "LeaseTime";
    private static final String DEVICE_RATE = "AssociatedDeviceRate";
    private static final String PARENT_CONTROL = "ParentControlEnable";


    public static void populateCard(Context context, JSONObject device, NetworkDeviceAdapter.NetworkDeviceViewHolder viewHolder) {
        try {
            String vendorId = device.getString(VENDOR_ID);
            String hostName = device.getString(HOST_NAME);
            String ip = device.getString(IP_ADDRESS);
            String mac = device.getString(MAC_ADDRESS);

            viewHolder.cardView.setVisibility(View.VISIBLE);
            viewHolder.nameView.setText(hostName);
            viewHolder.ipView.setText(ip);
            viewHolder.macView.setText(mac);

            if (viewHolder.cardView.isActivated()) {
                viewHolder.detailView.setText(getDetails(device));
            }

            Picasso.with(context)
                    .load(DeviceIconGetter.checkDevice(vendorId, hostName))
                    .into(viewHolder.iconView);

            // Prevents JSONException if device is connected via LAN
            if (device.getString(LAYER_2_INTERFACE).contains("SSID")) {
                Picasso.with(context)
                        .load(SignalStrengthIconGetter.checkSignal(device.getInt(RSSI)))
                        .into(viewHolder.signalView);
            } else {
                Picasso.with(context)
                        .load(R.drawable.lan_100)
                        .into(viewHolder.signalView);
            }

            if (device.getBoolean(ACTIVE)) {
                viewHolder.itemView.setAlpha(1f);
            }
            else {
                viewHolder.itemView.setAlpha(0.5f);
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e.getCause());
            viewHolder.setIsRecyclable(true);
        }
    }

    private static String getDetails(JSONObject device) throws JSONException {
        String details = "";
        String leaseTime = null;
        String deviceRate = null;

        if (device.getInt(LEASE_TIME) > 0) {
            leaseTime = UptimeFormatter.getString(device.getInt(LEASE_TIME));
        }

        if (device.getString(LAYER_2_INTERFACE).contains("SSID")) {
            deviceRate = device.getString(DEVICE_RATE);
        }

        boolean hasParentalControl = device.getBoolean(PARENT_CONTROL);

        if (leaseTime != null) {
            details += "Lease Time: " + leaseTime + "\n";
        }

        if (deviceRate != null) {
            details += "Device Rate: " + deviceRate + "\n";
        }

        if (hasParentalControl) {
            details += "Parental Control Enabled";
        }

        return details;
    }
}
