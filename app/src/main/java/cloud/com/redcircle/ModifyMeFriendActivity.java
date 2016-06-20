package cloud.com.redcircle;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import org.json.JSONObject;

import cloud.com.redcircle.api.HttpRequestHandler;
import cloud.com.redcircle.api.RedCircleManager;
import cloud.com.redcircle.utils.AccountUtils;

/**
 * Created by zhan on 16/5/24.
 */
public class ModifyMeFriendActivity extends BaseActivity {


    private EditText editText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);

        setTitle("朋友推荐语");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        editText = (EditText)findViewById(R.id.edit_text);
        editText.setTextSize(12);
        editText.setHint("请用一句话来描述朋友和你的关系及推荐语");


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add("完成").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:// 点击返回图标事件
                this.finish();
                break;
            case 0:
                this.doModifyAction();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void doModifyAction() {


        if (editText.getText().length() == 0) {
            return;
        }


        String mePhone =getIntent().getStringExtra("mePhone");
        String friendPhone =getIntent().getStringExtra("friendPhone");


        RedCircleManager.modifyDetailUser(this, mePhone, friendPhone, editText.getText().toString(), new HttpRequestHandler<JSONObject>() {
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
