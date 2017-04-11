package com.sonicmax.tt_hg633helper.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.sonicmax.tt_hg633helper.R;
import com.sonicmax.tt_hg633helper.loaders.AccountManager;
import com.sonicmax.tt_hg633helper.ui.ProgressDialogHandler;
import com.sonicmax.tt_hg633helper.network.CsrfHolder;
import com.sonicmax.tt_hg633helper.utilities.SharedPreferenceManager;

public class LoginFragment extends Fragment implements AccountManager.EventInterface {

    public LoginFragment() {}

    private final String LOG_TAG = LoginFragment.class.getSimpleName();

    private EditText mUsername;
    private EditText mPassword;
    private AccountManager mAccountManager;

    ///////////////////////////////////////////////////////////////////////////
    // Fragment methods
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAccountManager = new AccountManager(getContext(), this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        mUsername = (EditText) rootView.findViewById(R.id.login_username);
        mPassword = (EditText) rootView.findViewById(R.id.login_password);
        Button loginButton = (Button) rootView.findViewById(R.id.login_button);

        // Insert username and password from SharedPreferences (if they exist)
        String username = SharedPreferenceManager.getString(getActivity(), "username");
        String password = SharedPreferenceManager.getString(getActivity(), "password");

        if (username != null & password != null) {
            mUsername.setText(username);
            mPassword.setText(password);
        }

        loginButton.setOnClickListener(loginHandler);

        return rootView;
    }

    @Override
    public void onDetach() {
        // Make sure that we don't leak progress dialog when exiting activity
        ProgressDialogHandler.dismissDialog();
        super.onDetach();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Helper methods
    ///////////////////////////////////////////////////////////////////////////

    private View.OnClickListener loginHandler = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.login_button:
                    makeLoginRequest();
                    break;
            }
        }
    };

    private void makeLoginRequest() {
        Context context = getContext();

        ProgressDialogHandler.showDialog(getContext(), "Logging in...");

        String username = mUsername.getText().toString();
        String password = mPassword.getText().toString();

        // Store credentials for later use
        SharedPreferenceManager.putString(context, "username", username);
        SharedPreferenceManager.putString(context, "password", password);

        mAccountManager.setUsername(username)
                .setPassword(password)
                .getCsrfAndLogin();
    }

    @Override
    public void onScrapeCsrf(CsrfHolder csrf) {

    }

    @Override
    public void onHaveIntent(Intent intent) {
        ProgressDialogHandler.dismissDialog();
        getContext().startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }
}
