package cloud.com.redcircle;

import android.app.Activity;
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
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import cloud.com.redcircle.api.HttpRequestHandler;
import cloud.com.redcircle.api.RedCircleManager;
import cloud.com.redcircle.utils.TimeCount;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import info.hoang8f.widget.FButton;

/**
 * Created by zhan on 16/5/10.
 */
public class AddFriendActivity extends BaseActivity  {

    private EditText mPhone;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_friend);

        setTitle("朋友信息");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true); // 决定左上角图标的右侧是否有向左的小箭头, true

        mPhone = (EditText) findViewById(R.id.register_friend_phone_edit1);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.do_finish:
                this.doAddFriend();
                break;
            case android.R.id.home:// 点击返回图标事件
                this.finish();
                break;

        }

        return super.onOptionsItemSelected(item);
    }


    private void doAddFriend() {


        String mePhone = null;
        try {
            mePhone = mUser.getString("mePhone");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        RedCircleManager.addFriend(this, mePhone, mPhone.getText().toString(), new HttpRequestHandler<JSONObject>() {
            @Override
            public void onSuccess(JSONObject data) {
                setResult(Activity.RESULT_OK);
                finish();
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
