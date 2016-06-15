package cloud.com.redcircle;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import cloud.com.redcircle.api.HttpRequestHandler;
import cloud.com.redcircle.api.RedCircleManager;
import io.rong.imkit.RongIM;

/**
 * Created by zhan on 16/6/14.
 */
public class UserDetailActivity extends BaseActivity implements View.OnClickListener {

    private String friendPhone;
    private String mePhone;
    protected String friendName;

    private TextView nameTextView;
    private TextView sexTextView;
    private TextView phoneTextView;
    private TextView recommandLanguageView;
    private ImageView photoImageView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        setTitle("详细资料");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        Intent intent = getIntent();
        this.friendPhone = intent.getStringExtra("friendPhone");
        String mePhone = null;


        nameTextView = (TextView) findViewById(R.id.txt_fragment_user_name);
        sexTextView = (TextView) findViewById(R.id.txt_fragment_user_tagline);
        phoneTextView = (TextView) findViewById(R.id.txt_fragment_user_description);
        recommandLanguageView = (TextView) findViewById(R.id.txt_recommand_language);
        final Button sendMessageButton = (Button) findViewById(R.id.send_message_btn);
        final Button dialMobileButton = (Button) findViewById(R.id.dial_mobile_btn);
        photoImageView = (ImageView) findViewById(R.id.header_logo_fragment_user);
        final LinearLayout seePhotoLinearLayout = (LinearLayout) findViewById(R.id.see_photp_layout);
        final LinearLayout addRecommandLayout = (LinearLayout) findViewById(R.id.add_recommand_language_layout);

        sendMessageButton.setOnClickListener(this);
        dialMobileButton.setOnClickListener(this);
        seePhotoLinearLayout.setOnClickListener(this);
        addRecommandLayout.setOnClickListener(this);

        try {
            mePhone = mUser.getString("mePhone");
            this.mePhone = mePhone;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.initData();

    }


    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:// 点击返回图标事件
                this.finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {

            switch (requestCode) {
                case 0:
                    this.initData();
                    break;
                default:
                    break;
            }


        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_message_btn:
                //启动会话界面
                if (RongIM.getInstance() != null) {

                    String itemTitle = null;
                    if (friendName != null && friendName.length() > 0) {
                        itemTitle = friendName;
                    } else {
                        itemTitle = friendPhone;
                    }

                    RongIM.getInstance().startPrivateChat(this, this.friendPhone, itemTitle);

                }

                break;
            case R.id.dial_mobile_btn:

                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + this.friendPhone));
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    startActivity(intent);
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }

                break;
            case R.id.add_recommand_language_layout:
                Intent intent2 = new Intent(this, ModifyMeFriendActivity.class);
                intent2.putExtra("friendPhone",this.friendPhone);
                intent2.putExtra("mePhone",this.mePhone);
                startActivityForResult(intent2,0);

                break;

        }
    }


    public void initData() {
        RedCircleManager.getUserRelationById(this,
                mePhone,
                this.friendPhone,
                new HttpRequestHandler<JSONObject>() {

                    @Override
                    public void onSuccess(JSONObject data) {

                        try {
                            friendName = data.getString("name");

                            nameTextView.setText(data.getString("name"));
                            sexTextView.setText(data.getString("sex"));
                            phoneTextView.setText(data.getString("me_phone"));
                            recommandLanguageView.setText(data.getString("recommend_language"));
                            ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance
                            imageLoader.displayImage(RedCircleManager.HTTP_BASE_URL + "/downPhotoByPhone?mePhone=" + mUser.getString("mePhone"), photoImageView);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
