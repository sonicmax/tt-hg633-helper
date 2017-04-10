package com.sonicmax.tt_hg633helper.network;

import android.content.Context;
import android.util.Log;

import com.sonicmax.tt_hg633helper.utilities.SharedPreferenceManager;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.CacheControl;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WebRequest {
    private final String LOG_TAG = WebRequest.class.getSimpleName();

    private final int STATUS_OK = 200;

    private Context mContext;
    private OkHttpClient mClient;

    public WebRequest(Context context) {
        mContext = context;
        initClient();
    }

    private void initClient() {
        mClient = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .cookieJar(new CookieJar() {

                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                        if (cookies.size() > 0) {
                            storeCookie(cookies.get(0).toString());
                        }
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url) {
                        List<Cookie> cookies = new ArrayList<>();
                        Cookie cookie = null;

                        if (getCookie() != null) {
                            cookie = Cookie.parse(url, getCookie());
                        }

                        if (cookie != null) {
                            cookies.add(cookie);
                        }

                        return cookies;
                    }

                }).build();
    }

    public String get(String url) {
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Connection", "close") // See https://github.com/square/okhttp/issues/2738
                    .build();

            Response response = mClient.newCall(request).execute();

            if (response.code() == STATUS_OK) {
                return response.body().string();

            } else {
                Log.e(LOG_TAG, "Error: status code " + response.code());
                Log.d(LOG_TAG, "URL: " + url);
                return null;
            }

        } catch (SocketTimeoutException e) {
            Log.e(LOG_TAG, e.getMessage(), e.getCause());
            Log.d(LOG_TAG, "URL: " + url);
            return null;

        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e.getCause());
            Log.d(LOG_TAG, "URL: " + url);
            return null;
        }
    }

    public String post(String url, String payload) {
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        try {
            RequestBody body = RequestBody.create(JSON, payload);
            Request request = new Request.Builder()
                    .cacheControl(CacheControl.FORCE_NETWORK)
                    .url(url)
                    .post(body)
                    .build();

            Response response = mClient.newCall(request).execute();

            if (response.code() == STATUS_OK) {
                return response.body().string();

            } else {
                Log.e(LOG_TAG, "Error: status code " + response.code());
                Log.d(LOG_TAG, "URL: " + url);
                return null;
            }

        } catch (SocketTimeoutException e) {
            Log.e(LOG_TAG, e.getMessage(), e.getCause());
            Log.d(LOG_TAG, "URL: " + url);
            return null;

        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e.getCause());
            Log.d(LOG_TAG, "URL: " + url);
            return null;
        }
    }

    private String getCookie() {
        return SharedPreferenceManager.getString(mContext, "cookie");
    }

    private void storeCookie(String cookie) {
        SharedPreferenceManager.putString(mContext, "cookie", cookie);
    }
}