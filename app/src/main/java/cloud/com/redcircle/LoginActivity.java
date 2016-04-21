package cloud.com.redcircle;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import cn.smssdk.SMSSDK;

/**
 * Created by zhan on 16/4/19.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mVerificationCodeButton;
    private EditText mVerificationCode;
    private EditText mPhone;
    private Button mLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mVerificationCodeButton = (Button) findViewById(R.id.login_verificationCode_btn);
        mPhone = (EditText) findViewById(R.id.login_phone_edit);
        mLogin = (Button) findViewById(R.id.login_login_btn);
        mVerificationCode = (EditText) findViewById(R.id.login_verificationCode_edit);
        mVerificationCodeButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.login_verificationCode_btn:

                if (mPhone.getText().length() > 0) {
                    SMSSDK.getVerificationCode("86",mPhone.getText().toString());
                }
                break;
            case R.id.login_login_btn:

                if (mPhone.getText().length() > 0 && mVerificationCode.getText().length() > 0) {
                    SMSSDK.submitVerificationCode("86",mPhone.getText().toString(),mVerificationCode.getText().toString());
                }
        }

    }
}
