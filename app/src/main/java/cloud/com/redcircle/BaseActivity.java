package cloud.com.redcircle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import cloud.com.redcircle.utils.AccountUtils;

/**
 * Created by zhan on 16/4/21.
 */
public class BaseActivity extends AppCompatActivity implements AccountUtils.OnAccountListener {

    protected boolean mIsLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIsLogin = AccountUtils.isLogined(this);
        if (mIsLogin) {
//            mLoginProfile = AccountUtils.readLoginMember(this);
        } else {
            AccountUtils.registerAccountListener(this);

            Intent intent2 = new Intent(BaseActivity.this, LoginActivity.class);
            startActivity(intent2);
        }
    }

    @Override
    public void onLogout() {
        mIsLogin = false;
    }


}
