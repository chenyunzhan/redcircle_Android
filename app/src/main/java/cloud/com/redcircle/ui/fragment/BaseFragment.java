package cloud.com.redcircle.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import org.json.JSONObject;

import cloud.com.redcircle.utils.AccountUtils;

/**
 * Created by yw on 2015/5/3.
 */
public class BaseFragment extends Fragment implements AccountUtils.OnAccountListener{

    protected boolean mIsLogin;

    protected JSONObject mUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mIsLogin = AccountUtils.isLogined(getActivity());
        if (mIsLogin)
            mUser = AccountUtils.readLoginMember(getActivity());
        AccountUtils.registerAccountListener(this);
    }

    @Override
    public void onLogout() {

    }

    @Override
    public void onLogin(JSONObject member) {

    }
}
