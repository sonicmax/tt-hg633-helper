package com.sonicmax.tt_hg633helper.utilities;

import android.util.Log;

/**
 * Class which provides methods for convenience when building URLs for API requests.
 * See comments for information on each endpoint.
 *
 * Note: diagnose_internet, logindevice_count, device_count, wizard_wifi and device_info are
 * unauthenticated - all other APIs require valid session token (and CSRF values for POST requests)
 */

public class ApiPathManager {
    private static String LOG_TAG = "ApiPathManager";

    /**
     * Get full path of API using last path segment as shorthand.
     * @param shorthand
     * @return
     */
    public static String getFullPath(String shorthand) {
        final String SYSTEM_API = "/api/system/";
        final String APP_API = "/api/app/";
        final String NETWORK_API = "/api/ntwk/";
        final String SERVICE_API = "/api/service/";

        switch(shorthand) {
            case "user_login": // Checks credentials and responds with session token.
            case "user_logout": // Logs out current user.
            case "heartbeat": // Make requests every 5 secs to keep current session valid.
            case "diagnose_internet": // Provides information on the network configuration (external IP, DNS servers, etc)
            case "logindevice_count": // Provides number of printers on network, USB devices and devices connected to LAN
            case "device_count": // Same as logindevice_count, but includes devices connected to WLAN
            case "wizard_wifi": // Basic information on WLAN
            case "deviceinfo": // Provides device name, serial number, etc
            case "diagnose_ping": // POST to ping server, GET for status updates
            case "diagnose_lan": // Provides detailed information on devices connected to ethernet ports
            case "HostInfo": // Provides detailed information on devices connected to network.
            case "HostInfo?devicetype=wireless": // ^ but only wireless devices
            case "selfhelp": // Shows whether self help is enabled or not
            case "usbdevice": // Untested - presumably provides information on USB devices connected to router
            case "useraccount": // Provides information on user account (username, account level, etc)
            case "getuserlevel": // Reveals whether logged in user is admin. Presumably "isAdmin" is always true
            case "loginfo": // Provides router system logs
            case "downloadcfg": // Downloads encrypted router configuration file.
                return SYSTEM_API + shorthand;

            case "qosclass_host": // ?????
            case "fsstatus": // Provides current file sharing settings
                return APP_API + shorthand;

            case "WlanBasic": // Provides detailed information on the WLAN configuration. Can modify a number of different settings by making a POST request to this endpoint.
            case "dslinfo": // Provides technical information on DSL quality
            case "defaultwaninfo": // Provides external IP and DNS servers
            case "auto_sensing": // Provides information on line standard
            case "wan": // Provides detailed information on WAN status
            case "lan_host": // Provides detailed information on LAN status
            case "lan_info": // Provides LAN statistics
            case "wifi_info": // Provides WIFI statistics
            case "wan_st": // Provides WAN statistics
                return NETWORK_API + shorthand;

            case "cwmp": // Provides information on remote management settings
            case "reboot.cgi": // Reboots router.
                return SERVICE_API + shorthand;

            default:
                Log.e(LOG_TAG, "No API path found for shorthand value \"" + shorthand + "\".");
                return null;
        }
    }
}
