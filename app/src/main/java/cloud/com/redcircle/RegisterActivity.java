package cloud.com.redcircle;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONObject;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by zhan on 16/5/10.
 */
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private Menu mMenu;
    private Button mVerificationCodeButton;
    private EditText mVerificationCode;
    private EditText mPhone;

    private EventHandler eventHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setTitle("注册");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true); // 决定左上角图标的右侧是否有向左的小箭头, true



        mVerificationCode = (EditText) findViewById(R.id.register_verificationCode_edit);
        mPhone = (EditText) findViewById(R.id.register_phone_edit);
        mVerificationCodeButton = (Button) findViewById(R.id.register_verificationCode_btn);


        mVerificationCodeButton.setOnClickListener(this);

        initSDK();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_register, menu);
        this.mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.next_step_action:
                if (mPhone.getText().length() > 0 && mVerificationCode.getText().length() > 0) {
                    SMSSDK.submitVerificationCode("86",mPhone.getText().toString(),mVerificationCode.getText().toString());
                }
                break;
            case android.R.id.home:// 点击返回图标事件
                this.finish();
                break;


        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_verificationCode_btn:
                if (mPhone.getText().length() > 0) {
                    SMSSDK.getVerificationCode("86",mPhone.getText().toString());
                }
                break;

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

                    SMSSDK.unregisterEventHandler(RegisterActivity.this.eventHandler);
                    Intent intent = new Intent(RegisterActivity.this, FriendActivity.class);
                    intent.putExtra("meInfo","{\"me_phone\":\" " + mPhone.getText().toString() + " \"}");
                    startActivity(intent);
                }

            } else  {


//                Intent intent = new Intent(RegisterActivity.this, FriendActivity.class);
//                intent.putExtra("meInfo","{\"me_phone\":\" " + mPhone.getText().toString() + " \"}");
//                startActivity(intent);

                ((Throwable)msg.obj).printStackTrace();
            }
        }
    };
}
