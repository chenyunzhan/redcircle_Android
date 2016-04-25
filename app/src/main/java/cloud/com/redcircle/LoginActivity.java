package cloud.com.redcircle;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONObject;

import cloud.com.redcircle.api.HttpRequestHandler;
import cloud.com.redcircle.api.RedCircleManager;
import cloud.com.redcircle.utils.AccountUtils;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by zhan on 16/4/19.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {



    private static final String APPKEY = "11f391ad7b6fd";
    private static final String APPSECRET = "2d111f4bb7f9cd61815ca519bb63a2fc";
    protected static final String ACTIVITY_TAG="MyAndroid";

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
        mLogin.setOnClickListener(this);


        initSDK();

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


    /**
     * 初始化短信SDK
     */
    private void initSDK() {

        SMSSDK.initSDK(this, APPKEY, APPSECRET);
        EventHandler eventHandler = new EventHandler() {
            /**
             * 在操作之后被触发
             *
             * @param event
             *            参数1
             * @param result
             *            参数2 SMSSDK.RESULT_COMPLETE表示操作成功，为SMSSDK.
             *            RESULT_ERROR表示操作失败
             * @param data
             *            事件操作的结果
             */
            @Override
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);
            }
        };
        // 注册回调监听接口
        SMSSDK.registerEventHandler(eventHandler);

    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.arg2 == SMSSDK.RESULT_COMPLETE) {
                if (msg.arg1 == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {

                    login();
//                    Intent intent = new Intent(LoginActivity.this,
//                            MainActivity.class);
//                    startActivity(intent);
                }
            } else  {
                login();
                ((Throwable)msg.obj).printStackTrace();
            }
        }
    };


    private void login() {
        showProgressBar(true, getString(R.string.login_loging));

        RedCircleManager.loginWithUsername(this,
                mPhone.getText().toString(),
                mVerificationCode.getText().toString(),
                new HttpRequestHandler<JSONObject>() {

                    @Override
                    public void onSuccess(JSONObject data) {
                        showProgressBar(false);
                        AccountUtils.writeLoginMember(LoginActivity.this, data, true);
                        Intent intent = new Intent();
                        intent.putExtra("profile", (Parcelable) data);
                        setResult(RESULT_OK, intent);
                        finish();
                    }

                    @Override
                    public void onSuccess(JSONObject data, int totalPages, int currentPage) {

                    }

                    @Override
                    public void onFailure(String error) {
//                        MessageUtils.showErrorMessage(LoginActivity.this, error);
                        showProgressBar(false);
                    }
                });
    }







}
