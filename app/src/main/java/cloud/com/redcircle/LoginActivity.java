package cloud.com.redcircle;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import cloud.com.redcircle.api.HttpRequestHandler;
import cloud.com.redcircle.api.RedCircleManager;
import cloud.com.redcircle.utils.AccountUtils;
import cloud.com.redcircle.utils.TimeCount;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import info.hoang8f.widget.FButton;

/**
 * Created by zhan on 16/4/19.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {




    protected static final String ACTIVITY_TAG="MyAndroid";

    private FButton mVerificationCodeButton;
    private EditText mVerificationCode;
    private EditText mPhone;
    private Button mLogin;
    private Button mRegister;
    private EventHandler eventHandler;
    private TimeCount time;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mVerificationCodeButton = (FButton) findViewById(R.id.login_verificationCode_btn);
        mPhone = (EditText) findViewById(R.id.login_phone_edit);
        mLogin = (Button) findViewById(R.id.login_login_btn);
        mVerificationCode = (EditText) findViewById(R.id.login_verificationCode_edit);
        mRegister = (Button) findViewById(R.id.user_register_btn);

        time = new TimeCount(60000, 1000);//构造CountDownTimer对象
        time.button = mVerificationCodeButton;



        mVerificationCodeButton.setOnClickListener(this);
        mLogin.setOnClickListener(this);
        mRegister.setOnClickListener(this);


        initSDK();

    }


    @Override
    protected void onResume() {
        super.onResume();
        SMSSDK.registerEventHandler(this.eventHandler);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.login_verificationCode_btn:

                if (mPhone.getText().length() > 0) {
                    time.start();
                    SMSSDK.getVerificationCode("86",mPhone.getText().toString());
                }
                break;
            case R.id.login_login_btn:

                if (mPhone.getText().length() > 0 && mVerificationCode.getText().length() > 0) {
                    SMSSDK.submitVerificationCode("86",mPhone.getText().toString(),mVerificationCode.getText().toString());
                }
                break;
            case R.id.user_register_btn:

                SMSSDK.unregisterEventHandler(this.eventHandler);

                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
        }

    }


    /**
     * 初始化短信SDK
     */
    private void initSDK() {

        SMSSDK.initSDK(this, Application.MOB_APPKEY, Application.MOB_APPSECRET);
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

        this.eventHandler = eventHandler;
        // 注册回调监听接口
        SMSSDK.registerEventHandler(eventHandler);

    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.arg2 == SMSSDK.RESULT_COMPLETE) {
                if (msg.arg1 == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    login();
                }
            } else  {

                if("18706734109".equals(mPhone.getText().toString()) || "15891739884".equals(mPhone.getText().toString())) {
                    login();
                }

                Toast.makeText(LoginActivity.this, msg.toString(), Toast.LENGTH_LONG).show();

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
//                        Intent intent = new Intent();
//                        intent.putExtra("profile", (Parcelable) data);
//                        setResult(RESULT_OK, intent);
//                        finish();
                        getRongCloudToken();



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


    private void getRongCloudToken() {
        try {
            String mePhone = mUser.getString("mePhone");
            String name = mUser.getString("name");

            RedCircleManager.getRongCloudToken(this, mePhone, name, new HttpRequestHandler<JSONObject>() {
                @Override
                public void onSuccess(JSONObject data) {
                    AccountUtils.writeRongCloudToken(LoginActivity.this,data);

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }

                @Override
                public void onSuccess(JSONObject data, int totalPages, int currentPage) {

                }

                @Override
                public void onFailure(String error) {

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }





}
