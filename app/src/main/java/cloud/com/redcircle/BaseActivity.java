package cloud.com.redcircle;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.json.JSONObject;

import cloud.com.redcircle.utils.AccountUtils;

/**
 * Created by zhan on 16/4/21.
 */
public class BaseActivity extends AppCompatActivity implements AccountUtils.OnAccountListener {

    protected boolean mIsLogin;
    private ProgressDialog mProgressDialog;
    protected JSONObject mUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        AccountUtils.removeAll(this);

        mIsLogin = AccountUtils.isLogined(this);
        if (mIsLogin) {
            mUser = AccountUtils.readLoginMember(this);
        } else {
            AccountUtils.registerAccountListener(this);

            if (!(this instanceof LoginActivity)) {
                Intent intent2 = new Intent(BaseActivity.this, LoginActivity.class);
                startActivity(intent2);
            }

        }
    }

    @Override
    public void onLogout() {
        mIsLogin = false;
    }

    @Override
    public void onLogin(JSONObject member) {
        mIsLogin = true;
        mUser = member;

    }


    public void showProgressBar(boolean show) {
        showProgressBar(show, "");
    }

    private void initProgressBar() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
        }
    }

    public void showProgressBar(boolean show, String message) {
        initProgressBar();
        if (show) {
            mProgressDialog.setMessage(message);
            mProgressDialog.show();
        } else {
            mProgressDialog.hide();
        }
    }

    public void showProgressBar(int messageId) {
        String message = getString(messageId);
        showProgressBar(true, message);
    }

}
