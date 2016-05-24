package cloud.com.redcircle;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.view.menu.MenuItemImpl;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import org.json.JSONObject;

import cloud.com.redcircle.api.HttpRequestHandler;
import cloud.com.redcircle.api.RedCircleManager;
import cloud.com.redcircle.utils.AccountUtils;
import cn.smssdk.SMSSDK;

/**
 * Created by zhan on 16/5/24.
 */
public class ModifyActivity extends BaseActivity {


    private EditText editText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);

        String name =getIntent().getStringExtra("name");
        setTitle("修改"+name);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        editText = (EditText)findViewById(R.id.edit_text);
        editText.setHint("请输入新的"+name);


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add("修改").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
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


        String name =getIntent().getStringExtra("name");


        RedCircleManager.modifyUser(this, name, editText.getText().toString(), new HttpRequestHandler<JSONObject>() {
            @Override
            public void onSuccess(JSONObject data) {
                AccountUtils.writeLoginMember(ModifyActivity.this, data, true);
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
