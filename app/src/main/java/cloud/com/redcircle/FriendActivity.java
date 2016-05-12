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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cloud.com.redcircle.api.HttpRequestHandler;
import cloud.com.redcircle.api.RedCircleManager;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by zhan on 16/5/11.
 */
public class FriendActivity extends AppCompatActivity implements View.OnClickListener {


    private Button mVerificationCodeButton1;
    private EditText mVerificationCode1;
    private EditText mPhone1;
    private Button mVerificationCodeButton2;
    private EditText mVerificationCode2;
    private EditText mPhone2;


    private ArrayList<JSONObject> mFriendsArray;
    private JSONObject meInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_friend);

        setTitle("朋友信息");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mPhone1 = (EditText) findViewById(R.id.register_friend_phone_edit1);
        mPhone2 = (EditText) findViewById(R.id.register_friend_phone_edit2);

        mVerificationCode1 = (EditText) findViewById(R.id.register_friend_verificationCode_edit1);
        mVerificationCode2 = (EditText) findViewById(R.id.register_friend_verificationCode_edit1);

        mVerificationCodeButton1 = (Button) findViewById(R.id.register_friend_verificationCode_btn1);
        mVerificationCodeButton2 = (Button) findViewById(R.id.register_friend_verificationCode_btn2);

        mFriendsArray = new ArrayList<JSONObject>();

        Bundle extras = getIntent().getExtras();
        String meInfoStr = extras.getString("meInfo");
        try {
            meInfo = new JSONObject(meInfoStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mVerificationCodeButton1.setOnClickListener(this);
        mVerificationCodeButton2.setOnClickListener(this);

        initSDK();



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_friend_register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:// 点击返回图标事件
                this.finish();
                break;
            case R.id.do_finish_register:
                if (mPhone1.getText().length() > 0 && mVerificationCode1.getText().length() > 0) {
                    SMSSDK.submitVerificationCode("86",mPhone1.getText().toString(),mVerificationCode1.getText().toString());
                }
//                if (mPhone2.getText().length() > 0 && mVerificationCode2.getText().length() > 0) {
//                    SMSSDK.submitVerificationCode("86",mPhone2.getText().toString(),mVerificationCode2.getText().toString());
//                }
                break;


        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_friend_verificationCode_btn1:
                if (mPhone1.getText().length() > 0) {
                    SMSSDK.getVerificationCode("86",mPhone1.getText().toString());
                }
                break;
            case R.id.register_friend_verificationCode_btn2:
                if (mPhone2.getText().length() > 0) {
                    SMSSDK.getVerificationCode("86",mPhone2.getText().toString());
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
        // 注册回调监听接口
        SMSSDK.registerEventHandler(eventHandler);

    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.arg2 == SMSSDK.RESULT_COMPLETE) {
                if (msg.arg1 == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    try {
                        HashMap map = (HashMap) msg.obj;
                        String phone_text = (String) map.get("phone");


                        Boolean isContain = false;

                        for (int i=0; i<mFriendsArray.size(); i++) {
                            JSONObject friend = mFriendsArray.get(i);
                            if(friend.toString().contains(phone_text)) {
                                isContain = true;
                                break;
                            }
                        }
                        if (!isContain) {
                            mFriendsArray.add(new JSONObject("{\"phone_text\":\"" + phone_text + "\"}"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    if (mFriendsArray.size() == 1) {

                        JSONObject friendsJsonObject = new JSONObject();
                        try {
                            friendsJsonObject.put("meInfo",meInfo);
                            friendsJsonObject.put("friendArrayMap",mFriendsArray);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        RedCircleManager.registerAccount(FriendActivity.this, friendsJsonObject, new HttpRequestHandler<JSONObject>() {
                            @Override
                            public void onSuccess(JSONObject data) {

                            }

                            @Override
                            public void onSuccess(JSONObject data, int totalPages, int currentPage) {

                            }

                            @Override
                            public void onFailure(String error) {

                            }
                        });
                    }

                }

            } else  {

                ((Throwable)msg.obj).printStackTrace();
            }
        }
    };
}
