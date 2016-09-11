package com.arenas.droidfan.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import com.arenas.droidfan.Util.Utils;
import com.arenas.droidfan.api.Api;
import com.arenas.droidfan.api.ApiException;
import com.arenas.droidfan.AppContext;
import com.arenas.droidfan.R;
import com.arenas.droidfan.data.model.UserModel;
import com.arenas.droidfan.main.MainActivity;

import org.oauthsimple.model.OAuthToken;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements OnClickListener{

    private static final String TAG = LoginActivity.class.getSimpleName();
    private UserLoginTask mAuthTask = null;

    // UI references.
    @BindView(R.id.account) EditText mAccount;
    @BindView(R.id.password) EditText mPassword;
    @BindView(R.id.login_progress) ProgressBar mProgressView;
    @BindView(R.id.login_button) Button loginButton;
    @BindView(R.id.login_layout) LinearLayout loginLayout;
    @BindView(R.id.version_code) TextView versionCode;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        if (AppContext.isVerified()){
            showHomePage();
        }
    }

    private void init(){
        mContext = this;
        ButterKnife.bind(this);

        loginButton.setOnClickListener(this);
        versionCode.setText(Utils.getVersionCode());
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.login_button){
            attemptLogin();
        }
    }

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        String account = mAccount.getText().toString();
        String password = mPassword.getText().toString();

        if (account.trim().isEmpty() || password.trim().isEmpty()){
            Toast.makeText(this , "用户名或者密码不能为空" , Toast.LENGTH_SHORT).show();
            return;
        }

        Utils.hideKeyboard(LoginActivity.this , mPassword);
        showProgress(true);

        mAuthTask = new UserLoginTask(account, password);
        mAuthTask.execute();
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        loginLayout.setVisibility(show ? View.GONE : View.VISIBLE);
        loginLayout.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                loginLayout.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void showHomePage(){
        Intent intent = new Intent(mContext , MainActivity.class);
        startActivity(intent);
        finish();
    }
    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Integer, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                final Api api = AppContext.getApi();
                OAuthToken token = api.getOAuthAccessToken(mEmail, mPassword);
                if (token != null){
                    publishProgress();
                    AppContext.updateAccessToken(mContext , token);
                    final UserModel u = api.verifyCredentials();
                    if (u != null){
                        AppContext.updateUserInfo(mContext , u);
                        AppContext.updateLoginInfo(mContext , mEmail , mPassword);
                        return true;
                    }else {
                        AppContext.clearAccountInfo(mContext);
                        return false;
                    }
                }else {
                    Log.d(TAG , "token = null , login failed");
                    return false;
                }
            } catch (IOException e) {
                Log.d(TAG , "IOException");
                return false;
            } catch (ApiException e){
                Log.d(TAG , "ApiException : " + e.toString());
                return false;
            } catch (Exception e){
                Log.d(TAG , "Exception");
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                Log.d(TAG , "onPostExecute success");
                AppContext.setFirstLoad(true);
                showHomePage();
                finish();
            } else {
                Log.d(TAG , "onPostExecute failed");
                Utils.showToast(mContext , "密码或者账号错误");
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

