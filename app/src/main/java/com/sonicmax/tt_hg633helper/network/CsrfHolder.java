package com.sonicmax.tt_hg633helper.network;

public class CsrfHolder {
    private String param;
    private String token;

    public CsrfHolder() {}

    public void setParam(String param) {
        this.param = param;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getParam() {
        return param;
    }

    public String getToken() {
        return token;
    }

    public String toString() {
        return "{\"csrf\":{"
                + "\"csrf_param\":\"" + param + "\", "
                + "\"csrf_token\":\"" + token + "\"}}";
    }
}
