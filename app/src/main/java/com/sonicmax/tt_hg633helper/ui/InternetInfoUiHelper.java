package com.sonicmax.tt_hg633helper.ui;

import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;

import com.sonicmax.tt_hg633helper.utilities.UptimeFormatter;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Various methods to help with displaying WAN and DSL info provided by router APIs.
 */
public class InternetInfoUiHelper {
    // JSON keys for defaultwaninfo endpoint
    private static final String EXTERNAL_IP = "ExternalIPAddress";
    private static final String DNS = "DNSServers";

    // JSON keys for dslinfo endpoint
    private static final String STATUS = "Status";
    private static final String UP_RATE = "UpCurrRate";
    private static final String DOWN_RATE = "DownCurrRate";
    private static final String UP_NOISE = "ImpulsoNoiseProUs";
    private static final String DOWN_NOISE = "ImpulsoNoiseProDs";
    private static final String UP_INTERLEAVE = "InterleaveDelayUs";
    private static final String DOWN_INTERLEAVE = "InterleaveDelayDs";
    private static final String LINE_STANDARD = "Modulation";
    private static final String UP_ATTENUATION = "UpAttenuation";
    private static final String DOWN_ATTENUATION = "DownAttenuation";
    private static final String UP_POWER = "UpPower";
    private static final String DOWN_POWER = "DownPower";
    private static final String CHANNEL_TYPE = "DataPath";
    private static final String UPTIME = "ShowtimeStart";

    // Strings for UI
    private static final String IP_TEXT = "WAN IP Address: ";
    private static final String PRIMARY_DNS_TEXT = "Primary DNS Server: ";
    private static final String SECONDARY_DNS_TEXT = "Secondary DNS Server: ";
    private static final String SYNCH_STATUS_TEXT = "DSL synchronization status: ";
    private static final String UP_RATE_TEXT = "Upstream line rate: ";
    private static final String DOWN_RATE_TEXT = "Downstream line rate: ";
    private static final String UP_NOISE_TEXT = "Upstream noise safety coefficient: ";
    private static final String DOWN_NOISE_TEXT = "Downstream noise safety coefficient: ";
    private static final String UP_INTERLEAVE_TEXT = "Upstream interleave depth: ";
    private static final String DOWN_INTERLEAVE_TEXT = "Downstream interleave depth: ";
    private static final String LINE_STANDARD_TEXT = "Line standard: ";
    private static final String UP_ATTENUATION_TEXT = "Upstream line attenuation: ";
    private static final String DOWN_ATTENUATION_TEXT = "Downstream line attenuation: ";
    private static final String UP_POWER_TEXT = "Upstream output power: ";
    private static final String DOWN_POWER_TEXT = "Downstream output power: ";
    private static final String CHANNEL_TYPE_TEXT = "Channel type: ";
    private static final String UPTIME_TEXT = "DSL up-time: ";


    /**
     * Formats internet connection info ready to be displayed in TextView.
     * @param wanInfo JSON response from defaultwaninfo endpoint
     * @param dslInfo JSON response from dslinfo endpoint
     * @return Formatted string
     * @throws JSONException
     */
    public static SpannableStringBuilder buildString(JSONObject wanInfo, JSONObject dslInfo) throws JSONException {
        return getConnectionStatus(dslInfo).append(formatWanInfo(wanInfo)).append(formatDslInfo(dslInfo));
    }

    private static SpannableStringBuilder formatWanInfo(JSONObject data) throws JSONException {
        final String LINEBREAK = "\n\n";

        SpannableStringBuilder builder = new SpannableStringBuilder();

        String[] dns = data.getString(DNS).split(",");

        appendBold(builder, IP_TEXT);
        builder.append(data.getString(EXTERNAL_IP)).append(LINEBREAK);

        appendBold(builder, PRIMARY_DNS_TEXT);
        builder.append(dns[0]).append(LINEBREAK);

        appendBold(builder, SECONDARY_DNS_TEXT);
        builder.append(dns[1]).append(LINEBREAK);

        return builder;
    }

    private static SpannableStringBuilder getConnectionStatus(JSONObject data) throws JSONException {
        final String LINEBREAK = "\n\n";

        SpannableStringBuilder builder = new SpannableStringBuilder();

        appendBold(builder, SYNCH_STATUS_TEXT);
        builder.append(data.getString(STATUS)).append(LINEBREAK);

        // TODO: Fix me
        // Router displays a second connection status, but I haven't found this in any of
        // the endpoint responses. Typically would be "Showtime" which seems to be an ADSL thing
        // status += "Connection status: ";

        return builder;
    }

    private static SpannableStringBuilder formatDslInfo(JSONObject data) throws JSONException {
        final String LINEBREAK = "\n\n";
        final String KBITS = "kbit/s";
        final String DB = "dB";
        final String DBM = "dBm";

        SpannableStringBuilder builder = new SpannableStringBuilder();

        appendBold(builder, UP_RATE_TEXT);
        builder.append(data.getString(UP_RATE)).append(KBITS).append(LINEBREAK);

        appendBold(builder, DOWN_RATE_TEXT);
        builder.append(data.getString(DOWN_RATE)).append(KBITS).append(LINEBREAK);

        appendBold(builder, UP_NOISE_TEXT);
        builder.append(data.getString(UP_NOISE)).append(DB).append(LINEBREAK);

        appendBold(builder, DOWN_NOISE_TEXT);
        builder.append(data.getString(DOWN_NOISE)).append(DB).append(LINEBREAK);

        appendBold(builder, UP_INTERLEAVE_TEXT);
        builder.append(data.getString(UP_INTERLEAVE)).append(LINEBREAK);

        appendBold(builder, DOWN_INTERLEAVE_TEXT);
        builder.append(data.getString(DOWN_INTERLEAVE)).append(LINEBREAK);

        appendBold(builder, LINE_STANDARD_TEXT);
        builder.append(data.getString(LINE_STANDARD)).append(LINEBREAK);

        appendBold(builder, UP_ATTENUATION_TEXT);
        builder.append(data.getString(UP_ATTENUATION)).append(DB).append(LINEBREAK);

        appendBold(builder, DOWN_ATTENUATION_TEXT);
        builder.append(data.getString(DOWN_ATTENUATION)).append(DB).append(LINEBREAK);

        appendBold(builder, UP_POWER_TEXT);
        builder.append(data.getString(UP_POWER)).append(DBM).append(LINEBREAK);

        appendBold(builder, DOWN_POWER_TEXT);
        builder.append(data.getString(DOWN_POWER)).append(DBM).append(LINEBREAK);

        appendBold(builder, CHANNEL_TYPE_TEXT);
        builder.append(data.getString(CHANNEL_TYPE)).append(LINEBREAK);

        appendBold(builder, UPTIME_TEXT);
        builder.append(UptimeFormatter.getString(data.getInt(UPTIME))).append(LINEBREAK);

        return builder;
    }


    private static void appendBold(SpannableStringBuilder builder, String text) {
        // Using older method of appending text for compability reasons.
        // (newer Android versions can pass the StyleSpan with the call to append())
        builder.append(text);
        builder.setSpan(new StyleSpan(Typeface.BOLD),
                builder.length() - text.length(),
                builder.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
}
