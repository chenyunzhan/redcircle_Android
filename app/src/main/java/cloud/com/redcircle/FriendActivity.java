package cloud.com.redcircle;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import cloud.com.redcircle.api.HttpRequestHandler;
import cloud.com.redcircle.api.RedCircleManager;
import cn.smssdk.SMSSDK;

/**
 * Created by zhan on 16/5/11.
 */
public class FriendActivity extends AppCompatActivity {

    private EditText mPhone1;
    private JSONArray mFriendsArray;
    private JSONObject meInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_friend);

        setTitle("朋友信息");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mPhone1 = (EditText) findViewById(R.id.register_friend_phone_edit1);

        mFriendsArray = new JSONArray();

        Bundle extras = getIntent().getExtras();
        String meInfoStr = extras.getString("meInfo");
        try {
            meInfo = new JSONObject(meInfoStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                SMSSDK.unregisterAllEventHandler();
                this.finish();
                break;
            case R.id.do_finish_register:
                if (mPhone1.getText().length() > 0) {
                    this.doRegister(mPhone1.getText().toString());
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void doRegister(String phone_text) {
        try {
            Boolean isContain = false;
            for (int i=0; i<mFriendsArray.length(); i++) {
                JSONObject friend = (JSONObject) mFriendsArray.get(i);
                if(friend.toString().contains(phone_text)) {
                    isContain = true;
                    break;
                }
            }
            if (!isContain) {
                mFriendsArray.put(new JSONObject("{\"phone_text\":\"" + phone_text + "\"}"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        if (mFriendsArray.length() == 1) {

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

                    SMSSDK.unregisterAllEventHandler();

                    Toast.makeText(FriendActivity.this, "恭喜注册成功,请进行登录", Toast.LENGTH_LONG).show();


                    Intent intent = new Intent(FriendActivity.this, LoginActivity.class);
                    startActivity(intent);
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
}
