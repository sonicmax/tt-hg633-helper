package com.sonicmax.tt_hg633helper.loaders;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Base64;
import android.util.Log;

import com.sonicmax.tt_hg633helper.activities.LoginActivity;
import com.sonicmax.tt_hg633helper.activities.MainActivity;
import com.sonicmax.tt_hg633helper.network.WebRequest;
import com.sonicmax.tt_hg633helper.utilities.ApiPathManager;
import com.sonicmax.tt_hg633helper.utilities.CsrfHolder;
import com.sonicmax.tt_hg633helper.utilities.CsrfScraper;
import com.sonicmax.tt_hg633helper.utilities.SharedPreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Class which allows us to perform account-related actions
 */

public class AccountManager implements LoaderManager.LoaderCallbacks<Object> {
    private final String LOG_TAG = AccountManager.class.getSimpleName();
    private final int GET_CSRF = -1; // -1 to prevent conflicts with ApiRequestHandler
    private final int GET_CSRF_AND_LOGIN = -2;
    private final int LOGIN = -3;
    private final int LOGOUT = -4;

    private String mUsername;
    private String mPassword;
    private String mHostname;

    private Context mContext;
    private EventInterface mEventInterface;
    private LoaderManager mLoaderManager;
    private boolean mShouldPulse = true;

    public AccountManager(Context context, EventInterface eventInterface) {
        mContext = context;
        mEventInterface = eventInterface;
        mLoaderManager = ((FragmentActivity) mContext).getSupportLoaderManager();
        mHostname = "http://192.168.1.1";
    }

    public interface EventInterface {
        void onHaveIntent(Intent intent);
        void onScrapeCsrf(CsrfHolder csrf);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Public methods for logging in, logging out, etc
    ///////////////////////////////////////////////////////////////////////////

    public AccountManager setUsername(String username) {
        mUsername = username;
        return this;
    }

    public AccountManager setPassword(String password) {
        mPassword = password;
        return this;
    }

    public void getCsrfFields() {
        Bundle args = new Bundle(1);
        args.putString("url", mHostname);

        mLoaderManager.initLoader(GET_CSRF, args, this).forceLoad();
    }

    public void getCsrfAndLogin() {
        Bundle args = new Bundle(1);
        args.putString("url", mHostname);

        mLoaderManager.initLoader(GET_CSRF_AND_LOGIN, args, this).forceLoad();
    }

    private void loginWithCsrf(CsrfHolder csrf) {
        try {
            JSONObject payload = buildLoginPayload(mUsername, mPassword,
                    csrf.getParam(), csrf.getToken());

            Bundle args = new Bundle(2);
            args.putString("url", mHostname + ApiPathManager.getFullPath("user_login"));
            args.putString("payload", payload.toString());

            mLoaderManager.initLoader(LOGIN, args, this).forceLoad();

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error building payload for getCsrfAndLogin request", e);
        }
    }

    private JSONObject buildLoginPayload(String username, String password,
                                         String csrf_param, String csrf_token) throws JSONException {

        JSONObject csrf = new JSONObject();
        csrf.put("csrf_param", csrf_param);
        csrf.put("csrf_token", csrf_token);

        JSONObject data = new JSONObject();
        data.put("UserName", "admin");
        data.put("Password", createPasswordHash(username, password, csrf_param, csrf_token));

        JSONObject payload = new JSONObject();
        payload.put("data", data);
        payload.put("csrf", csrf);

        return payload;
    }

    /**
     * Replicates some of the logic in Atp.LoginController.postData() required for use user_login API
     * @return SHA256 hash of (username + base64 encoded hash of password + csrf_param + csrf_token)
     */

    private String createPasswordHash(String username, String password, String csrf_param, String csrf_token) {
        try {
            String passwordHash = computeHash(password);
            byte[] hashBytes = passwordHash.getBytes("UTF-8");
            String firstStep = username + Base64.encodeToString(hashBytes, Base64.NO_WRAP) + csrf_param + csrf_token;

            return computeHash(firstStep);

        } catch (NoSuchAlgorithmException e) {
            Log.e(LOG_TAG, e.getMessage());
            return null;

        } catch (UnsupportedEncodingException e) {
            Log.e(LOG_TAG, e.getMessage());
            return null;
        }
    }

    private String computeHash(String input) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.reset();

        byte[] byteData = digest.digest(input.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();

        for (byte b : byteData) {
            sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Loader callbacks
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public Loader<Object> onCreateLoader(int id, final Bundle args) {
        switch (id) {
            case GET_CSRF:
            case GET_CSRF_AND_LOGIN:
                 return new AsyncLoader(mContext, args) {
                    @Override
                    public String loadInBackground() {
                        return new WebRequest(mContext).get(args.getString("url"));
                    }
                };

            // Allow cases to fallthrough
            case LOGIN:
            case LOGOUT:

                return new AsyncLoader(mContext, args) {
                    @Override
                    public String loadInBackground() {
                        return new WebRequest(mContext)
                                .post(args.getString("url"), args.getString("payload"));
                    }
                };

            default:
                Log.e(LOG_TAG, "Couldn't find loader with id " + id);
                return null;
        }
    }

        @Override
        public void onLoadFinished(Loader<Object> loader, Object data) {
            Intent intent = null;
            String response;
            CsrfHolder csrf;

            switch (loader.getId()) {
                case GET_CSRF:
                    response = (String) data;
                    csrf = CsrfScraper.scrapeCsrfFields(response);
                    mEventInterface.onScrapeCsrf(csrf);
                    break;

                case GET_CSRF_AND_LOGIN:
                    response = (String) data;
                    csrf = CsrfScraper.scrapeCsrfFields(response);
                    loginWithCsrf(csrf);
                    break;

                case LOGOUT:
                    SharedPreferenceManager.putBoolean(mContext, "is_logged_in", false);
                    intent = new Intent(mContext, LoginActivity.class);
                    break;

                case LOGIN:
                    SharedPreferenceManager.putBoolean(mContext, "is_logged_in", true);
                    intent = new Intent(mContext, MainActivity.class);
                    break;

                default:
                    Log.w(LOG_TAG, "No case for loader ID: " + loader.getId());
                    break;
            }

            if (intent != null) {
                mEventInterface.onHaveIntent(intent);
            }
        }

        @Override
        public void onLoaderReset(Loader<Object> loader) {
            loader.reset();
        }
}
