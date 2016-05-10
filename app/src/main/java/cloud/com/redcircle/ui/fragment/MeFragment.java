package cloud.com.redcircle.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;

import cloud.com.redcircle.R;
import cloud.com.redcircle.api.RedCircleManager;
import cloud.com.redcircle.ui.ButtonAwesome;
import cloud.com.redcircle.ui.TextAwesome;
import cloud.com.redcircle.utils.AccountUtils;

/**
 * Created by zhan on 16/4/25.
 */
public class MeFragment extends BaseFragment implements View.OnClickListener {


    private TextView phoneNumberTextView;
    private TextView usernameTextView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_me, container, false);

        View logoutTextView = rootView.findViewById(R.id.login_logout_txt);
        phoneNumberTextView = (TextView) rootView.findViewById(R.id.phone_number_txt);
        usernameTextView = (TextView) rootView.findViewById(R.id.username_txt);


        logoutTextView.setOnClickListener(this);

        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (mIsLogin) {
            try {
                phoneNumberTextView.setText(mUser.getString("mePhone"));
                usernameTextView.setText(mUser.getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.login_logout_txt:

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.settings_dialog_hint)
                        .setMessage(R.string.settings_logout_or_not)
                        .setPositiveButton(R.string.title_confirm_yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                RedCircleManager.logout(getActivity());
                                AccountUtils.removeAll(getActivity());
                                getActivity().finish();
                            }
                        })
                        .setNegativeButton(R.string.title_confirm_cancel, null).show();

                break;
        }

    }
}
