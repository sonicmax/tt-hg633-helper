package com.sonicmax.tt_hg633helper.ui;

import com.sonicmax.tt_hg633helper.R;

import java.util.HashMap;
import java.util.Map;

public class DeviceIconGetter {
    public static final Map<String, Integer> drawableMap;
    public static final String[] deviceTypes = {"msft", "computer", "android", "mobile",
            "phone", "tablet", "kindle", "ipad", "xbox", "stb", "camera", "cctv"};

    static {
        drawableMap = new HashMap<>();
        drawableMap.put("msft", R.drawable.laptop_100);
        drawableMap.put("computer", R.drawable.laptop_100);

        // Note: "pc" can cause wrong icon to be displayed for "dhcpcd-5.2.10" vendor class id.
        // Need to use separate lists for vendor id and hostname checks

        // drawableMap.put("pc", R.drawable.laptop_100);

        drawableMap.put("android", R.drawable.smartphone_100);
        drawableMap.put("mobile", R.drawable.smartphone_100);
        drawableMap.put("phone", R.drawable.smartphone_100);

        drawableMap.put("tablet", R.drawable.tablet_100);
        drawableMap.put("kindle", R.drawable.tablet_100);
        drawableMap.put("ipad", R.drawable.tablet_100);

        drawableMap.put("xbox", R.drawable.game_console_100);
        drawableMap.put("stb", R.drawable.stb_100);
        drawableMap.put("camera", R.drawable.camera_100);
        drawableMap.put("cctv", R.drawable.camera_100);
    }

    public static int checkDevice(String vendorClassId, String hostName) {
        for (String type : deviceTypes) {
            if (vendorClassId.toLowerCase().contains(type)) {
                return drawableMap.get(type);
            }
            else if (hostName.toLowerCase().contains(type)) {
                return drawableMap.get(type);
            }
        }

        return R.drawable.unknown_device_100;
    }

    /*
	    xbox: "game",
	    stb: "stb",
	    camera: "camera",
	    computer: "DesktopComputer",
	    siphone: "siphone",
	    iphone: "smartphone",
	    phone: "smartphone",
	    mobile: "smartphone",
	    laptop: "laptop",
	    ipad: "pad",
	    pad: "pad",
	    android: "smartphone",
	    msft: "DesktopComputer"
     */
}
