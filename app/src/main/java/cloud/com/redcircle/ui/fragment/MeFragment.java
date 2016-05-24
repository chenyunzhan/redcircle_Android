package cloud.com.redcircle.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;

import cloud.com.redcircle.LoginActivity;
import cloud.com.redcircle.ModifyActivity;
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
    private TextView sexTextView;


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
        sexTextView = (TextView) rootView.findViewById(R.id.sex_txt);

        RelativeLayout phoneCell = (RelativeLayout)rootView.findViewById(R.id.name_layout);
        RelativeLayout sexCell = (RelativeLayout)rootView.findViewById(R.id.sex_layout);



        logoutTextView.setOnClickListener(this);
        phoneCell.setOnClickListener(this);
        sexCell.setOnClickListener(this);

        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            this.initData();
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
            case R.id.name_layout:
                Intent intent = new Intent(getActivity(), ModifyActivity.class);
                intent.putExtra("name", "姓名");
                startActivityForResult(intent,0);
                break;
            case R.id.sex_layout:
                Intent intent1 = new Intent(getActivity(), ModifyActivity.class);
                intent1.putExtra("name", "性别");
                startActivityForResult(intent1,0);
                break;
        }

    }

    private void initData() {
        if (mIsLogin) {
            try {
                phoneNumberTextView.setText(mUser.getString("mePhone"));
                usernameTextView.setText(mUser.getString("name"));
                sexTextView.setText(mUser.getString("sex"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
